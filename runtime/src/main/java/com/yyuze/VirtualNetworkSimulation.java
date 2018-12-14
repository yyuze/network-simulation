package com.yyuze;

import com.yyuze.anno.system.Command;
import com.yyuze.anno.system.Schedule;
import com.yyuze.enable.Assembleable;
import com.yyuze.enums.LayerType;
import com.yyuze.anno.platform.Layer;
import com.yyuze.system.CommandTask;
import com.yyuze.system.ScheduleTask;
import com.yyuze.tool.Console;
import com.yyuze.tool.Invoker;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Author: yyuze
 * Time: 2018-12-02
 */
public class VirtualNetworkSimulation {

    private HashMap<LayerType, Assembleable> platforms;

    private ArrayList<Object> instances;

    private ArrayList<Class> classes;

    private HashMap<String, Invoker> commandInvokers;

    private HashMap<Long, ArrayList<Invoker>> scheduleInvokers;

    private ThreadPoolExecutor threadPoor;

    private int runningAmount = 0;

    private final ReentrantLock deamonLock = new ReentrantLock();

    private final Condition terminate = deamonLock.newCondition();

    private final Console console = new Console();

    public void start() {
        CommandTask commandTask = new CommandTask(this.deamonLock, terminate, this.commandInvokers, console);
        ScheduleTask scheduleTask = new ScheduleTask(this.deamonLock, terminate, this.scheduleInvokers, console);
        this.startTask(commandTask);
        this.startTask(scheduleTask);
        this.threadPoor.execute(() -> {
            /**
             * deamon thread
             */
            final Lock lock = this.deamonLock;
            lock.lock();
            try {
                terminate.await();
                scheduleTask.terminate();
                commandTask.terminate();
                this.console.write("System is shutting down");
                terminate.signal();
                while (this.runningAmount != 0) {
                    terminate.await();
                    --this.runningAmount;
                }
                this.threadPoor.shutdown();
                this.console.write("good bye");
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                lock.unlock();
            }
        });
    }

    public VirtualNetworkSimulation(Class linkLayer,Class networkLayer,Class tansportLayer,Class applicationLayer) {
        this.platforms = new HashMap<>();
        this.instances = new ArrayList<>();
        this.classes = new ArrayList<>();
        this.commandInvokers = new HashMap<>();
        this.scheduleInvokers = new HashMap<>();
        this.initPlatforms(linkLayer,networkLayer,tansportLayer,applicationLayer);
        this.initCommandInvokers();
        this.initScheduleInvokers();
        this.initThreadPoor();
    }

    private void startTask(Runnable task) {
        this.threadPoor.execute(task);
        ++this.runningAmount;
    }

    private void initPlatforms(Class linkLayer,Class networkLayer,Class tansportLayer,Class applicationLayer) {
        this.instanceLinkLayer(linkLayer);
        this.instanceNetworkLayer(networkLayer);
        this.instanceTransportLayer(tansportLayer);
        this.instanceApplicationLayer(applicationLayer);

    }

    private <L extends EmptyLinkLayerPlatform> void instanceLinkLayer(Class<L> clz) {
        EmptyLinkLayerPlatform.Builder builder = new EmptyLinkLayerPlatform.Builder();
        Assembleable linkLayerPlatform = builder.buildRuntimePlatform(clz);
        LayerType type = linkLayerPlatform.getClass().getAnnotation(Layer.class).value();
        this.platforms.put(type, linkLayerPlatform);
        this.instances.addAll(builder.getInstancesOnPlatform());
        this.classes.addAll(builder.getClassesOnPlatform());
    }

    private <L extends Assembleable> void instanceNetworkLayer(Class<L> clz) {
        //todo 初始化网络层
    }

    private <L extends Assembleable> void instanceTransportLayer(Class<L> clz) {
        //todo 初始化运输层
    }

    private <L extends Assembleable> void instanceApplicationLayer(Class<L> clz) {
        //todo 初始化应用层
    }

    private void initScheduleInvokers() {
        for (Object instance : this.instances) {
            Method[] methods = instance.getClass().getDeclaredMethods();
            for (Method method : methods) {
                Schedule anno = method.getAnnotation(Schedule.class);
                if (anno != null) {
                    long invokePeriod = anno.period();
                    if (!this.scheduleInvokers.containsKey(invokePeriod)) {
                        this.scheduleInvokers.put(invokePeriod, new ArrayList<>());
                    }
                    Invoker invoker = new Invoker(instance, method);
                    this.scheduleInvokers.get(invokePeriod).add(invoker);
                }
            }
        }
    }

    private void initCommandInvokers() {
        this.platforms.forEach((type, platform) -> {
            Method[] methods = platform.getClass().getDeclaredMethods();
            for (Method method : methods) {
                Command anno = method.getAnnotation(Command.class);
                if (anno != null) {
                    Invoker invoker = new Invoker(platform, method);
                    this.commandInvokers.put(anno.value().toString(), invoker);
                }
            }
        });
    }

    private void initThreadPoor() {
        int corePoolSize = 3;
        int maximumPoolSize = 4;
        long keepAliveTime = 500;
        TimeUnit unit = TimeUnit.MILLISECONDS;
        int queueCapacity = 8;
        this.threadPoor = new ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime, unit, new ArrayBlockingQueue<>(queueCapacity));
    }

}

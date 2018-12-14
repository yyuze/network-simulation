package com.yyuze;

import com.yyuze.anno.system.Command;
import com.yyuze.anno.system.Schedule;
import com.yyuze.enable.Assembleable;
import com.yyuze.enums.LayerType;
import com.yyuze.anno.platform.Layer;
import com.yyuze.layer.LinkLayerPlatform;
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

    private HashMap<Long,ArrayList<Invoker>> scheduleInvokers;

    private ThreadPoolExecutor threadPoor;

    private final ReentrantLock deamonLock = new ReentrantLock();

    private final Condition terminate = deamonLock.newCondition();

    private final Console console = new Console();

    public void start() {
        CommandTask commandTask = new CommandTask(this.deamonLock,terminate,this.commandInvokers, console);
        ScheduleTask scheduleTask = new ScheduleTask(this.deamonLock,this.scheduleInvokers,console);
        this.threadPoor.execute(commandTask);
        this.threadPoor.execute(scheduleTask);
        this.threadPoor.execute(()->{
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
                this.threadPoor.shutdown();
                this.console.write("good bye");
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                lock.unlock();
            }
        });

    }

    public VirtualNetworkSimulation() {
        this.platforms = new HashMap<>();
        this.instances = new ArrayList<>();
        this.classes = new ArrayList<>();
        this.commandInvokers = new HashMap<>();
        this.scheduleInvokers = new HashMap<>();
        this.initPlatforms();
        this.initCommandInvokers();
        this.initScheduleInvokers();
        this.initThreadPoor();
    }


    private void initPlatforms() {
        this.instanceLinkLayer();
        this.instanceNetworkLayer();
        this.instanceTransportLayer();
        this.instanceApplicationLayer();

    }

    private void instanceLinkLayer() {
        EmptyLinkLayerPlatform.Builder builder = new EmptyLinkLayerPlatform.Builder();
        Assembleable linkLayerPlatform = builder.buildRuntimePlatform(LinkLayerPlatform.class);
        LayerType type = linkLayerPlatform.getClass().getAnnotation(Layer.class).value();
        this.platforms.put(type, linkLayerPlatform);
        this.instances.addAll(builder.getInstancesOnPlatform());
        this.classes.addAll(builder.getClassesOnPlatform());
    }

    private void instanceNetworkLayer() {
        //todo 初始化网络层
    }

    private void instanceTransportLayer() {
        //todo 初始化运输层
    }

    private void instanceApplicationLayer() {
        //todo 初始化应用层
    }

    private void initScheduleInvokers(){
        for(Object instance:this.instances){
            Method[] methods = instance.getClass().getDeclaredMethods();
            for(Method method:methods){
                Schedule anno = method.getAnnotation(Schedule.class);
                if(anno != null){
                    long invokePeriod = anno.period();
                    if(!this.scheduleInvokers.containsKey(invokePeriod)){
                        this.scheduleInvokers.put(invokePeriod,new ArrayList<>());
                    }
                    Invoker invoker = new Invoker(instance,method);
                    this.scheduleInvokers.get(invokePeriod).add(invoker);
                }
            }
        }
    }

    private void initCommandInvokers() {
        this.platforms.forEach((type,platform)->{
            Method[] methods = platform.getClass().getDeclaredMethods();
            for(Method method:methods){
                Command anno = method.getAnnotation(Command.class);
                if(anno!=null){
                    Invoker invoker = new Invoker(platform,method);
                    this.commandInvokers.put(anno.value().toString(),invoker);
                }
            }
        });
    }

    private void initThreadPoor(){
        int corePoolSize = 3;
        int maximumPoolSize = 4;
        long keepAliveTime = 500;
        TimeUnit unit = TimeUnit.MILLISECONDS;
        int queueCapacity = 8;
        this.threadPoor = new ThreadPoolExecutor(corePoolSize,maximumPoolSize,keepAliveTime,unit, new ArrayBlockingQueue<>(queueCapacity));
    }

}

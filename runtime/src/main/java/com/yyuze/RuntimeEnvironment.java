package com.yyuze;

import com.yyuze.anno.system.Command;
import com.yyuze.anno.system.Schedule;
import com.yyuze.enable.Assembleable;
import com.yyuze.enums.LayerType;
import com.yyuze.anno.platform.Layer;
import com.yyuze.layer.LinkLayerPlatform;
import com.yyuze.system.CommandTask;
import com.yyuze.tool.Invoker;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Author: yyuze
 * Time: 2018-12-02
 */
public class RuntimeEnvironment {

    private HashMap<LayerType, Assembleable> platforms;

    private ArrayList<Object> instances;

    private ArrayList<Class> classes;

    private HashMap<String, Invoker> commandInvokers;

    private HashMap<Long,ArrayList<Invoker>> scheduleInvokers;

    private ThreadPoolExecutor threadPoor;

    private final ReentrantLock commandLock = new ReentrantLock();

    private final ReentrantLock scheduleLock = new ReentrantLock();

    public void start() {
        this.threadPoor.execute(new CommandTask(commandLock,this.commandInvokers));
    }

    public RuntimeEnvironment() {
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
        int queueCapacity = 4;
        this.threadPoor = new ThreadPoolExecutor(corePoolSize,maximumPoolSize,keepAliveTime,unit, new ArrayBlockingQueue<>(queueCapacity));
    }

}

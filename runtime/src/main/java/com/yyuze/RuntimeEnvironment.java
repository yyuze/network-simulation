package com.yyuze;

import com.yyuze.anno.system.Command;
import com.yyuze.enable.Assembleable;
import com.yyuze.enums.LayerType;
import com.yyuze.anno.platform.Layer;
import com.yyuze.layer.LinkLayerPlatform;
import com.yyuze.tool.Invoker;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Author: yyuze
 * Time: 2018-12-02
 */
public class RuntimeEnvironment {

    private HashMap<LayerType, Assembleable> platforms;

    private ArrayList<Object> instances;

    private ArrayList<Class> classes;

    private HashMap<String, Invoker> command;

    public void start() {

    }

    public RuntimeEnvironment() {
        this.platforms = new HashMap<>();
        this.instances = new ArrayList<>();
        this.classes = new ArrayList<>();
        this.command = new HashMap<>();
        this.initPlatforms();
    }


    private void initPlatforms() {
        this.instanceLinkLayer();
        this.instanceNetworkLayer();
        this.instanceTransportLayer();
        this.instanceApplicationLayer();
        this.initCommandLine();
        this.initSchedule();
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

    private void initCommandLine() {
        this.platforms.forEach((type,platform)->{
            Method[] methods = platform.getClass().getDeclaredMethods();
            for(Method method:methods){
                Command anno = method.getAnnotation(Command.class);
                if(anno!=null){
                    Invoker invoker = new Invoker(platform,method);
                    this.command.put(anno.value().toString(),invoker);
                }
            }
        });
    }

    private void initSchedule() {
        //todo 初始化轮询（模拟网络驱动力）
    }

}

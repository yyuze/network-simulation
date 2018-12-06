package com.yyuze.builder;

import com.yyuze.enable.Assembleable;

import java.io.File;
import java.util.ArrayList;

/**
 * Author: yyuze
 * Time: 2018-12-05
 */
public abstract class BaseBuilder {

    protected ArrayList<Class> classes;

    protected ArrayList<Object> instances;

    public BaseBuilder(){
        this.classes = new ArrayList<>();
        this.instances = new ArrayList<>();
        String jarPath = this.getClass().getClassLoader().getResource("").getPath();
        this.loadClassesInJar(jarPath);
    }

    protected void loadClassesInJar(String path){
        String[] ls = new File(path).list();
        for(String fileName :ls){
            String fullFilePath = path+"/"+fileName;
            File file = new File(fullFilePath);
            if(file.isDirectory()){
                this.loadClassesInJar(fullFilePath);
            }else{
                if(fileName.contains(".class")){
                    try {
                        Class clz = this.getClassFromJavaFile(fullFilePath);
                        this.classes.add(clz);
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    protected Class getClassFromJavaFile(String filePath) throws ClassNotFoundException {
        int begin = filePath.indexOf("//")+2;
        String className = filePath.substring(begin,filePath.lastIndexOf(".")).replaceAll("/",".");
        return Class.forName(className);
    }

    public ArrayList<Object> getInstancesOnPlatform(){
        return this.instances;
    }

    public ArrayList<Class> getClassesOnPlatform(){
        return this.classes;
    }

    protected abstract Assembleable buildRuntimePlatform();
}

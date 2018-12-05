package com.yyuze.builder;

import com.yyuze.invoker.command.CommandInvoker;
import com.yyuze.enable.Assembleable;

import java.io.File;
import java.util.HashMap;

/**
 * Author: yyuze
 * Time: 2018-12-05
 */
public abstract class BaseBuilder {

    protected HashMap<String,Class> classMap;

    public BaseBuilder(){
        this.classMap = new HashMap<>();
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
                        this.classMap.put(clz.getName(),clz);
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

    abstract Assembleable buildRuntimePlatform();
}

package com.yyuze;

/**
 * Author: yyuze
 * Time: 2018-12-02
 */
public class RuntimeEnvironment {

    public void start(){

    }

    private void init(){
        this.instanceLinkLayer();
        this.instanceNetworkLayer();
        this.instanceTransportLayer();
        this.instanceApplicationLayer();
        this.initCommandLine();
    }

    private void instanceLinkLayer(){
        //todo 初始化链路层
    }

    private void instanceNetworkLayer(){
        //todo 初始化网络层
    }

    private void instanceTransportLayer(){
        //todo 初始化运输层
    }

    private void instanceApplicationLayer(){
        //todo 初始化应用层
    }

    private void initCommandLine(){
        //todo 初始化命令行
    }

}

package com.yyuze.builder;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;


/**
 * Author: yyuze
 * Time: 2018-12-05
 */
public class LinkLayerBuilderTest {

    private LinkLayerBuilder builder;


    @BeforeAll
    @Test
    public void initLinkLayerBuilderTest(){
        System.out.println(this.getClass().getName()+" start");
        this.builder = new LinkLayerBuilder();
    }


}

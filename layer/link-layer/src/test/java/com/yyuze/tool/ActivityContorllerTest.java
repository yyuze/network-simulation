package com.yyuze.tool;

import com.yyuze.component.PhisicalLink;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Author: yyuze
 * Time: 2018-12-07
 */
class ActivityContorllerTest {

    private static ActivityContorller contorller;

    private static int collisionCount = 5;

    private long BIT_512_TIME = 512000000 / PhisicalLink.BANDWIDTH;

    @BeforeAll
    public static void initActivityContorllerTest(){
        contorller = new ActivityContorller();
    }

    @Test
    void pause() {
        /**
         * 碰撞5次
         * 最大停等时间 = 2^5 512比特时间
         * 最小停等时间 = 1 512比特时间
         */
        contorller.pause(collisionCount);
        System.out.println("pause() executed");
        contorller.reset();
    }

    @Test
    void reset() throws InterruptedException {
        contorller.pause(collisionCount);
        boolean flag_pause = !contorller.isAllowedTransfer();
        assert flag_pause : "pause() failed";
        System.out.println("pause() passed");
        contorller.reset();
        Thread.sleep(1);
        boolean flag_reset = contorller.isAllowedTransfer();
        assert flag_reset :"reset() failed";
        System.out.println("reset() passed");
    }

    @Test
    void isAllowedTransfer() throws InterruptedException {
        contorller.pause(collisionCount);
        boolean flag_pause_min = !contorller.isAllowedTransfer();
        assert flag_pause_min : "最小停等时间 failed";
        System.out.println("最小停等时间 passed");
        /**
         * 停等 2^5 512比特时间 + 1 ms
         */
        Thread.sleep(BIT_512_TIME*(int)Math.pow(2,5)+1);
        boolean flag_pause_max = contorller.isAllowedTransfer();
        assert flag_pause_max : "最大停等时间不通过";
        System.out.println("最大停等时间 passed");
        System.out.println("isAllowedTransfer() passed");
    }
}
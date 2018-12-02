package com.yyuze;

import com.yyuze.enable.Receiveable;
import com.yyuze.enable.Sendable;

/**
 * Author: yyuze
 * Time: 2018-12-01
 */
public abstract class BasePlatform extends Thread implements Receiveable, Sendable{

    @Override
    public void run() {
    }
}

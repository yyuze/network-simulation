package com.yyuze.anno;

import com.yyuze.enums.CommandEnum;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Author: yyuze
 * Time: 2018-12-02
 */

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Command {

    CommandEnum value();

}

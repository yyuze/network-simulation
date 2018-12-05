package com.yyuze.platform.anno;

import com.yyuze.enums.LayerType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Author: yyuze
 * Time: 2018-12-01
 */

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Layer {

    LayerType value();

}

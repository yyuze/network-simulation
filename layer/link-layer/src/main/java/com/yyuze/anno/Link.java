package com.yyuze.anno;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Author: yyuze
 * Time: 2018-12-02
 */

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Link {

    long serial();

    long[] MACs();

    long[] switchs();

}

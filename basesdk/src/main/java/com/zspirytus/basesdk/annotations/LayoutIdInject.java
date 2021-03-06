package com.zspirytus.basesdk.annotations;

import android.support.annotation.LayoutRes;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * layoutId 注解类
 * Created by ZSpirytus on 2018/9/9.
 */

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface LayoutIdInject {
    @LayoutRes
    int value() default 0;
}

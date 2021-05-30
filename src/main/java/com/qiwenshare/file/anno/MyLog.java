package com.qiwenshare.file.anno;

import java.lang.annotation.*;

/**
 * 自定义注解类
 */
@Target(ElementType.METHOD) //注解放置的目标位置,METHOD是可注解在方法级别上
@Retention(RetentionPolicy.RUNTIME) //注解在哪个阶段执行
@Documented //生成文档
public @interface MyLog {
    String module() default "";

    String operation() default "";

    String type() default "operation";

    String level() default "0"; //0-低，1-中，2-高
}
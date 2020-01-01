package com.github.sgi.core.anno;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @FileName: GImpl.java
 * @Description: GImpl.java类说明
 * @Author: timestatic
 * @Date: 2019/12/22 18:23
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface GImpl {

    String value() default "";

    boolean override() default true;

    boolean isSpringBean() default false;

}

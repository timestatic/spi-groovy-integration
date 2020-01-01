package com.github.sgi.core.anno;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @FileName: SGI.java
 * @Description: SGI.java类说明
 * @Author: timestatic
 * @Date: 2019/12/22 16:54
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface SGI {

    String value() default "";

}

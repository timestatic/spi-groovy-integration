package com.github.sgi.core.scanner;

import com.github.sgi.core.anno.GImpl;
import org.reflections.Reflections;

import java.lang.annotation.Annotation;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;
import java.util.regex.Pattern;

/**
 * @FileName: ScanTest.java
 * @Description: ScanTest.java类说明
 * @Author: timestatic
 * @Date: 2019/12/29 16:36
 */
public class PackageScanner {
    public static final Pattern COMMA_SPLIT_PATTERN = Pattern.compile("\\s*[,]+\\s*");

    public static Set<Class<?>> findGImplClass(Class<?> clazz) {
        return scanPackages("", GImpl.class, item -> !item.isInterface() && clazz.isAssignableFrom(item));
    }

    public static Set<Class<?>> scanPackages(String packageNames, Class<? extends Annotation> annotation, Predicate<Class> filter) {
        Set<Class<?>> result = new HashSet<>();
        for (String packageName : COMMA_SPLIT_PATTERN.split(packageNames)) {
            for (Class<?> clazz : scan(packageName.trim(), annotation)) {
                if (filter.test(clazz)) {
                    result.add(clazz);
                }
            }
        }
        return result;
    }

    public static Set<Class<?>> scan(String packageName, Class<? extends Annotation> annotation) {
        Reflections reflections = new Reflections(packageName);
        return reflections.getTypesAnnotatedWith(annotation, true);
    }
}

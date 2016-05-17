package com.legendmohe.intentinjector;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by legendmohe on 16/5/17.
 */
public class IntentInjector {

    private static Map<Class, Set<Field>> sFieldCache = new HashMap<>();
    private static Map<Class, Set<Method>> sMethodCache = new HashMap<>();

    public synchronized static void inject(Activity target) {
        Bundle datas = target.getIntent().getExtras();
        if (datas != null) {
            injectFields(target, datas);
            injectMethods(target, datas);
        }
    }

    public synchronized static void inject(Object target, Intent intent) {
        Bundle datas = intent.getExtras();
        if (datas != null && datas.size() != 0) {
            injectFields(target, datas);
            injectMethods(target, datas);
        }
    }

    private static void injectMethods(Object target, Bundle intent) {
        Set<Method> methods = sMethodCache.get(target.getClass());
        if (methods == null) {
            methods = findAnnotatedMethods(target.getClass(), InjectIntent.class, 1);
            sMethodCache.put(target.getClass(), methods);
        }
        if (methods.size() == 0)
            return;

        for (Method method: methods) {
            String key = method.getAnnotation(InjectIntent.class).value();
            if (key != null && key.length() != 0) {
                Object value = intent.get(key);
                if (value != null) {
                    try {
                        method.invoke(target, value);
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

    }

    private static void injectFields(Object target, Bundle intent) {
        Set<Field> fields = sFieldCache.get(target.getClass());
        if (fields == null) {
            fields = findAnnotatedFields(target.getClass(), InjectIntent.class);
            sFieldCache.put(target.getClass(), fields);
        }
        if (fields.size() == 0)
            return;

        for (Field field: fields) {
            boolean accessible = true;
            if (!field.isAccessible()) {
                accessible = false;
                field.setAccessible(true);
            }
            String key = field.getAnnotation(InjectIntent.class).value();
            if (key != null && key.length() != 0) {
                Object value = intent.get(key);
                if (value != null) {
                    try {
                        field.set(target, value);
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            }
            field.setAccessible(accessible);
        }
    }

    public static Set<Field> findAnnotatedFields(final Class<?> type, Class<? extends Annotation> annotationClass) {
        Class<?> clazz = type;
        final Set<Field> fields = new HashSet<Field>();
        while (!shouldSkipClass(clazz)) {
            final Field[] allFields = clazz.getDeclaredFields();
            for (final Field field : allFields) {
                if (filterField(field, annotationClass)) {
                    fields.add(field);
                }
            }
            clazz = clazz.getSuperclass();
        }
        return fields;
    }

    private static boolean filterField(Field field, Class<? extends Annotation> annotation) {
        if (!field.isAnnotationPresent(annotation)) {
            return false;
        }
        if (Modifier.isStatic(field.getModifiers())) {
            return false;
        }
        if (Modifier.isVolatile(field.getModifiers())) {
            return false;
        }
        return true;
    }


    public static Set<Method> findAnnotatedMethods(final Class<?> type, Class<? extends Annotation> annotationClass, int paramNum) {
        Class<?> clazz = type;
        final Set<Method> methods = new HashSet<Method>();
        while (!shouldSkipClass(clazz)) {
            final Method[] allMethods = clazz.getDeclaredMethods();
            for (final Method method : allMethods) {
                if (filterMethod(method, annotationClass, paramNum)) {
                    methods.add(method);
                }
            }
            clazz = clazz.getSuperclass();
        }
        return methods;
    }

    private static boolean filterMethod(Method method, Class<? extends Annotation> annotation, int paramNum) {
        if (!method.isAnnotationPresent(annotation)) {
            return false;
        }
        if (!Modifier.isPublic(method.getModifiers())) {
            return false;
        }
        if (Modifier.isStatic(method.getModifiers())) {
            return false;
        }
        if (Modifier.isVolatile(method.getModifiers())) {
            return false;
        }
        if (method.getParameterTypes().length != paramNum) {
            return false;
        }
        return true;
    }

    private static boolean shouldSkipClass(final Class<?> clazz) {
        final String clsName = clazz.getName();
        return Object.class.equals(clazz)
                || clsName.startsWith("java.")
                || clsName.startsWith("javax.")
                || clsName.startsWith("android.")
                || clsName.startsWith("com.android.");
    }
}
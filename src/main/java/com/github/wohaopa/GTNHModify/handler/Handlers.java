package com.github.wohaopa.GTNHModify.handler;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import com.github.wohaopa.GTNHModify.GTNHModifyMod;
import com.github.wohaopa.GTNHModify.ModHelper;
import com.github.wohaopa.GTNHModify.strategies.Strategy;

public class Handlers {

    public static List<String> handlers = new ArrayList<>();
    private static final String Suffix = "Handler";
    private static final List<Method> methods = new ArrayList<>();

    public static boolean init() {
        if (!Strategy.prevInit()) return false;

        GTNHModifyMod.LOG.info("Start processing the recipe");
        if (methods.isEmpty()) {

            handlers.add("Minecraft");
            if (ModHelper.hasGregtech) handlers.add("GregTech");
            if (ModHelper.hasThaumcraft) handlers.add("Thaumcraft");

            String pkg = Handlers.class.getName()
                .replace("Handlers", "");
            for (String name : handlers) {
                String className = pkg + name + Suffix;
                try {
                    Class<?> clazz = Class.forName(className);
                    IHandler iHandler = clazz.getAnnotation(IHandler.class);
                    if (iHandler != null) {
                        Method method = clazz.getDeclaredMethod(iHandler.value());
                        methods.add(method);
                        GTNHModifyMod.LOG.info("Discovery handler: " + className);
                    }
                } catch (ClassNotFoundException | NoSuchMethodException e) {
                    GTNHModifyMod.LOG.debug("An error occurred while initializing handler. Reason: " + e.getMessage());
                }
            }
        }
        for (Method method : methods) {
            try {
                GTNHModifyMod.LOG.info(
                    "Invoke handler: " + method.getDeclaringClass()
                        .getName());
                method.invoke(null);
            } catch (IllegalAccessException | InvocationTargetException e) {
                GTNHModifyMod.LOG.debug("An error occurred while executing handler. Reason: " + e.getMessage());
            }
        }

        Strategy.postInit();
        GTNHModifyMod.LOG.info("Complete processing the recipe");
        return true;
    }
}

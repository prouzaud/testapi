package com.xxx.test.api.nrt.apinrt.campaignReporter.pluginEngine;

import com.xxx.test.api.nrt.apinrt.campaignReporter.exceptions.ReportException;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Component
public class PluginsEngine {

    private final ApplicationContext context;

    private final List<ReportPlugin> reportPlugins = new ArrayList<>();

    public PluginsEngine(ApplicationContext context) {
        this.context = context;
        detectPlugins();
    }

    public void detectPlugins() {
        Map<String, ReportPlugin> beans = context.getBeansOfType(ReportPlugin.class);
        reportPlugins.addAll(beans.values());
    }

    public void dispatchCall(String methodName, Object... params){
        for (ReportPlugin plugin : reportPlugins) {
            invokeMethod(plugin, methodName, params);
        }
    }

    private void invokeMethod(Object bean, String methodName, Object... params){
        Class<?>[] classes = new Class[params.length];
        for (int i = 0 ; i<params.length; i++){
            if (params[i] instanceof Exception) {
                classes[i] = Exception.class;
            } else {
                classes[i] = params[i].getClass();
            }
        }
        Method method;
        method = findMethod(bean, methodName, classes);
        invokeMethod(bean, method, params);
    }

    private void invokeMethod(Object bean, Method method, Object[] params ) {
        try {
            method.invoke(bean, params);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new ReportException("Plugin report issue: Exception raised during " + bean.getClass().getCanonicalName() + "." + method.getName() + " invocation with parameters " + Arrays.toString(params), e);
        }
    }

    private Method findMethod(Object bean, String methodName, Class<?>[] classes) {
        Method method;
        try {
            method = bean.getClass().getMethod(methodName, classes);
        } catch (NoSuchMethodException e) {
            throw new ReportException("Plugin report issue: Unable to find the method " + methodName + " from the class " + bean.getClass().getCanonicalName() + " with the parameters " + Arrays.toString(classes), e);
        }
        return method;
    }
}

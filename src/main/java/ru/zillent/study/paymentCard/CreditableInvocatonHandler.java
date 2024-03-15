package ru.zillent.study.paymentCard;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;

public class CreditableInvocatonHandler implements InvocationHandler {
    private final Object obj;
    private HashMap<String, Object> cache;

    public CreditableInvocatonHandler(Object obj) {
        this.obj = obj;
        this.cache = new HashMap<>();
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Method classMethod = obj.getClass().getMethod(method.getName(), method.getParameterTypes());
        if (classMethod.getAnnotationsByType(Cache.class).length != 0) {
            if (!this.cache.containsKey(method.getName())) this.cache.put(method.getName(), method.invoke(obj, args));
            return this.cache.get(method.getName());
        }
        if (classMethod.getAnnotationsByType(Mutator.class).length != 0) {
            this.cache.clear();
        }
        return method.invoke(obj, args);
    }
}

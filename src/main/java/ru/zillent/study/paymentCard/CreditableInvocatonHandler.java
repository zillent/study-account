package ru.zillent.study.paymentCard;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class CreditableInvocatonHandler implements InvocationHandler {
    private final Object obj;

    record CacheStruct(
        Object[] params,
        Object result,
        int timeToLive
    ) {};

    private HashMap<String, List<CacheStruct>> cache;

    public CreditableInvocatonHandler(Object obj) {
        this.obj = obj;
        this.cache = new HashMap<>();
        Thread cacheClearance = new Thread(()->{
            while (true) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                HashMap<String, List<CacheStruct>> changedItems = new HashMap<>();
                synchronized (this.cache) {
                    this.cache.forEach((methodName, cacheRecord) -> {
                        List<CacheStruct> itemsToAdd = new ArrayList<>();
                        List<CacheStruct> itemsToDelete = new ArrayList<>();
                        for (CacheStruct cacheStruct : cacheRecord) {
                            int timeToLive = cacheStruct.timeToLive - 100;
                            itemsToDelete.add(cacheStruct);
                            if (timeToLive > 0)
                                itemsToAdd.add(new CacheStruct(cacheStruct.params, cacheStruct.result, timeToLive));
                        }
                        itemsToDelete.forEach(cacheRecord::remove);
                        cacheRecord.addAll(itemsToAdd);
                        changedItems.put(methodName, cacheRecord);
                    });
                    this.cache.putAll(changedItems);
                }
            }
        });
        cacheClearance.start();
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Method classMethod = obj.getClass().getMethod(method.getName(), method.getParameterTypes());
        List<CacheStruct> cacheRecord = new ArrayList<>();
        String cacheMethodName = method.getName();
        Object[] cacheArgs = args;
        synchronized (this.cache) {
            if (classMethod.isAnnotationPresent(Cache.class)) {
                int timeToLive = classMethod.getAnnotation(Cache.class).timeToLive();
                if (this.cache.containsKey(cacheMethodName)) {
                    cacheRecord = this.cache.get(cacheMethodName);
                    for (CacheStruct cacheStruct : cacheRecord) {
                        if (checkParamsIsEqual(cacheStruct.params, cacheArgs)) {
                            cacheRecord.remove(cacheStruct);
                            cacheRecord.add(new CacheStruct(cacheStruct.params, cacheStruct.result, timeToLive));
                            this.cache.put(cacheMethodName, cacheRecord);
                            return cacheStruct.result;
                        }
                    }
                }
                Object result = method.invoke(obj, args);
                cacheRecord.add(new CacheStruct(cacheArgs, result, timeToLive));
                this.cache.put(cacheMethodName, cacheRecord);
                return result;
            }
            if (classMethod.isAnnotationPresent(Mutator.class)) {
                cacheMethodName = cacheMethodName.replace("set", "get");
                cacheArgs = Arrays.copyOf(args, args.length - 1);
                if (this.cache.containsKey(cacheMethodName)) {
                    cacheRecord = this.cache.get(cacheMethodName);
                    for (CacheStruct cacheStruct : cacheRecord) {
                        if (checkParamsIsEqual(cacheStruct.params, cacheArgs)) {
                            cacheRecord.remove(cacheStruct);
                            break;
                        }
                    }
                }
                this.cache.put(cacheMethodName, cacheRecord);
            }
        }
        return method.invoke(obj, args);
    }

    private boolean checkParamsIsEqual(Object[] params, Object[] args) {
        if (params == null && args == null) return true;
        if (params == null || args == null) return false;
        if (params.length != args.length) return false;
        for (Object param: params) {
            boolean argsContainsParam = false;
            for (Object arg: args) {
                if (param == arg) {
                    argsContainsParam = true;
                    break;
                }
            }
            if (!argsContainsParam) return false;
        }
        return true;
    }
}

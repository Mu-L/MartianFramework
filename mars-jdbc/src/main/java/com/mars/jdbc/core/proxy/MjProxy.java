package com.mars.jdbc.core.proxy;

import com.mars.jdbc.core.annotation.DataSource;
import com.mars.jdbc.core.annotation.MarsGet;
import com.mars.jdbc.core.annotation.MarsSelect;
import com.mars.jdbc.core.annotation.MarsUpdate;
import com.mars.jdbc.core.proxy.oper.ProxyOperation;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

/**
 * 代理类
 * @author yuye
 *
 */
public class MjProxy implements MethodInterceptor {

    /**
     * 获取代理对象
     *
     * @param clazz bean的class
     * @return 对象
     */
    public Object getProxy(Class<?> clazz) {
        Enhancer enhancer = new Enhancer();
        // 设置需要创建子类的类
        enhancer.setSuperclass(clazz);
        enhancer.setCallback(this);
        // 通过字节码技术动态创建子类实例
        return enhancer.create();
    }


    /**
     * 绑定代理
     *
     * @param o
     * @param method
     * @param args
     * @param methodProxy
     * @return obj
     * @throws Throwable
     */
    @Override
    public Object intercept(Object o, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {
        MarsGet marsGet = method.getAnnotation(MarsGet.class);
        MarsSelect marsSelect = method.getAnnotation(MarsSelect.class);
        MarsUpdate marsUpdate = method.getAnnotation(MarsUpdate.class);

        int count = checkAnnotation(marsGet, marsSelect, marsUpdate);

        if (count == 0) {
            return methodProxy.invokeSuper(o, args);
        } else if (count == 1) {
            return executeMethod(args,method,marsGet,marsSelect,marsUpdate);
        } else {
            throw new Exception(method.getName() + "方法上不允许有多个sql注解");
        }
    }

    /**
     * 执行方法
     * @param args 参数
     * @param method 要执行的方法
     * @param marsGet 注解
     * @param marsSelect 注解
     * @param marsUpdate 注解
     * @return 返回值
     * @throws Exception 异常
     */
    private Object executeMethod(Object[] args,Method method,MarsGet marsGet, MarsSelect marsSelect, MarsUpdate marsUpdate) throws Exception {
        Object param = checkArgs(args);
        String dataSourceName = null;
        DataSource dataSource = method.getAnnotation(DataSource.class);
        if (dataSource != null) {
            dataSourceName = dataSource.value();
        }
        if (marsGet != null) {
            return ProxyOperation.get(marsGet, dataSourceName, param, method);
        } else if (marsSelect != null) {
            return ProxyOperation.select(marsSelect, dataSourceName, param, method);
        } else if (marsUpdate != null) {
            return ProxyOperation.update(marsUpdate, dataSourceName, param);
        }
        return null;
    }

    /**
     * 校验参数
     *
     * @param args 参数
     * @return 数据
     * @throws Exception 异常
     */
    private Object checkArgs(Object[] args) throws Exception {
        if (args != null && args.length > 1) {
            throw new Exception("MarsDAO的方法只允许有一个参数列表");
        } else if (args == null || args.length < 1) {
            return null;
        }
        return args[0];
    }

    /**
     * 校验注解
     *
     * @param marsGet    注解
     * @param marsSelect 注解
     * @param marsUpdate 注解
     * @return
     */
    private int checkAnnotation(MarsGet marsGet, MarsSelect marsSelect, MarsUpdate marsUpdate) {
        int count = 0;
        if (marsGet != null) {
            count++;
        }
        if (marsSelect != null) {
            count++;
        }
        if (marsUpdate != null) {
            count++;
        }
        return count;
    }
}



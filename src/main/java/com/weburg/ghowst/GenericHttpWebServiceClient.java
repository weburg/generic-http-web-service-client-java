package com.weburg.ghowst;

import java.lang.reflect.Method;

public class GenericHttpWebServiceClient implements java.lang.reflect.InvocationHandler {
    private final String baseUrl;
    private final HttpWebServiceInvoker httpWebServiceInvoker;

    public static Object newInstance(String baseUrl, Class webServiceClass) {
        Class[] interfaceArray = {webServiceClass};

        return java.lang.reflect.Proxy.newProxyInstance(
                webServiceClass.getClassLoader(),
                interfaceArray,
                new GenericHttpWebServiceClient(baseUrl));
    }

    private GenericHttpWebServiceClient(String baseUrl) {
        this.baseUrl = baseUrl;
        this.httpWebServiceInvoker = new HttpWebServiceInvoker();
    }

    public Object invoke(Object proxy, Method method, Object[] arguments) {
        return httpWebServiceInvoker.invoke(method, arguments, this.baseUrl);
    }
}

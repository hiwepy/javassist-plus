/*
 * Copyright (c) 2017, vindell (https://github.com/vindell).
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.github.vindell.javassist.proxy;

import java.lang.reflect.Method;

import javassist.util.proxy.MethodFilter;
import javassist.util.proxy.MethodHandler;
import javassist.util.proxy.Proxy;
import javassist.util.proxy.ProxyFactory;

/**
 * @see http://blog.csdn.net/mingxin95/article/details/51810499
 */
@SuppressWarnings({ "unchecked" })
public class JavassistProxy {

	// 代理工厂
	private static ProxyFactory proxyFactory = new ProxyFactory();
	
	/*
	 * 要代理的对象
	 */
	public static <T> T getProxy(T target) throws InstantiationException, IllegalAccessException {
		return (T) getProxy(target.getClass());
	}

	
	public static <T> T getProxy(Class<T> proxyClass) throws InstantiationException, IllegalAccessException {
		/*
		 * 定义一个拦截器。在调用目标方法时，Javassist会回调MethodHandler接口方法拦截， 来实现你自己的代理逻辑，
		 * 类似于JDK中的InvocationHandler接口。
		 */
		MethodHandler handler = new MethodHandler() {
			/*
			 * self为由Javassist动态生成的代理类实例，
			 * method为 当前要调用的方法 proceed 为生成的代理类对方法的代理引用。
			 * Object[]为参数值列表， 返回：从代理实例的方法调用返回的值。
			 * 其中，proceed.invoke(self, args);
			 * 调用代理类实例上的代理方法的父类方法（即实体类ConcreteClassNoInterface中对应的方法）
			 */
			public Object invoke(Object self, Method method, Method proceed, Object[] args) throws Throwable {
				
				System.out.println("--------------------------------");
				System.out.println("method name: " + method.getName() + " exec");
				System.out.println(self.getClass());
				// class com.javassist.demo.A_$$_javassist_0
				System.out.println("代理类对方法的代理引用:" + method.getName());
				System.out.println("开启事务 -------");
				Object result = proceed.invoke(self, args);
				System.out.println("提交事务 -------");
				return result;
			}
		};
		
		return (T) getProxy(proxyClass, handler);
	}
	
	/*
	 * 要代理的对象class
	 */
	public static <T> T getProxy(Class<T> proxyClass, MethodHandler handler) throws InstantiationException, IllegalAccessException {

		// 设置需要创建子类的父类
		proxyFactory.setSuperclass(proxyClass);
		proxyFactory.setFilter(new MethodFilter() {
			public boolean isHandled(Method m) {
				// ignore finalize()
				return !m.getName().equals("finalize");
			}
		});
		
		// 通过字节码技术动态创建子类实例
		Class<?> realClass = proxyFactory.createClass();

		Object proxy = realClass.newInstance();
		((Proxy) proxy).setHandler(handler);
		return (T) proxy;
	}

}
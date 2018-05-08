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
package com.github.vindell.javassist.utils;

import java.util.concurrent.ConcurrentHashMap;

import javassist.ClassClassPath;
import javassist.ClassPath;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtField;
import javassist.CtMethod;
import javassist.LoaderClassPath;
import javassist.NotFoundException;

/**
 * 
 * @className	： JavassistUtils
 * @description	： TODO(描述这个类的作用)
 * @author 		： <a href="https://github.com/vindell">vindell</a>
 * @see http://blog.csdn.net/youaremoon/article/details/50766972
 * @see https://my.oschina.net/GameKing/blog/794580
 * @see http://www.codeweblog.com/%E5%85%B3%E4%BA%8Ejavassist-notfoundexception/
 */
public class JavassistUtils {

	private static ConcurrentHashMap<ClassLoader, ClassPool> CLASS_POOL_MAP = new ConcurrentHashMap<ClassLoader, ClassPool>();
	
	public static ClassPool getDefaultPool() {
		ClassPool pool = ClassPool.getDefault();
		pool.appendClassPath(new LoaderClassPath(Thread.currentThread().getContextClassLoader()));
		/**为defaultPool添加一个类路径 : http://www.codeweblog.com/%E5%85%B3%E4%BA%8Ejavassist-notfoundexception/*/
		pool.insertClassPath(new ClassClassPath(JavassistUtils.class));
		pool.importPackage("java.util");
		pool.importPackage("java.lang");
		pool.importPackage("java.lang.reflect");
		return pool;
	}
	
	/**
	 * <pre>
	 *	ClassPath是一个接口，代表类的搜索路径，含有具体的搜索实现。当通过其它途径无法获取要编辑的类时，可以尝试定制一个自己的ClassPath。API提供的实现中值得关注的有：
     *	1. ByteArrayClassPath : 将类以字节码的形式加入到该path中，ClassPool 可以从该path中生成所需的CtClass。
     *	2. ClassClassPath : 通过某个class生成的path，通过该class的classloader来尝试加载指定的类文件。
     *	3. LoaderClassPath : 通过某个classloader生成path，并通过该classloader搜索加载指定的类文件。需要注意的是该类加载器以弱引用的方式存在于path中，当不存在强引用时，随时可能会被清理。
     * </pre>
	 */
	public static ClassPool getClassPool(ClassPath... classPaths) {
		if (null == classPaths || classPaths.length == 0) {
			return getDefaultPool();
		}
		ClassPool pool = new ClassPool(true);
		/**为defaultPool添加一个类路径 : http://www.codeweblog.com/%E5%85%B3%E4%BA%8Ejavassist-notfoundexception/*/
		pool.insertClassPath(new ClassClassPath(JavassistUtils.class));
		
		pool.importPackage("java.util");
		pool.importPackage("java.lang");
		pool.importPackage("java.lang.reflect");
		
		for (ClassPath classPath : classPaths) {
			pool.appendClassPath(classPath);
		}
		return pool;
	}
	
	/**
	 * 不同的ClassLoader返回不同的ClassPool
	 * @param loader
	 * @return
	 */
	public static ClassPool getClassPool(ClassLoader loader) {
		if (null == loader) {
			return getDefaultPool();
		}

		ClassPool pool = CLASS_POOL_MAP.get(loader);
		if (null == pool) {
			
			pool = new ClassPool(true);
			pool.appendClassPath(new LoaderClassPath(loader));
			/**为defaultPool添加一个类路径 : http://www.codeweblog.com/%E5%85%B3%E4%BA%8Ejavassist-notfoundexception/*/
			pool.insertClassPath(new ClassClassPath(JavassistUtils.class));
			
			pool.importPackage("java.util");
			pool.importPackage("java.lang");
			pool.importPackage("java.lang.reflect");
			
			CLASS_POOL_MAP.put(loader, pool);
		}
		return pool;
	}

	public static boolean hasField(final CtClass ctclass, final String fieldName) {
		try {
			// 检查字段是否已经定义
			CtField field = ctclass.getDeclaredField(fieldName);
			if(field != null) {
				return true;
			}
			return false;
		} catch (NotFoundException e) {
			return false;
		}
	}
	
	public static boolean hasMethod(final CtClass ctclass, final String methodName, CtClass... paramTypes) {
		try {
			// 有参方法
			if(paramTypes != null && paramTypes.length > 0) {
				// 检查方法是否已经定义
				CtMethod method = ctclass.getDeclaredMethod(methodName, paramTypes);
				if(method != null) {
					return true;
				}
			} else {
				// 检查方法是否已经定义
				CtMethod method = ctclass.getDeclaredMethod(methodName);
				if(method != null) {
					return true;
				}
			}
			return false;
		} catch (NotFoundException e) {
			return false;
		}
	}
	
	/*public static boolean reload(final CtClass ctclass, final String port) {
		byte[] classFile = ctclass.toBytecode();
		HotSwapper hs = new HotSwapper(port);
		hs.reload("Test", classFile);
	}*/
			 
}

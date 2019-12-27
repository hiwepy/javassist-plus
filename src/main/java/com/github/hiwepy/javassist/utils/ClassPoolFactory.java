/*
 * Copyright (c) 2018, hiwepy (https://github.com/hiwepy).
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
package com.github.hiwepy.javassist.utils;

import java.net.URL;
import java.net.URLClassLoader;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javassist.ClassClassPath;
import javassist.ClassPath;
import javassist.ClassPool;
import javassist.LoaderClassPath;

public class ClassPoolFactory {

	private static ConcurrentHashMap<ClassLoader, ClassPool> CLASS_POOL_MAP = new ConcurrentHashMap<ClassLoader, ClassPool>();

	public static ClassPool getDefaultPool() {
		ClassPool pool = ClassPool.getDefault();
		/**
		 * 为defaultPool添加一个类路径 :
		 * http://www.codeweblog.com/%E5%85%B3%E4%BA%8Ejavassist-notfoundexception/
		 */
		pool.insertClassPath(new ClassClassPath(JavassistUtils.class));
		pool.importPackage("java.util");
		pool.importPackage("java.lang");
		pool.importPackage("java.lang.reflect");
		return pool;
	}

	/**
	 *  <pre>
	 *	ClassPath是一个接口，代表类的搜索路径，含有具体的搜索实现。当通过其它途径无法获取要编辑的类时，可以尝试定制一个自己的ClassPath。API提供的实现中值得关注的有：
	 *	1. ByteArrayClassPath : 将类以字节码的形式加入到该path中，ClassPool 可以从该path中生成所需的CtClass。
	 *	2. ClassClassPath : 通过某个class生成的path，通过该class的classloader来尝试加载指定的类文件。
	 *	3. LoaderClassPath : 通过某个classloader生成path，并通过该classloader搜索加载指定的类文件。需要注意的是该类加载器以弱引用的方式存在于path中，当不存在强引用时，随时可能会被清理。
	 * </pre>
	 * @param classPaths The class paths
	 * @return ClassPool Instance
	 */
	public static ClassPool getClassPool(ClassPath... classPaths) {
		if (null == classPaths || classPaths.length == 0) {
			return getDefaultPool();
		}

		ClassPool pool = getClassPoolForCurrentContextClassLoader();
		for (ClassPath classPath : classPaths) {
			pool.appendClassPath(classPath);
		}

		pool.importPackage("java.util");
		pool.importPackage("java.lang");
		pool.importPackage("java.lang.reflect");

		
		return pool;
	}

	/**
	 * 不同的ClassLoader返回不同的ClassPool
	 * @param loader the class loader
	 * @return ClassPool Instance
	 */
	public static ClassPool getClassPool(ClassLoader loader) {
		if (null == loader) {
			return getDefaultPool();
		}

		ClassPool pool = CLASS_POOL_MAP.get(loader);
		if (null == pool) {
			
			pool = new ClassPool(true);
			pool.appendClassPath(new LoaderClassPath(loader));
			pool.importPackage("java.util");
			pool.importPackage("java.lang");
			pool.importPackage("java.lang.reflect");

			CLASS_POOL_MAP.put(loader, pool);
		}
		return pool;
	}
	
	public static ClassPool getClassPoolForManifest() {

		ClassPool pool = new ClassPool(true);
		
		/**
		 * 为defaultPool添加一个类路径 :
		 * http://www.codeweblog.com/%E5%85%B3%E4%BA%8Ejavassist-notfoundexception/
		 */
		pool.insertClassPath(new ClassClassPath(JavassistUtils.class));

		Set<URL> urls = ClassHelper.getClasspathUrlsByManifest();
		pool.appendClassPath(new LoaderClassPath(URLClassLoader.newInstance(urls.toArray(new URL[urls.size()]))));
		
		return pool;
	}
		
		

	public static ClassPool getClassPoolForCurrentContextClassLoader() {
		
		ClassPool pool = new ClassPool(true);

		/**
		 * 为defaultPool添加一个类路径 :
		 * http://www.codeweblog.com/%E5%85%B3%E4%BA%8Ejavassist-notfoundexception/
		 */
		pool.insertClassPath(new ClassClassPath(JavassistUtils.class));

		final ClassLoader tccl = Thread.currentThread().getContextClassLoader();
		if (tccl != null) {
			pool.appendClassPath(new LoaderClassPath(tccl));
		}
		pool.appendClassPath(new LoaderClassPath(JavassistUtils.class.getClassLoader()));
		
		return pool;
	}
	
}

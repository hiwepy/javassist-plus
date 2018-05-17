/*
 * Copyright (c) 2018, vindell (https://github.com/vindell).
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
package com.github.vindell.javassist;

import java.io.File;
import java.io.FileOutputStream;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.CtNewMethod;
import javassist.Modifier;
import javassist.bytecode.AnnotationsAttribute;
import javassist.bytecode.ClassFile;
import javassist.bytecode.ConstPool;
import javassist.bytecode.ParameterAnnotationsAttribute;
import javassist.bytecode.annotation.Annotation;
import javassist.bytecode.annotation.StringMemberValue;

public class JavassistWebserviceGenerator {

	public Class<?> createDynamicClazz() throws Exception {
		ClassPool pool = ClassPool.getDefault();

		// 创建类
		CtClass cc = pool.makeClass("org.apache.cxf.spring.boot.DynamicHelloWorldImpl");

		// 创建方法
		CtClass ccStringType = pool.get("java.lang.String");
		// 参数： 1：返回类型 2：方法名称 3：传入参数类型 4：所属类CtClass
		CtMethod ctMethod = new CtMethod(ccStringType, "sayHello", new CtClass[] { ccStringType }, cc);
		ctMethod.setModifiers(Modifier.PUBLIC);
		StringBuffer body = new StringBuffer();
		body.append("{");
		body.append("\n    System.out.println($1);");
		body.append("\n    return \"Hello, \" + $1;");
		body.append("\n}");
		ctMethod.setBody(body.toString());
		cc.addMethod(ctMethod);

		ClassFile ccFile = cc.getClassFile();
		ConstPool constPool = ccFile.getConstPool();

		// 添加类注解
		AnnotationsAttribute bodyAttr = new AnnotationsAttribute(constPool, AnnotationsAttribute.visibleTag);
		Annotation bodyAnnot = new Annotation("javax.jws.WebService", constPool);
		bodyAnnot.addMemberValue("name", new StringMemberValue("HelloWoldService", constPool));
		bodyAttr.addAnnotation(bodyAnnot);

		ccFile.addAttribute(bodyAttr);

		// 添加方法注解
		AnnotationsAttribute methodAttr = new AnnotationsAttribute(constPool, AnnotationsAttribute.visibleTag);
		Annotation methodAnnot = new Annotation("javax.jws.WebMethod", constPool);
		methodAnnot.addMemberValue("operationName", new StringMemberValue("sayHelloWorld", constPool));
		methodAttr.addAnnotation(methodAnnot);

		Annotation resultAnnot = new Annotation("javax.jws.WebResult", constPool);
		resultAnnot.addMemberValue("name", new StringMemberValue("result", constPool));
		methodAttr.addAnnotation(resultAnnot);

		ctMethod.getMethodInfo().addAttribute(methodAttr);

		// 添加参数注解
		ParameterAnnotationsAttribute parameterAtrribute = new ParameterAnnotationsAttribute(constPool,
				ParameterAnnotationsAttribute.visibleTag);
		Annotation paramAnnot = new Annotation("javax.jws.WebParam", constPool);
		paramAnnot.addMemberValue("name", new StringMemberValue("name", constPool));
		Annotation[][] paramArrays = new Annotation[1][1];
		paramArrays[0][0] = paramAnnot;
		parameterAtrribute.setAnnotations(paramArrays);

		ctMethod.getMethodInfo().addAttribute(parameterAtrribute);

		// 把生成的class文件写入文件
		byte[] byteArr = cc.toBytecode();
		FileOutputStream fos = new FileOutputStream(new File("D://DynamicHelloWorldImpl.class"));
		fos.write(byteArr);
		fos.close();

		return cc.toClass();
	}

	public Class<?> createDynamicInterface() throws Exception {

		ClassPool pool = ClassPool.getDefault();
		
		// 创建类
		CtClass declaring = pool.makeInterface("com.github.vindell.javassist.IDynamicHelloWorld");
		//declaring.setSuperclass(pool.getCtClass("java.lang.Cloneable"));
		
		ClassFile ccFile = declaring.getClassFile();
		ConstPool constPool = ccFile.getConstPool();

		// 添加类注解
		AnnotationsAttribute bodyAttr = new AnnotationsAttribute(constPool, AnnotationsAttribute.visibleTag);
		Annotation bodyAnnot = new Annotation("javax.jws.WebService", constPool);
		bodyAnnot.addMemberValue("name", new StringMemberValue("HelloWoldService", constPool));
		bodyAttr.addAnnotation(bodyAnnot);

		ccFile.addAttribute(bodyAttr);
		
		// 创建抽象方法
		
		// 参数： 1：返回类型 2：方法名称 3：传入参数类型 4：所属类CtClass
		CtClass[] parameters = new CtClass[] { pool.get("java.lang.String") };
		CtClass[] exceptions = new CtClass[] { pool.get("java.lang.Exception") };
		CtMethod ctMethod = CtNewMethod.abstractMethod(pool.get("java.lang.String"), "sayHello", parameters , exceptions, declaring);
		declaring.addMethod(ctMethod);
		
		// 添加方法注解
		AnnotationsAttribute methodAttr = new AnnotationsAttribute(constPool, AnnotationsAttribute.visibleTag);
		Annotation methodAnnot = new Annotation("javax.jws.WebMethod", constPool);
		methodAnnot.addMemberValue("operationName", new StringMemberValue("sayHelloWorld", constPool));
		methodAttr.addAnnotation(methodAnnot);

		Annotation resultAnnot = new Annotation("javax.jws.WebResult", constPool);
		resultAnnot.addMemberValue("name", new StringMemberValue("result", constPool));
		methodAttr.addAnnotation(resultAnnot);
		
		ctMethod.getMethodInfo().addAttribute(methodAttr);

		// 添加参数注解
		ParameterAnnotationsAttribute parameterAtrribute = new ParameterAnnotationsAttribute(constPool,
				ParameterAnnotationsAttribute.visibleTag);
		Annotation paramAnnot = new Annotation("javax.jws.WebParam", constPool);
		paramAnnot.addMemberValue("name", new StringMemberValue("name", constPool));
		Annotation[][] paramArrays = new Annotation[1][1];
		paramArrays[0][0] = paramAnnot;
		parameterAtrribute.setAnnotations(paramArrays);

		ctMethod.getMethodInfo().addAttribute(parameterAtrribute);

		// 把生成的class文件写入文件
		byte[] byteArr = declaring.toBytecode();
		FileOutputStream fos = new FileOutputStream(new File("D://IDynamicHelloWorld.class"));
		fos.write(byteArr);
		fos.close();
		
		return declaring.toClass();

	}

}
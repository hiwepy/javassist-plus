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

import java.lang.reflect.Array;
import java.lang.reflect.Method;

import com.github.vindell.javassist.bytecode.visit.ArrayIndexAssigningVisitor;

import javassist.CtClass;
import javassist.CtField;
import javassist.CtMethod;
import javassist.NotFoundException;
import javassist.bytecode.AnnotationsAttribute;
import javassist.bytecode.ClassFile;
import javassist.bytecode.ConstPool;
import javassist.bytecode.FieldInfo;
import javassist.bytecode.MethodInfo;
import javassist.bytecode.ParameterAnnotationsAttribute;
import javassist.bytecode.SignatureAttribute;
import javassist.bytecode.annotation.Annotation;
import javassist.bytecode.annotation.AnnotationMemberValue;
import javassist.bytecode.annotation.ArrayMemberValue;
import javassist.bytecode.annotation.BooleanMemberValue;
import javassist.bytecode.annotation.ByteMemberValue;
import javassist.bytecode.annotation.CharMemberValue;
import javassist.bytecode.annotation.ClassMemberValue;
import javassist.bytecode.annotation.DoubleMemberValue;
import javassist.bytecode.annotation.EnumMemberValue;
import javassist.bytecode.annotation.FloatMemberValue;
import javassist.bytecode.annotation.IntegerMemberValue;
import javassist.bytecode.annotation.LongMemberValue;
import javassist.bytecode.annotation.MemberValue;
import javassist.bytecode.annotation.ShortMemberValue;
import javassist.bytecode.annotation.StringMemberValue;

/**
 * 
 * @className ： JavassistUtils
 * @description ： TODO(描述这个类的作用)
 * @author ： <a href="https://github.com/vindell">vindell</a>
 * @see http://blog.csdn.net/youaremoon/article/details/50766972
 * @see https://my.oschina.net/GameKing/blog/794580
 * @see http://www.codeweblog.com/%E5%85%B3%E4%BA%8Ejavassist-notfoundexception/
 */
public class JavassistUtils {

	public static boolean hasField(final CtClass ctclass, final String fieldName) {
		try {
			// 检查字段是否已经定义
			CtField field = ctclass.getDeclaredField(fieldName);
			if (field != null) {
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
			if (paramTypes != null && paramTypes.length > 0) {
				// 检查方法是否已经定义
				CtMethod method = ctclass.getDeclaredMethod(methodName, paramTypes);
				if (method != null) {
					return true;
				}
			} else {
				// 检查方法是否已经定义
				CtMethod method = ctclass.getDeclaredMethod(methodName);
				if (method != null) {
					return true;
				}
			}
			return false;
		} catch (NotFoundException e) {
			return false;
		}
	}

	public static void addClassAnnotation(CtClass clazz, javassist.bytecode.annotation.Annotation annotation) {
		ClassFile classFile = clazz.getClassFile();
		AnnotationsAttribute attribute = (AnnotationsAttribute) classFile.getAttribute(AnnotationsAttribute.visibleTag);
		if (attribute == null) {
			attribute = new AnnotationsAttribute(classFile.getConstPool(), AnnotationsAttribute.visibleTag);
		}
		attribute.addAnnotation(annotation);
		classFile.addAttribute(attribute);
	}

	public static void addFieldAnnotation(CtField field, javassist.bytecode.annotation.Annotation annotation) {
		FieldInfo fieldInfo = field.getFieldInfo();
		AnnotationsAttribute attribute = (AnnotationsAttribute) fieldInfo.getAttribute(AnnotationsAttribute.visibleTag);
		if (attribute == null) {
			attribute = new AnnotationsAttribute(fieldInfo.getConstPool(), AnnotationsAttribute.visibleTag);
		}
		attribute.addAnnotation(annotation);
		fieldInfo.addAttribute(attribute);
	}

	public static void addSignature(CtField field, String signature) {
		FieldInfo fieldInfo = field.getFieldInfo();
		ConstPool constPool = fieldInfo.getConstPool();
		SignatureAttribute signatureAttribute = new SignatureAttribute(constPool, signature);
		fieldInfo.addAttribute(signatureAttribute);
	}

	public static void addSignature(CtMethod method, String signature) {
		MethodInfo methodInfo = method.getMethodInfo();
		ConstPool constPool = methodInfo.getConstPool();
		SignatureAttribute signatureAttribute = new SignatureAttribute(constPool, signature);
		methodInfo.addAttribute(signatureAttribute);
	}

	/**
	 * Copies the provided annotation into the provided const pool.
	 *
	 * @param annotation
	 * @param constPool
	 * @return
	 * @throws NotFoundException
	 */
	public static Annotation cloneAnnotation(Annotation annotation, final ConstPool constPool)
			throws NotFoundException {

		Annotation ret = new Annotation(annotation.getTypeName(), constPool);

		if (annotation.getMemberNames() != null) {

			for (Object m : annotation.getMemberNames()) {

				final String memberName = (String) m;

				MemberValue origValue = annotation.getMemberValue(memberName);
				final MemberValue[] newValue = new MemberValue[1];

				origValue.accept(new ArrayIndexAssigningVisitor(newValue, 0, constPool));

				ret.addMemberValue(memberName, newValue[0]);
			}
		}

		return ret;
	}

	public static AnnotationsAttribute copyAnnotations(AnnotationsAttribute annotations, ConstPool constPool)
			throws NotFoundException {
		if (annotations != null) {

			Annotation[] origAnnotations = annotations.getAnnotations();
			Annotation[] newClassAnnotations = new Annotation[origAnnotations.length];
			for (int i = 0; i < newClassAnnotations.length; ++i) {
				newClassAnnotations[i] = cloneAnnotation(origAnnotations[i], constPool);
			}

			AnnotationsAttribute newAnnotations = new AnnotationsAttribute(constPool, AnnotationsAttribute.visibleTag);
			newAnnotations.setAnnotations(newClassAnnotations);

			return newAnnotations;
		}

		return null;
	}

	public static ParameterAnnotationsAttribute copyParameterAnnotations(
			ParameterAnnotationsAttribute parameterAnnotations, ConstPool constPool, int fromIndex)
			throws NotFoundException {

		if (parameterAnnotations != null) {
			Annotation[][] originalAnnotations = parameterAnnotations.getAnnotations();

			// return early if there are no annotations to copy
			if (originalAnnotations.length - fromIndex <= 0) {
				return null;
			}

			Annotation[][] newParameterAnnotations = new Annotation[originalAnnotations.length - fromIndex][];

			for (int i = fromIndex; i < originalAnnotations.length; i++) {
				newParameterAnnotations[i - fromIndex] = new Annotation[originalAnnotations[i].length];
				for (int j = 0; j < originalAnnotations[i].length; ++j) {
					Annotation origAnnotation = originalAnnotations[i][j];

					newParameterAnnotations[i - fromIndex][j] = cloneAnnotation(origAnnotation, constPool);
				}
			}

			ParameterAnnotationsAttribute newAnnotationsAttribute = new ParameterAnnotationsAttribute(constPool,
					ParameterAnnotationsAttribute.visibleTag);

			newAnnotationsAttribute.setAnnotations(newParameterAnnotations);

			return newAnnotationsAttribute;
		}

		return null;
	}

	public static Annotation createAnnotation(java.lang.annotation.Annotation annotation, ConstPool cp) {
		try {
			Annotation a = new Annotation(annotation.annotationType().getName(), cp);
			for (Method m : annotation.annotationType().getDeclaredMethods()) {
				Object val = m.invoke(annotation);
				a.addMemberValue(m.getName(), createMemberValue(cp, m.getReturnType(), val));
			}
			return a;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Makes an instance of <code>MemberValue</code>.
	 * @param cp 	the constant pool table.
	 * @param type 	the type of the member.
	 * @return the member value
	 */
	public static MemberValue createMemberValue(ConstPool cp, Class<?> type) {
		if (type == int.class) {
			return new IntegerMemberValue(cp);
		} else if (type == short.class) {
			return new ShortMemberValue(cp);
		} else if (type == long.class) {
			return new LongMemberValue(cp);
		} else if (type == byte.class) {
			return new ByteMemberValue(cp);
		} else if (type == float.class) {
			return new FloatMemberValue(cp);
		} else if (type == double.class) {
			return new DoubleMemberValue(cp);
		} else if (type == char.class) {
			return new CharMemberValue(cp);
		} else if (type == boolean.class) {
			return new BooleanMemberValue(cp);
		} else if (type == String.class) {
			return new StringMemberValue(cp);
		} else if (type == Class.class) {
			return new ClassMemberValue(cp);
		} else if (type.isEnum()) {
			EnumMemberValue e = new EnumMemberValue(cp);
			e.setType(type.getName());
			return e;
		} else if (type.isAnnotation()) {
			AnnotationMemberValue a = new AnnotationMemberValue(cp);
			return a;
		} else if (type.isArray()) {
			Class<?> arrayType = type.getComponentType();
			MemberValue arrayval = createMemberValue(cp, arrayType);
			ArrayMemberValue ret = new ArrayMemberValue(arrayval, cp);
			return ret;
		}
		throw new RuntimeException("Invalid array type " + type + " with no value ");
	}

	/**
	 * Makes an instance of <code>MemberValue</code>.
	 * @param cp 	the constant pool table.
	 * @param type 	the type of the member.
	 * @param val 	the value of the member.
	 * @return the member value
	 */
	public static MemberValue createMemberValue(ConstPool cp, Class<?> type, Object val) {
		if (type == int.class) {
			return new IntegerMemberValue(cp, (Integer) val);
		} else if (type == short.class) {
			return new ShortMemberValue((Short) val, cp);
		} else if (type == long.class) {
			return new LongMemberValue((Long) val, cp);
		} else if (type == byte.class) {
			return new ByteMemberValue((Byte) val, cp);
		} else if (type == float.class) {
			return new FloatMemberValue((Float) val, cp);
		} else if (type == double.class) {
			return new DoubleMemberValue((Double) val, cp);
		} else if (type == char.class) {
			return new CharMemberValue((Character) val, cp);
		} else if (type == boolean.class) {
			return new BooleanMemberValue((Boolean) val, cp);
		} else if (type == String.class) {
			return new StringMemberValue((String) val, cp);
		} else if (type == Class.class) {
			return new ClassMemberValue(((Class<?>) val).getName(), cp);
		} else if (type.isEnum()) {
			EnumMemberValue e = new EnumMemberValue(cp);
			e.setType(type.getName());
			e.setValue(((Enum<?>) val).name());
			return e;
		} else if (type.isAnnotation()) {
			Annotation annot = createAnnotation((java.lang.annotation.Annotation) val, cp);
			return new AnnotationMemberValue(annot, cp);
		} else if (type.isArray()) {
			Class<?> arrayType = type.getComponentType();
			int length = Array.getLength(val);
			MemberValue arrayval = createMemberValue(cp, arrayType);
			ArrayMemberValue ret = new ArrayMemberValue(arrayval, cp);
			MemberValue[] vals = new MemberValue[length];
			for (int i = 0; i < length; ++i) {
				vals[i] = createMemberValue(cp, arrayType, Array.get(val, i));
			}
			ret.setValue(vals);
			return ret;
		}
		throw new RuntimeException("Invalid array type " + type + " value: " + val);
	}

	/**
	 * Makes an instance of <code>MemberValue</code>.
	 * @param cp 	the constant pool table.
	 * @param type 	the type of the member.
	 * @return the member value
	 */
	public static MemberValue createMemberValue(ConstPool cp, CtClass type) throws NotFoundException {
		return javassist.bytecode.annotation.Annotation.createMemberValue(cp, type);
	}

	/**
	 * Makes an instance of <code>MemberValue</code>.
	 * @param cp 	the constant pool table.
	 * @param type 	the type of the member.
	 * @param val 	the value of the member.
	 * @return the member value
	 */
	public static MemberValue createMemberValue(ConstPool cp, CtClass type, Object val) throws NotFoundException {

		if (type == CtClass.booleanType) {
			return new BooleanMemberValue((Boolean) val, cp);
		} else if (type == CtClass.byteType) {
			return new ByteMemberValue((Byte) val, cp);
		} else if (type == CtClass.charType) {
			return new CharMemberValue((Character) val, cp);
		} else if (type == CtClass.shortType) {
			return new ShortMemberValue((Short) val, cp);
		} else if (type == CtClass.intType) {
			return new IntegerMemberValue(cp, (Integer) val);
		} else if (type == CtClass.longType) {
			return new LongMemberValue((Long) val, cp);
		} else if (type == CtClass.floatType) {
			return new FloatMemberValue((Float) val, cp);
		} else if (type == CtClass.doubleType) {
			return new DoubleMemberValue((Double) val, cp);
		} else if (type.getName().equals("java.lang.Class")) {
			return new ClassMemberValue(((Class<?>) val).getName(), cp);
		} else if (type.getName().equals("java.lang.String")) {
			return new StringMemberValue((String) val, cp);
		} else if (type.isAnnotation()) {
			return new AnnotationMemberValue((Annotation) val, cp);
		} else if (type.isArray()) {
			CtClass arrayType = type.getComponentType();
			int length = Array.getLength(val);
			MemberValue arrayval = createMemberValue(cp, arrayType);
			ArrayMemberValue ret = new ArrayMemberValue(arrayval, cp);
			MemberValue[] vals = new MemberValue[length];
			for (int i = 0; i < length; ++i) {
				vals[i] = createMemberValue(cp, arrayType, Array.get(val, i));
			}
			ret.setValue(vals);
			return ret;
		} else if (type.isInterface()) {
			Annotation info = new Annotation(cp, type);
			return new AnnotationMemberValue(info, cp);
		} else if (type.isEnum()) {
			// treat as enum. I know this is not typed,
			// but JBoss has an Annotation Compiler for JDK 1.4
			// and I want it to work with that. - Bill Burke
			EnumMemberValue emv = new EnumMemberValue(cp);
			emv.setType(type.getName());
			emv.setValue(((Enum<?>) val).name());
			return emv;
		}
		throw new RuntimeException("Invalid array type " + type + " value: " + val);
	}

}

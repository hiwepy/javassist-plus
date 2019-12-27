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
package com.github.hiwepy.javassist.bytecode;
/*
* Copyright 2012, Stuart Douglas, and individual contributors as indicated
* by the @authors tag.
*
* This is free software; you can redistribute it and/or modify it
* under the terms of the GNU Lesser General Public License as
* published by the Free Software Foundation; either version 2.1 of
* the License, or (at your option) any later version.
*
* This software is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
* Lesser General Public License for more details.
*
* You should have received a copy of the GNU Lesser General Public
* License along with this software; if not, write to the Free
* Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
* 02110-1301 USA, or see the FSF site: http://www.fsf.org.
*/

import java.lang.reflect.Method;

import org.apache.commons.lang3.builder.Builder;

import com.github.hiwepy.javassist.utils.JavassistUtils;

import javassist.CtClass;
import javassist.CtField;
import javassist.bytecode.ConstPool;
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
import javassist.bytecode.annotation.ShortMemberValue;
import javassist.bytecode.annotation.StringMemberValue;

/**
 * Created a Javassist Annotation from a java one
 * 
 * @author stuart
 */
@SuppressWarnings("rawtypes")
public class CtAnnotationBuilder implements Builder<Annotation> {

	private Annotation annotation;
	private ConstPool constPool;

	public static CtAnnotationBuilder create(Class<? extends java.lang.annotation.Annotation> annotation,
			ConstPool constPool) {
		return new CtAnnotationBuilder(annotation, constPool);
	}

	public static CtAnnotationBuilder create(java.lang.annotation.Annotation annotation, ConstPool constPool) {
		return new CtAnnotationBuilder(annotation, constPool);
	}

	protected CtAnnotationBuilder(Class<? extends java.lang.annotation.Annotation> annotation, ConstPool constPool) {
		this.annotation = new Annotation(annotation.getName(), constPool);
		this.constPool = constPool;
	}
	
	protected CtAnnotationBuilder(java.lang.annotation.Annotation annotation, ConstPool constPool) {
		try {
			this.annotation = new Annotation(annotation.annotationType().getName(), constPool);
			for (Method m : annotation.annotationType().getDeclaredMethods()) {
				Object val = m.invoke(annotation);
				this.annotation.addMemberValue(m.getName(), JavassistUtils.createMemberValue(constPool, m.getReturnType(), val));
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		this.constPool = constPool;
	}

	public CtAnnotationBuilder addAnnotationMember(String name, Annotation value) {
		annotation.addMemberValue(name, new AnnotationMemberValue(value, constPool));
		return this;
	}
	
	public CtAnnotationBuilder addAnnotationMember(String name, Annotation[] values) {
		ArrayMemberValue member = new ArrayMemberValue(constPool);
		AnnotationMemberValue[] members = new AnnotationMemberValue[values.length];
		for (int i = 0; i < values.length; i++) {
			members[i] = new AnnotationMemberValue(values[i], constPool);
		}
		member.setValue(members);
		annotation.addMemberValue(name, member);
		return this;
	}
	
	public CtAnnotationBuilder addBooleanMember(String name, boolean value) {
		annotation.addMemberValue(name, new BooleanMemberValue(value, constPool));
		return this;
	}
	
	public CtAnnotationBuilder addBooleanMember(String name, boolean[] values) {
		ArrayMemberValue member = new ArrayMemberValue(constPool);
		BooleanMemberValue[] members = new BooleanMemberValue[values.length];
		for (int i = 0; i < values.length; i++) {
			members[i] = new BooleanMemberValue(values[i], constPool);
		}
		member.setValue(members);
		annotation.addMemberValue(name, member);
		return this;
	}
	
	public CtAnnotationBuilder addByteMember(String name, byte value) {
		annotation.addMemberValue(name, new ByteMemberValue(value, constPool));
		return this;
	}
	
	public CtAnnotationBuilder addByteMember(String name, byte[] values) {
		ArrayMemberValue member = new ArrayMemberValue(constPool);
		ByteMemberValue[] members = new ByteMemberValue[values.length];
		for (int i = 0; i < values.length; i++) {
			members[i] = new ByteMemberValue(values[i], constPool);
		}
		member.setValue(members);
		annotation.addMemberValue(name, member);
		return this;
	}
	
	public CtAnnotationBuilder addCharMember(String name, char value) {
		annotation.addMemberValue(name, new CharMemberValue(value, constPool));
		return this;
	}

	public CtAnnotationBuilder addCharMember(String name, char[] values) {
		ArrayMemberValue member = new ArrayMemberValue(constPool);
		CharMemberValue[] members = new CharMemberValue[values.length];
		for (int i = 0; i < values.length; i++) {
			members[i] = new CharMemberValue(values[i], constPool);
		}
		member.setValue(members);
		annotation.addMemberValue(name, member);
		return this;
	}
	
	public CtAnnotationBuilder addClassMember(String name, String value) {
		ClassMemberValue classValue = new ClassMemberValue(value, constPool);
		annotation.addMemberValue(name, classValue);
		return this;
	}
	
	public CtAnnotationBuilder addClassMember(String name, String[] values) {
		ArrayMemberValue member = new ArrayMemberValue(constPool);
		ClassMemberValue[] members = new ClassMemberValue[values.length];
		for (int i = 0; i < values.length; i++) {
			members[i] = new ClassMemberValue(values[i], constPool);
		}
		member.setValue(members);
		annotation.addMemberValue(name, member);
		return this;
	}
	
	public CtAnnotationBuilder addDoubleMember(String name, double value) {
		annotation.addMemberValue(name, new DoubleMemberValue(value, constPool));
		return this;
	}

	public CtAnnotationBuilder addDoubleMember(String name, double[] values) {
		ArrayMemberValue member = new ArrayMemberValue(constPool);
		DoubleMemberValue[] members = new DoubleMemberValue[values.length];
		for (int i = 0; i < values.length; i++) {
			members[i] = new DoubleMemberValue(values[i], constPool);
		}
		member.setValue(members);
		annotation.addMemberValue(name, member);
		return this;
	}
	
	public CtAnnotationBuilder addEnumMember(String name, Enum value) {
		EnumMemberValue enumValue = new EnumMemberValue(constPool);
		enumValue.setType(value.getClass().getName());
		enumValue.setValue(value.name());
		annotation.addMemberValue(name, enumValue);
		return this;
	}
	
	public CtAnnotationBuilder addEnumMember(String name, Enum[] values) {
		ArrayMemberValue member = new ArrayMemberValue(constPool);
		EnumMemberValue[] members = new EnumMemberValue[values.length];
		for (int i = 0; i < values.length; i++) {
			
			EnumMemberValue enumValue = new EnumMemberValue(constPool);
			enumValue.setType(values[i].getClass().getName());
			enumValue.setValue(values[i].name());
			
			members[i] = enumValue;
		}
		member.setValue(members);
		annotation.addMemberValue(name, member);
		return this;
	}

	public CtAnnotationBuilder addFloatMember(String name, float value) {
		annotation.addMemberValue(name, new FloatMemberValue(value, constPool));
		return this;
	}
	
	public CtAnnotationBuilder addFloatMember(String name, float[] values) {
		ArrayMemberValue member = new ArrayMemberValue(constPool);
		FloatMemberValue[] members = new FloatMemberValue[values.length];
		for (int i = 0; i < values.length; i++) {
			members[i] = new FloatMemberValue(values[i], constPool);
		}
		member.setValue(members);
		annotation.addMemberValue(name, member);
		return this;
	}
	
	public CtAnnotationBuilder addIntegerMember(String name, int value) {
		annotation.addMemberValue(name, new IntegerMemberValue(constPool, value));
		return this;
	}
	
	public CtAnnotationBuilder addIntegerMember(String name, int[] values) {
		ArrayMemberValue member = new ArrayMemberValue(constPool);
		IntegerMemberValue[] members = new IntegerMemberValue[values.length];
		for (int i = 0; i < values.length; i++) {
			members[i] = new IntegerMemberValue(values[i], constPool);
		}
		member.setValue(members);
		annotation.addMemberValue(name, member);
		return this;
	}
	
	public CtAnnotationBuilder addLongMember(String name, long value) {
		annotation.addMemberValue(name, new LongMemberValue(value, constPool));
		return this;
	}

	public CtAnnotationBuilder addLongMember(String name, long[] values) {
		ArrayMemberValue member = new ArrayMemberValue(constPool);
		LongMemberValue[] members = new LongMemberValue[values.length];
		for (int i = 0; i < values.length; i++) {
			members[i] = new LongMemberValue(values[i], constPool);
		}
		member.setValue(members);
		annotation.addMemberValue(name, member);
		return this;
	}
	
	public CtAnnotationBuilder addShortMember(String name, short value) {
		annotation.addMemberValue(name, new ShortMemberValue(value, constPool));
		return this;
	}
	
	public CtAnnotationBuilder addShortMember(String name, short[] values) {
		ArrayMemberValue member = new ArrayMemberValue(constPool);
		ShortMemberValue[] members = new ShortMemberValue[values.length];
		for (int i = 0; i < values.length; i++) {
			members[i] = new ShortMemberValue(values[i], constPool);
		}
		member.setValue(members);
		annotation.addMemberValue(name, member);
		return this;
	}
	
	public CtAnnotationBuilder addStringMember(String name, String value) {
		annotation.addMemberValue(name, new StringMemberValue(value, constPool));
		return this;
	}
	
	public CtAnnotationBuilder addStringMember(String name, String[] values) {
		ArrayMemberValue member = new ArrayMemberValue(constPool);
		StringMemberValue[] members = new StringMemberValue[values.length];
		for (int i = 0; i < values.length; i++) {
			members[i] = new StringMemberValue(values[i], constPool);
		}
		member.setValue(members);
		annotation.addMemberValue(name, member);
		return this;
	}

	public void markClass(CtClass clazz) {
		JavassistUtils.addClassAnnotation(clazz, annotation);
	}

	public void markField(CtField field) {
		JavassistUtils.addFieldAnnotation(field, annotation);
	}

	@Override
	public Annotation build() {
		return annotation;
	}

}

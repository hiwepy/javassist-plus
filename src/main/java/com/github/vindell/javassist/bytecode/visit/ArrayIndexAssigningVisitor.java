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
package com.github.vindell.javassist.bytecode.visit;

import javassist.bytecode.ConstPool;
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
import javassist.bytecode.annotation.MemberValueVisitor;
import javassist.bytecode.annotation.ShortMemberValue;
import javassist.bytecode.annotation.StringMemberValue;

public class ArrayIndexAssigningVisitor implements MemberValueVisitor {
	
	private MemberValue[] array;
	private int index;
	private ConstPool constPool;

	public ArrayIndexAssigningVisitor(MemberValue[] array, int index, ConstPool constPool) {
		this.array = array;
		this.index = index;
		this.constPool = constPool;
	}

	@Override
	public void visitStringMemberValue(StringMemberValue node) {
		array[index] = new StringMemberValue(node.getValue(), constPool);
	}

	@Override
	public void visitShortMemberValue(ShortMemberValue node) {
		array[index] = new ShortMemberValue(node.getValue(), constPool);
	}

	@Override
	public void visitLongMemberValue(LongMemberValue node) {
		array[index] = new LongMemberValue(node.getValue(), constPool);
	}

	@Override
	public void visitIntegerMemberValue(IntegerMemberValue node) {
		array[index] = new IntegerMemberValue(constPool, node.getValue());
	}

	@Override
	public void visitFloatMemberValue(FloatMemberValue node) {
		array[index] = new FloatMemberValue(node.getValue(), constPool);
	}

	@Override
	public void visitEnumMemberValue(EnumMemberValue node) {
		EnumMemberValue val = new EnumMemberValue(constPool);
		val.setType(node.getType());
		val.setValue(node.getValue());
		array[index] = val;
	}

	@Override
	public void visitDoubleMemberValue(DoubleMemberValue node) {
		array[index] = new DoubleMemberValue(node.getValue(), constPool);
	}

	@Override
	public void visitClassMemberValue(ClassMemberValue node) {
		array[index] = new ClassMemberValue(node.getValue(), constPool);
	}

	@Override
	public void visitCharMemberValue(CharMemberValue node) {
		array[index] = new CharMemberValue(node.getValue(), constPool);
	}

	@Override
	public void visitByteMemberValue(ByteMemberValue node) {
		array[index] = new ByteMemberValue(node.getValue(), constPool);
	}

	@Override
	public void visitBooleanMemberValue(BooleanMemberValue node) {
		array[index] = new BooleanMemberValue(node.getValue(), constPool);
	}

	@Override
	public void visitArrayMemberValue(ArrayMemberValue node) {
		ArrayMemberValue val = new ArrayMemberValue(node.getType(), constPool);
		MemberValue[] newVals = new MemberValue[node.getValue().length];
		for (int i = 0; i < node.getValue().length; ++i) {
			node.getValue()[i].accept(new ArrayIndexAssigningVisitor(newVals, i, constPool));
		}

		val.setValue(newVals);
		array[index] = val;
	}

	@Override
	public void visitAnnotationMemberValue(AnnotationMemberValue node) {
		array[index] = new AnnotationMemberValue(node.getValue(), constPool);
	}
}
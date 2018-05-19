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

/**
 * Comment
 * @author <a href="mailto:bill@jboss.org">Bill Burke</a>
 * @version $Revision: 37406 $
 **/
public class MemberValueCreationVisitor implements MemberValueVisitor {
	
	public MemberValue value;
	private ConstPool cp;

	public MemberValueCreationVisitor(ConstPool cp) {
		this.cp = cp;
	}

	public void visitAnnotationMemberValue(AnnotationMemberValue annotationMemberValue) {
		value = new AnnotationMemberValue(cp);
	}

	public void visitArrayMemberValue(ArrayMemberValue arrayMemberValue) {
		throw new RuntimeException("NOT IMPLEMENTED");
	}

	public void visitBooleanMemberValue(BooleanMemberValue booleanMemberValue) {
		value = new BooleanMemberValue(cp);
	}

	public void visitByteMemberValue(ByteMemberValue byteMemberValue) {
		value = new ByteMemberValue(cp);
	}

	public void visitCharMemberValue(CharMemberValue charMemberValue) {
		value = new CharMemberValue(cp);
	}

	public void visitDoubleMemberValue(DoubleMemberValue doubleMemberValue) {
		value = new DoubleMemberValue(cp);
	}

	public void visitEnumMemberValue(EnumMemberValue enumMemberValue) {
		value = new EnumMemberValue(cp);
	}

	public void visitFloatMemberValue(FloatMemberValue floatMemberValue) {
		value = new FloatMemberValue(cp);
	}

	public void visitIntegerMemberValue(IntegerMemberValue integerMemberValue) {
		value = new IntegerMemberValue(cp);
	}

	public void visitLongMemberValue(LongMemberValue longMemberValue) {
		value = new LongMemberValue(cp);
	}

	public void visitShortMemberValue(ShortMemberValue shortMemberValue) {
		value = new ShortMemberValue(cp);
	}

	public void visitStringMemberValue(StringMemberValue stringMemberValue) {
		value = new StringMemberValue(cp);
	}

	public void visitClassMemberValue(ClassMemberValue classMemberValue) {
		value = new ClassMemberValue(cp);
	}
	
}

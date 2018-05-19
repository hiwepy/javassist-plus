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
package com.github.vindell.javassist.bytecode;

import java.util.ArrayList;
import java.util.List;

//a quick and dirty method signature parser
//see http://docs.oracle.com/javase/specs/jvms/se7/html/jvms-4.html#jvms-4.3.4
public class MethodSignature {

	public String returnType;
	public List<String> paramTypes = new ArrayList<String>();
	public String typeParameters;
	public String exceptionTypes;

	public static MethodSignature parse(String signature) {
		int startParams = signature.indexOf('(') + 1;
		int endParams = signature.indexOf(')');
		int startExceptions = signature.indexOf('^');

		MethodSignature sig = new MethodSignature();
		sig.typeParameters = signature.substring(0, startParams - 1);
		if (startExceptions == -1) {
			sig.returnType = signature.substring(endParams + 1);
			sig.exceptionTypes = "";
		} else {
			sig.returnType = signature.substring(endParams + 1, startExceptions);
			sig.exceptionTypes = signature.substring(startExceptions);
		}

		int idx = startParams;
		while (idx < endParams) {
			int end = findEndOfTypeSignature(idx, signature);
			sig.paramTypes.add(signature.substring(idx, end));
			idx = end;
		}

		return sig;
	}

	private static int findEndOfTypeSignature(int idx, String signature) {
		int c = signature.charAt(idx);

		switch (c) {
		case 'L':
			return findEndOfClassSignature(idx, signature);
		case '[':
			return findEndOfTypeSignature(idx + 1, signature);
		case 'T':
			return signature.indexOf(';', idx + 1) + 1;
		default:
			return idx + 1;
		}
	}

	private static int findEndOfClassSignature(int indexOfL, String signature) {
		int idx = indexOfL + 1;

		int genericDeclDepth = 0;

		while (idx < signature.length()) {
			boolean sigComplete = false;

			char c = signature.charAt(idx++);
			switch (c) {
			case '<':
				genericDeclDepth++;
				break;
			case '>':
				genericDeclDepth--;
				break;
			case ';':
				sigComplete = genericDeclDepth == 0;
				break;
			}

			if (sigComplete) {
				break;
			}
		}

		return idx;
	}

	@Override
	public String toString() {
		StringBuilder bld = new StringBuilder(typeParameters);
		bld.append("(");
		for (String p : paramTypes) {
			bld.append(p);
		}
		bld.append(")");
		bld.append(returnType);
		bld.append(exceptionTypes);
		return bld.toString();
	}
	
}
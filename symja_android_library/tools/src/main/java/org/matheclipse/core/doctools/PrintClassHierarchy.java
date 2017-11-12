package org.matheclipse.core.doctools;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import org.matheclipse.core.expression.AST;
import org.matheclipse.core.expression.AST0;
import org.matheclipse.core.expression.AST1;
import org.matheclipse.core.expression.AST2;
import org.matheclipse.core.expression.AST3;
import org.matheclipse.core.expression.ASTRealMatrix;
import org.matheclipse.core.expression.ASTRealVector;
import org.matheclipse.core.expression.ApcomplexNum;
import org.matheclipse.core.expression.ApfloatNum;
import org.matheclipse.core.expression.BigFractionSym;
import org.matheclipse.core.expression.BigIntegerSym;
import org.matheclipse.core.expression.BuiltInSymbol;
import org.matheclipse.core.expression.ComplexNum;
import org.matheclipse.core.expression.ComplexSym;
import org.matheclipse.core.expression.FractionSym;
import org.matheclipse.core.expression.IntegerSym;
import org.matheclipse.core.expression.NILPointer;
import org.matheclipse.core.expression.Num;
import org.matheclipse.core.expression.Pattern;
import org.matheclipse.core.expression.PatternSequence;
import org.matheclipse.core.expression.StringX;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IASTMutable;

public class PrintClassHierarchy {
	private static final String PADDING = "        ";
	private static final String PADDING_WITH_COLUMN = "   |    ";
	private static final String PADDING_WITH_ENTRY = "   |--- ";
	private static final String BASE_CLASS = Object.class.getName();

	private final Map<String, List<String>> subClazzEntries = new HashMap<String, List<String>>();

	public static void main(final String[] args) {
		new PrintClassHierarchy(IAST.class, IASTMutable.class, AST0.class, AST1.class, AST2.class, AST3.class,
				AST.class, ASTRealMatrix.class, ASTRealVector.class, IASTAppendable.class, ComplexSym.class,
				IntegerSym.class, BigIntegerSym.class, FractionSym.class, BigFractionSym.class, ComplexNum.class,
				Num.class, ApcomplexNum.class, ApfloatNum.class, Pattern.class, PatternSequence.class,
				BuiltInSymbol.class, NILPointer.class, StringX.class).printHierarchy();
	}

	public PrintClassHierarchy(final Class<?>... clazzes) {
		// get all entries of tree
		traverseClasses(clazzes);
	}

	public void printHierarchy() {
		// print collected entries as ASCII tree
		printHierarchy(BASE_CLASS, new Stack<Boolean>());
	}

	private void printHierarchy(final String clazzName, final Stack<Boolean> moreClassesInHierarchy) {
		if (!moreClassesInHierarchy.empty()) {
			for (final Boolean hasColumn : moreClassesInHierarchy.subList(0, moreClassesInHierarchy.size() - 1)) {
				System.out.print(hasColumn.booleanValue() ? PADDING_WITH_COLUMN : PADDING);
			}
		}

		if (!moreClassesInHierarchy.empty()) {
			System.out.print(PADDING_WITH_ENTRY);
		}

		System.out.println(clazzName);

		if (subClazzEntries.containsKey(clazzName)) {
			final List<String> list = subClazzEntries.get(clazzName);

			for (int i = 0; i < list.size(); i++) {
				// if there is another class that comes beneath the next class,
				// flag this level
				moreClassesInHierarchy.push(new Boolean(i < list.size() - 1));

				printHierarchy(list.get(i), moreClassesInHierarchy);

				moreClassesInHierarchy.removeElementAt(moreClassesInHierarchy.size() - 1);
			}
		}
	}

	private void traverseClasses(final Class<?>... clazzes) {
		// do the traverseClasses on each provided class (possible since Java 8)
		List<Class<?>> list = Arrays.asList(clazzes);// .forEach(c ->
														// traverseClasses(c,
														// 0));
		for (int i = 0; i < list.size(); i++) {
			traverseClasses(list.get(i), 0);
		}
	}

	private void traverseClasses(final Class<?> clazz, final int level) {
		final Class<?> superClazz = clazz.getSuperclass();

		if (superClazz == null) {
			// we arrived java.lang.Object
			return;
		}

		final String name = clazz.getName();
		final String superName = superClazz.getName();

		if (subClazzEntries.containsKey(superName)) {
			final List<String> list = subClazzEntries.get(superName);

			if (!list.contains(name)) {
				list.add(name);
				Collections.sort(list); // SortedList
			}
		} else {
			subClazzEntries.put(superName, new ArrayList<String>(Arrays.asList(name)));
		}

		traverseClasses(superClazz, level + 1);
	}
}
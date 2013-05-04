package org.matheclipse.core.eval;

import java.io.File;
import java.net.URL;

import org.matheclipse.core.eval.interfaces.AbstractArg2;
import org.matheclipse.core.eval.interfaces.AbstractArgMultiple;
import org.matheclipse.core.eval.interfaces.AbstractNonOrderlessArgMultiple;
import org.matheclipse.core.eval.interfaces.AbstractSymbolEvaluator;
import org.matheclipse.core.eval.interfaces.IFunctionEvaluator;
import org.matheclipse.core.expression.F;

/**
 * Generate java sources for initializing
 */
public class SourceGenerator {

	public SourceGenerator() {
	}

	public final static String DS0 = "public static ISymbol ";

	public final static String CS0 = " = F.predefinedSymbol(\"";

	public final static String S0 = "\n  public static IAST ";

	public final static String UNA0 = "(IExpr a0) {\n    return unary(F.";

	public final static String UNA1 = ", a0);\n  }";

	public final static String BIN0 = "(IExpr a0, IExpr a1) {\n    return binary(F.";

	public final static String BIN1 = ", a0, a1);\n  }";

	public final static String TER0 = "(IExpr a0, IExpr a1, IExpr a2) {\n    return ternary(F.";

	public final static String TER1 = ", a0, a1, a2);\n  }";

	public final static String EVS0 = "\n  public static IAST EV";

	public final static String EVUNA0 = "(IExpr a0) {\n    return eval(F.";

	public final static String EVBIN0 = "(IExpr a0, IExpr a1) {\n    return eval(F.";

	public final static String EVTER0 = "(IExpr a0, IExpr a1, IExpr a2) {\n    return eval(F.";

	public static String createUnary(final String oper) {
		return S0 + oper + UNA0 + oper + UNA1;
	}

	public static String createBinary(final String oper) {
		return S0 + oper + BIN0 + oper + BIN1;
	}

	public static String createTernary(final String oper) {
		return S0 + oper + TER0 + oper + TER1;
	}

	public static String createEvalUnary(final String oper) {
		return EVS0 + oper + EVUNA0 + oper + UNA1;
	}

	public static String createEvalBinary(final String oper) {
		return EVS0 + oper + EVBIN0 + oper + BIN1;
	}

	public static String createEvalTernary(final String oper) {
		return EVS0 + oper + EVTER0 + oper + TER1;
	}

	public static void generateDeclareSymbols(final String pckgname) {
		String name = pckgname;
		if (!name.startsWith("/")) {
			name = "/" + name;
		}

		name = name.replace('.', '/');

		// Get a File object for the package
		final URL url = SourceGenerator.class.getResource(name);
		final File directory = new File(url.getFile());

		if (directory.exists()) {
			// Get the list of the files contained in the package
			final String[] files = directory.list();
			for (int i = 0; i < files.length; i++) {

				// we are only interested in .class files
				if (files[i].endsWith(".class")) {
					// removes the .class extension
					final String classname = files[i].substring(0, files[i].length() - 6);
					try {
						// Try to create an instance of the object
						final IFunctionEvaluator ev = (IFunctionEvaluator) Class.forName(pckgname + "." + classname).newInstance();
						System.out.println(DS0 + classname + ";");
						// System.out.println(DS0 + classname + CS0 + classname
						// + "\");");
					} catch (final ClassCastException e) {
						// e.printStackTrace();
					} catch (final ClassNotFoundException cnfex) {
						System.err.println(cnfex);
					} catch (final InstantiationException iex) {
						// We try to instantiate an interface
						// or an object that does not have a
						// default constructor
					} catch (final IllegalAccessException iaex) {
						// The class is not public
					}
				}
			}
		}
	}

	public static void generateCreateSymbols(final String pckgname) {
		String name = pckgname;
		if (!name.startsWith("/")) {
			name = "/" + name;
		}

		name = name.replace('.', '/');

		// Get a File object for the package
		final URL url = SourceGenerator.class.getResource(name);
		final File directory = new File(url.getFile());

		if (directory.exists()) {
			// Get the list of the files contained in the package
			final String[] files = directory.list();
			for (int i = 0; i < files.length; i++) {

				// we are only interested in .class files
				if (files[i].endsWith(".class")) {
					// removes the .class extension
					final String classname = files[i].substring(0, files[i].length() - 6);
					try {
						// Try to create an instance of the object
						final IFunctionEvaluator ev = (IFunctionEvaluator) Class.forName(pckgname + "." + classname).newInstance();
						System.out.println(classname + CS0 + classname + "\");");
					} catch (final ClassCastException e) {
						// e.printStackTrace();
					} catch (final ClassNotFoundException cnfex) {
						System.err.println(cnfex);
					} catch (final InstantiationException iex) {
						// We try to instantiate an interface
						// or an object that does not have a
						// default constructor
					} catch (final IllegalAccessException iaex) {
						// The class is not public
					}
				}
			}
		}
	}

	public static void generateFunctions(final String pckgname) {
		String name = pckgname;
		if (!name.startsWith("/")) {
			name = "/" + name;
		}

		name = name.replace('.', '/');

		// Get a File object for the package
		final URL url = SourceGenerator.class.getResource(name);
		final File directory = new File(url.getFile());

		if (directory.exists()) {
			// Get the list of the files contained in the package
			final String[] files = directory.list();
			for (int i = 0; i < files.length; i++) {

				// we are only interested in .class files
				if (files[i].endsWith(".class")) {
					// removes the .class extension
					final String classname = files[i].substring(0, files[i].length() - 6);
					try {
						// Try to create an instance of the object
						final IFunctionEvaluator ev = (IFunctionEvaluator) Class.forName(pckgname + "." + classname).newInstance();
						if ((ev instanceof AbstractArgMultiple) || (ev instanceof AbstractNonOrderlessArgMultiple)) {
							System.out.println(createBinary(classname));
							System.out.println(createTernary(classname));
						} else if (ev instanceof AbstractArg2) {
							System.out.println(createBinary(classname));
						} else {
							System.out.println(createUnary(classname));
						}
					} catch (final ClassCastException e) {
					} catch (final ClassNotFoundException cnfex) {
						System.err.println(cnfex);
					} catch (final InstantiationException iex) {
						// We try to instantiate an interface
						// or an object that does not have a
						// default constructor
					} catch (final IllegalAccessException iaex) {
						// The class is not public
					}
				}
			}
		}
	}

	public static void generateEvalFunctions(final String pckgname) {
		String name = pckgname;
		if (!name.startsWith("/")) {
			name = "/" + name;
		}

		name = name.replace('.', '/');

		// Get a File object for the package
		final URL url = SourceGenerator.class.getResource(name);
		final File directory = new File(url.getFile());

		if (directory.exists()) {
			// Get the list of the files contained in the package
			final String[] files = directory.list();
			for (int i = 0; i < files.length; i++) {

				// we are only interested in .class files
				if (files[i].endsWith(".class")) {
					// removes the .class extension
					final String classname = files[i].substring(0, files[i].length() - 6);
					try {
						// Try to create an instance of the object
						final IFunctionEvaluator ev = (IFunctionEvaluator) Class.forName(pckgname + "." + classname).newInstance();
						if ((ev instanceof AbstractArgMultiple) || (ev instanceof AbstractNonOrderlessArgMultiple)) {
							System.out.println(createEvalBinary(classname));
							System.out.println(createEvalTernary(classname));
						} else if (ev instanceof AbstractArg2) {
							System.out.println(createEvalBinary(classname));
						} else {
							System.out.println(createEvalUnary(classname));
						}
					} catch (final ClassCastException e) {
					} catch (final ClassNotFoundException cnfex) {
						System.err.println(cnfex);
					} catch (final InstantiationException iex) {
						// We try to instantiate an interface
						// or an object that does not have a
						// default constructor
					} catch (final IllegalAccessException iaex) {
						// The class is not public
					}
				}
			}
		}
	}

	public static void generateCoreRules(final String pckgname) {
		String name = pckgname;
		if (!name.startsWith("/")) {
			name = "/" + name;
		}

		name = name.replace('.', '/');

		// Get a File object for the package
		final URL url = SourceGenerator.class.getResource(name);
		final File directory = new File(url.getFile());

		if (directory.exists()) {
			// Get the list of the files contained in the package
			final String[] files = directory.list();
			for (int i = 0; i < files.length; i++) {

				// we are only interested in .class files
				if (files[i].endsWith(".class")) {
					// removes the .class extension
					final String classname = files[i].substring(0, files[i].length() - 6);
					try {
						// Try to create an instance of the object
						Class.forName(pckgname + "." + classname).newInstance();
						System.out.println("  f.createSymbol(\"" + classname + "\");");
						F.$s(classname);
					} catch (final ClassNotFoundException cnfex) {
						System.err.println(cnfex);
					} catch (final InstantiationException iex) {
						// We try to instantiate an interface
						// or an object that does not have a
						// default constructor
					} catch (final IllegalAccessException iaex) {
						// The class is not public
					}
				}
			}
		}
	}

	public static void generateJFlexKeywords(final String packageName, int maximumNumberOfChars) {
		String name = packageName;
		if (!name.startsWith("/")) {
			name = "/" + name;
		}

		name = name.replace('.', '/');

		// Get a File object for the package
		final URL url = SourceGenerator.class.getResource(name);
		final File directory = new File(url.getFile());

		if (directory.exists()) {
			System.out.println("  \"True |\"");
			System.out.println("  \"False |\"");
			System.out.println("  \"Null |\"");
			// Get the list of the files contained in the package
			final String[] files = directory.list();
			for (int i = 0; i < files.length; i++) {

				// we are only interested in .class files
				if (files[i].endsWith(".class")) {
					// removes the .class extension
					final String classname = files[i].substring(0, files[i].length() - 6);
					try {
						// Try to create an instance of the object
						final Object o = Class.forName(packageName + "." + classname).newInstance();

						if (classname.length() >= maximumNumberOfChars && !(classname.contains("$"))) {
							System.out.println("  \"" + classname + "\" |");
						}

					} catch (final ClassNotFoundException cnfex) {
						System.err.println(cnfex);
					} catch (final InstantiationException iex) {
						// We try to instantiate an interface
						// or an object that does not have a
						// default constructor
					} catch (final IllegalAccessException iaex) {
						// The class is not public
					}
				}
			}
		}

	}

	public static void generateJavaScript(final String pckgname, boolean isArray) {
		String name = pckgname;
		if (!name.startsWith("/")) {
			name = "/" + name;
		}

		name = name.replace('.', '/');

		// Get a File object for the package
		final URL url = SourceGenerator.class.getResource(name);
		final File directory = new File(url.getFile());

		if (directory.exists()) {
			System.out.println("\"True\",");
			System.out.println("\"False\",");
			System.out.println("\"List\",");
			System.out.println("\"Modulus\",");
			System.out.println("\"Flat\",");
			System.out.println("\"HoldAll\",");
			System.out.println("\"HoldFirst\",");
			System.out.println("\"HoldRest\",");
			System.out.println("\"Listable\",");
			System.out.println("\"NumericFunction\",");
			System.out.println("\"OneIdentity\",");
			System.out.println("\"Orderless\",");
			System.out.println("\"Slot\",");
			System.out.println("\"SlotSequence\",");
			// Get the list of the files contained in the package
			final String[] files = directory.list();
			for (int i = 0; i < files.length; i++) {

				// we are only interested in .class files
				if (files[i].endsWith(".class")) {
					// removes the .class extension
					final String classname = files[i].substring(0, files[i].length() - 6);
					if (!(classname.contains("$"))) {
						try {
							// Try to create an instance of the object
							final Object o = Class.forName(pckgname + "." + classname).newInstance();

							if (o instanceof AbstractSymbolEvaluator) {
								System.out.print("\"" + classname + "\"");
							} else {
								if (isArray) {
									System.out.print("\"" + classname + "[]\"");
								} else {
									System.out.print("\"" + classname + "\"");
								}
							}
							if (i < files.length - 1) {
								System.out.println(",");
							}
						} catch (final ClassNotFoundException cnfex) {
							System.err.println(cnfex);
						} catch (final InstantiationException iex) {
							// We try to instantiate an interface
							// or an object that does not have a
							// default constructor
						} catch (final IllegalAccessException iaex) {
							// The class is not public
						}
					}
				}
			}
		}

	}
	
	public static void generateFunctionStrings(final String pckgname, boolean isArray) {
		String name = pckgname;
		if (!name.startsWith("/")) {
			name = "/" + name;
		}

		name = name.replace('.', '/');

		// Get a File object for the package
		final URL url = SourceGenerator.class.getResource(name);
		final File directory = new File(url.getFile());

		if (directory.exists()) {
			
			System.out.println("\"ArcSinh\",");
			System.out.println("\"DirectedInfinity\",");
			System.out.println("\"False\",");
			System.out.println("\"Flat\",");
			System.out.println("\"HoldAll\",");
			System.out.println("\"HoldFirst\",");
			System.out.println("\"HoldRest\",");
			System.out.println("\"Indeterminate\",");
			System.out.println("\"Integer\",");
			System.out.println("\"List\",");
			System.out.println("\"Listable\",");
			System.out.println("\"Modulus\",");
			System.out.println("\"NumericFunction\",");
			System.out.println("\"OneIdentity\",");
			System.out.println("\"Orderless\",");
			System.out.println("\"Real\",");
			System.out.println("\"Slot\",");
			System.out.println("\"SlotSequence\",");
			System.out.println("\"String\",");
			System.out.println("\"Symbol\",");			
			System.out.println("\"True\",");
			// Get the list of the files contained in the package
			final String[] files = directory.list();
			for (int i = 0; i < files.length; i++) {

				// we are only interested in .class files
				if (files[i].endsWith(".class")) {
					// removes the .class extension
					final String classname = files[i].substring(0, files[i].length() - 6);
					if (!(classname.contains("$"))) {
						try {
							// Try to create an instance of the object
							final Object o = Class.forName(pckgname + "." + classname).newInstance();

							if (o instanceof AbstractSymbolEvaluator) {
								System.out.print("\"" + classname + "\"");
							} else {
								if (isArray) {
									System.out.print("\"" + classname + "[]\"");
								} else {
									System.out.print("\"" + classname + "\"");
								}
							}
							if (i < files.length - 1) {
								System.out.println(",");
							}
						} catch (final ClassNotFoundException cnfex) {
							System.err.println(cnfex);
						} catch (final InstantiationException iex) {
							// We try to instantiate an interface
							// or an object that does not have a
							// default constructor
						} catch (final IllegalAccessException iaex) {
							// The class is not public
						}
					}
				}
			}
		}

	}

	/**
	 * Generate templates for the Eclipse editor
	 * 
	 * @param pckgname
	 */
	public static void generateTemplates(final String pckgname) {
		String name = pckgname;
		if (!name.startsWith("/")) {
			name = "/" + name;
		}

		name = name.replace('.', '/');

		// Get a File object for the package
		final URL url = SourceGenerator.class.getResource(name);
		final File directory = new File(url.getFile());

		if (directory.exists()) {
			System.out
					.print("\n<template id=\"true\" name=\"True\" description=\"True\" context=\"org.matheclipse.templates\" enabled=\"true\">True</template>");
			System.out
					.print("\n<template id=\"false\" name=\"False\" description=\"False\" context=\"org.matheclipse.templates\" enabled=\"true\">False</template>");
			System.out
					.print("\n<template id=\"infinity\" name=\"Infinity\" description=\"Infinity\" context=\"org.matheclipse.templates\" enabled=\"true\">Infinity</template>");

			// Get the list of the files contained in the package
			final String[] files = directory.list();
			for (int i = 0; i < files.length; i++) {

				// we are only interested in .class files
				if (files[i].endsWith(".class")) {
					// removes the .class extension
					final String classname = files[i].substring(0, files[i].length() - 6);
					try {
						// Try to create an instance of the object
						final Object o = Class.forName(pckgname + "." + classname).newInstance();

						if (o instanceof AbstractSymbolEvaluator) {
							// <template id="pi" name="Pi" description="Pi"
							// context="org.matheclipse.templates"
							// enabled="true">Pi</template>
							System.out.print("\n<template id=\"" + classname.toLowerCase() + "\" name=\"" + classname + "\" description=\""
									+ classname + "\" context=\"org.matheclipse.templates\"" + " enabled=\"true\">" + classname + "</template>");
						} else {
							// <template id="if" name="If" description="If[]"
							// context="org.matheclipse.templates"
							// enabled="true">If[${cursor}]</template>
							System.out.print("\n<template id=\"" + classname.toLowerCase() + "\" name=\"" + classname + "\" description=\""
									+ classname + "[]\" context=\"org.matheclipse.templates\"" + " enabled=\"true\">" + classname
									+ "[${cursor}]</template>");
						}
					} catch (final ClassNotFoundException cnfex) {
						System.err.println(cnfex);
					} catch (final InstantiationException iex) {
						// We try to instantiate an interface
						// or an object that does not have a
						// default constructor
					} catch (final IllegalAccessException iaex) {
						// The class is not public
					}
				}
			}
		}

	}

	public static void main(final String[] args) {
		F.initSymbols();
		// generateTemplates("org.matheclipse.core.reflection.system");
		// return;

		// generateCoreRules("org.matheclipse.core.reflection.system");
		// System.out.println("public static void init() {");
		// System.out.println(" ExpressionFactory f =
		// ExpressionFactory.get();");
		//		
		// generateCoreRules("org.matheclipse.core.reflection.system");
		// System.out.println("}");
		//		
		// System.out.println("\n--------------------\n");

		// generate list of symbols
		// generateJFlexKeywords("org.matheclipse.core.reflection.system", 1);

		// generate predefined function Strings:
		System.out.println("private final static String[] FUNCTION_STRINGS = { ");

		generateFunctionStrings("org.matheclipse.core.reflection.system", false);

		System.out.println("};");

		// generate javascript:
//		System.out.println("private final String[] COMPLETIONS = { ");
//
//		generateJavaScript("org.matheclipse.core.reflection.system", false);
//
//		System.out.println("};");
//
//		System.out.println("\n--------------------\n");
//
//		generateDeclareSymbols("org.matheclipse.core.reflection.system");
//
//		System.out.println("\n--------------------\n");
//
//		generateCreateSymbols("org.matheclipse.core.reflection.system");
//		System.out.println("\n--------------------\n");
//		generateFunctions("org.matheclipse.core.reflection.system");

		// System.out.println("\n--------------------\n");
		// generateEvalFunctions("org.matheclipse.core.reflection.system");
	}
}
package org.matheclipse.core.builtin;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.convert.Object2Expr;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.ArgumentTypeException;
import org.matheclipse.core.eval.interfaces.AbstractEvaluator;
import org.matheclipse.core.expression.Context;
import org.matheclipse.core.expression.DataExpr;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.expression.data.JavaClassExpr;
import org.matheclipse.core.expression.data.JavaObjectExpr;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

public class JavaFunctions {
  private static final Logger LOGGER = LogManager.getLogger();

  /**
   * See <a href="https://pangin.pro/posts/computation-in-static-initializer">Beware of computation
   * in static initializer</a>
   */
  private static class Initializer {

    private static void init() {
      if (!Config.FUZZY_PARSER) {
        S.AddToClassPath.setEvaluator(new AddToClassPath());
        S.InstanceOf.setEvaluator(new InstanceOf());
        S.JavaNew.setEvaluator(new JavaNew());
        S.JavaObject.setEvaluator(new JavaObject());
        S.JavaObjectQ.setEvaluator(new JavaObjectQ());
        S.LoadJavaClass.setEvaluator(new LoadJavaClass());
        S.SameObjectQ.setEvaluator(new SameObjectQ());
      }
    }
  }

  private static class AddToClassPath extends AbstractEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      try {
        if (Config.URL_CLASS_LOADER == null) {
          Config.URL_CLASS_LOADER = ClassLoader.getSystemClassLoader();
        }
        ClassLoader child = Config.URL_CLASS_LOADER;
        for (int i = 1; i < ast.size(); i++) {
          IExpr arg = ast.get(i);
          if (arg.isString()) {
            String path = arg.toString();
            File file = new File(path);
            child = new URLClassLoader(new URL[] {file.toURI().toURL()}, child);
          }
        }
        if (child != null) {
          Config.URL_CLASS_LOADER = child;
        }
      } catch (MalformedURLException ex) {
        LOGGER.log(engine.getLogLevel(), ast.topHead(), ex);
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_INFINITY;
    }
  }

  /**
   * <pre>
   * <code>InstanceOf[java-object, &quot;class-name&quot;]
   * </code>
   * </pre>
   *
   * <p>
   * return the result of the Java expression <code>java-object instanceof class</code>.
   *
   * <p>
   * <strong>Note</strong>: the Java specific functions which call Java native classes are only
   * available in the MMA mode in a local installation. All symbol and method names have to be case
   * sensitive.
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * <code>&gt;&gt; loc = JavaNew[&quot;java.util.Locale&quot;, &quot;US&quot;]
   * JavaObject[class java.util.Locale]
   *
   * &gt;&gt; InstanceOf[loc, &quot;java.util.Locale&quot;]
   * True
   *
   * &gt;&gt; InstanceOf[loc, &quot;java.io.Serializable&quot;]
   * True
   * </code>
   * </pre>
   *
   * <h3>Related terms</h3>
   *
   * <p>
   * <a href="JavaClass.md">JavaClass</a>, <a href="JavaNew.md">JavaNew</a>,
   * <a href="JavaObject.md">JavaObject</a>, <a href="JavaObjectQ.md">JavaObjectQ</a>,
   * <a href="LoadJavaClass.md">LoadJavaClass</a>, <a href="SameObjectQ.md">SameObjectQ</a>
   */
  private static class InstanceOf extends AbstractEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr arg1 = ast.arg1();
      IExpr arg2 = ast.arg2();
      if (arg2.isString()) {
        String str = arg2.toString().trim();
        if (str.length() == 0) {
          // `1`
          IOFunctions.printMessage(ast.topHead(), "error",
              F.List(F.stringx("ClassNotFoundException: \"" + str + "\"")), engine);
          return F.False;
        }
        try {
          if (Config.URL_CLASS_LOADER == null) {
            Config.URL_CLASS_LOADER = ClassLoader.getSystemClassLoader();
          }
          arg2 = JavaClassExpr.newInstance(str, Config.URL_CLASS_LOADER);
        } catch (ClassNotFoundException cnfex) {
          // `1`
          IOFunctions.printMessage(ast.topHead(), "error",
              F.List(F.stringx("ClassNotFoundException: \"" + arg2 + "\"")), engine);
          return F.False;
        }
      }
      if (arg1 instanceof JavaObjectExpr && arg2 instanceof JavaClassExpr) {
        return F.booleSymbol(((JavaClassExpr) arg2).toData().isInstance(((JavaObjectExpr) arg1).toData()));
      }
      return F.False;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_2;
    }
  }

  /**
   *
   *
   * <pre>
   * <code>JavaObject[class className]
   * </code>
   * </pre>
   *
   * <p>
   * a <code>JavaObject</code> can be created with the <code>JavaNew</code> function.
   *
   * <p>
   * <strong>Note</strong>: the Java specific functions which call Java native classes are only
   * available in the MMA mode in a local installation. All symbol and method names have to be case
   * sensitive.
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * <code>&gt;&gt; loc = JavaNew[&quot;java.util.Locale&quot;, &quot;US&quot;]
   * JavaObject[class java.util.Locale]
   *
   * &gt;&gt; ds =  JavaNew[&quot;java.text.DecimalFormatSymbols&quot;, loc]
   * JavaObject[class java.text.DecimalFormatSymbols]
   *
   * &gt;&gt; dm = JavaNew[&quot;java.text.DecimalFormat&quot;, &quot;#.00&quot;, ds]
   * JavaObject[class java.text.DecimalFormat]
   * </code>
   * </pre>
   *
   * <p>
   * Calls the <code>format</code> method of the <code>dm</code> Java object.
   *
   * <pre>
   * <code>&gt;&gt; dm@format[0.815] // InputForm
   * &quot;.81&quot;
   * </code>
   * </pre>
   *
   * <h3>Related terms</h3>
   *
   * <p>
   * <a href="InstanceOf.md">InstanceOf</a>, <a href="JavaClass.md">JavaClass</a>,
   * <a href="JavaObject.md">JavaObject</a>, <a href="JavaObjectQ.md">JavaObjectQ</a>,
   * <a href="JavaShow.md">JavaShow</a>, <a href="LoadJavaClass.md">LoadJavaClass</a>,
   * <a href="SameObjectQ.md">SameObjectQ</a>
   */
  private static class JavaNew extends AbstractEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (Config.isFileSystemEnabled(engine)) {
        IExpr arg1 = ast.arg1();
        if (arg1.isString()) {
          try {
            if (Config.URL_CLASS_LOADER == null) {
              Config.URL_CLASS_LOADER = ClassLoader.getSystemClassLoader();
            }
            arg1 = JavaClassExpr.newInstance(arg1.toString(), Config.URL_CLASS_LOADER);
          } catch (ClassNotFoundException cnfex) {
            LOGGER.log(engine.getLogLevel(), ast.topHead(), cnfex);
            return F.NIL;
          }
        }
        if (arg1 instanceof JavaClassExpr) {
          //
          try {
            Constructor<?>[] constructors = ((JavaClassExpr) arg1).toData().getConstructors();
            for (int i = 0; i < constructors.length; i++) {
              Parameter[] parameters = constructors[i].getParameters();
              if (parameters.length == ast.argSize() - 1) {
                Object[] params = determineParameters(ast, parameters, 2);
                if (params != null) {
                  Object obj = constructors[i].newInstance(params);
                  return JavaObjectExpr.newInstance(obj);
                }
              }
            }
          } catch (InstantiationException | IllegalAccessException | IllegalArgumentException
              | InvocationTargetException | SecurityException ex) {
            LOGGER.log(engine.getLogLevel(), ast.topHead(), ex);
            return F.NIL;
          }
        }
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_INFINITY;
    }
  }

  /**
   *
   *
   * <pre>
   * <code>JavaNew[&quot;class-name&quot;]
   * </code>
   * </pre>
   *
   * <p>
   * create a <code>JavaObject</code> from the <code>class-name</code> default constructor.
   *
   * <pre>
   * <code>JavaNew[&quot;class-name&quot;, arg1, arg2,...]
   * </code>
   * </pre>
   *
   * <p>
   * create a <code>JavaObject</code> from the <code>class-name</code> constructor matching the
   * arguments <code>arg1, arg2,...</code>
   *
   * <p>
   * <strong>Note</strong>: the Java specific functions which call Java native classes are only
   * available in the MMA mode in a local installation. All symbol and method names have to be case
   * sensitive.
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * <code>&gt;&gt; loc = JavaNew[&quot;java.util.Locale&quot;, &quot;US&quot;]
   * JavaObject[class java.util.Locale]
   *
   * &gt;&gt; ds =  JavaNew[&quot;java.text.DecimalFormatSymbols&quot;, loc]
   * JavaObject[class java.text.DecimalFormatSymbols]
   *
   * &gt;&gt; dm = JavaNew[&quot;java.text.DecimalFormat&quot;, &quot;#.00&quot;, ds]
   * JavaObject[class java.text.DecimalFormat]
   * </code>
   * </pre>
   *
   * <p>
   * Calls the <code>format</code> method of the <code>dm</code> Java object.
   *
   * <pre>
   * <code>&gt;&gt; dm@format[0.815] // InputForm
   * &quot;.81&quot;
   * </code>
   * </pre>
   *
   * <h3>Related terms</h3>
   *
   * <p>
   * <a href="InstanceOf.md">InstanceOf</a>, <a href="JavaClass.md">JavaClass</a>,
   * <a href="JavaNew.md">JavaNew</a>, <a href="JavaObjectQ.md">JavaObjectQ</a>,
   * <a href="JavaShow.md">JavaShow</a>, <a href="LoadJavaClass.md">LoadJavaClass</a>,
   * <a href="SameObjectQ.md">SameObjectQ</a>
   */
  private static class JavaObject extends AbstractEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (ast.head() == S.JavaObject) {
        return F.NIL;
      }
      if (ast.head() instanceof JavaObjectExpr //
          && ast.argSize() == 1 && ast.arg1().isAST()) {
        try {
          IAST methodExpr = (IAST) ast.arg1();
          if (methodExpr.head().isSymbol()) {
            JavaObjectExpr joe = (JavaObjectExpr) ast.head();
            Object obj = joe.toData();
            Method[] method = obj.getClass().getMethods();
            for (int i = 0; i < method.length; i++) {
              if (methodExpr.head().isString(method[i].getName())) {
                Parameter[] parameters = method[i].getParameters();
                if (parameters.length == methodExpr.argSize()) {
                  Object[] params = determineParameters(methodExpr, parameters, 1);
                  if (params != null) {
                    Object result = method[i].invoke(obj, params);
                    if (result instanceof String) {
                      return F.stringx((String) result);
                    }
                    return Object2Expr.convert(result, false, true);
                  }
                }
              }
            }
          }

        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
          LOGGER.log(engine.getLogLevel(), S.JavaObject, ex);
        }
      }
      return F.NIL;
    }
  }

  /**
   *
   *
   * <pre>
   * <code>JavaObjectQ[java-object]
   * </code>
   * </pre>
   *
   * <p>
   * return <code>True</code> if <code>java-object</code> is a <code>JavaObject</code> expression.
   *
   * <p>
   * <strong>Note</strong>: the Java specific functions which call Java native classes are only
   * available in the MMA mode in a local installation. All symbol and method names have to be case
   * sensitive.
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * <code>&gt;&gt; loc = JavaNew[&quot;java.util.Locale&quot;, &quot;US&quot;]
   * JavaObject[class java.util.Locale]
   *
   * &gt;&gt; JavaObjectQ[loc]
   * True
   * </code>
   * </pre>
   *
   * <h3>Related terms</h3>
   *
   * <p>
   * <a href="InstanceOf.md">InstanceOf</a>, <a href="JavaClass.md">JavaClass</a>,
   * <a href="JavaNew.md">JavaNew</a>, <a href="JavaObject.md">JavaObject</a>,
   * <a href="JavaShow.md">JavaShow</a>, <a href="LoadJavaClass.md">LoadJavaClass</a>,
   * <a href="SameObjectQ.md">SameObjectQ</a>
   */
  private static class JavaObjectQ extends AbstractEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      return F.booleSymbol(ast.arg1() instanceof JavaObjectExpr);
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_1;
    }
  }

  /**
   *
   *
   * <pre>
   * <code>LoadJavaClass[&quot;class-name&quot;]
   * </code>
   * </pre>
   *
   * <p>
   * loads the class with the specified <code>class-name</code> and return a <code>JavaClass
   * </code> expression. All static method names are assigned to a context which will be created by
   * the last part of the class name.
   *
   * <p>
   * <strong>Note</strong>: the Java specific functions which call Java native classes are only
   * available in the MMA mode in a local installation. All symbol and method names have to be case
   * sensitive.
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * <code>&gt;&gt; clazz= LoadJavaClass[&quot;org.jsoup.Jsoup&quot;]
   * JavaClass[org.jsoup.Jsoup]
   *
   * &gt;&gt; conn=Jsoup`connect[&quot;https://jsoup.org/&quot;]
   * JavaObject[class org.jsoup.helper.HttpConnection]
   *
   * &gt;&gt; doc=conn@get[ ];
   * </code>
   * </pre>
   *
   * <p>
   * Print the title of the HTML page.
   *
   * <pre>
   * <code>&gt;&gt; Print[doc@title[ ]]
   * jsoup Java HTML Parser, with the best of HTML5 DOM methods and CSS selectors.
   * </code>
   * </pre>
   *
   * <h3>Related terms</h3>
   *
   * <p>
   * <a href="InstanceOf.md">InstanceOf</a>, <a href="JavaClass.md">JavaClass</a>,
   * <a href="JavaNew.md">JavaNew</a>, <a href="JavaObject.md">JavaObject</a>,
   * <a href="JavaObjectQ.md">JavaObjectQ</a>
   */
  private static class LoadJavaClass extends AbstractEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (Config.isFileSystemEnabled(engine)) {
        IExpr arg1 = ast.arg1();
        if (arg1.isString()) {
          try {
            String className = arg1.toString();
            if (Config.URL_CLASS_LOADER == null) {
              Config.URL_CLASS_LOADER = ClassLoader.getSystemClassLoader();
            }
            JavaClassExpr jClazz = JavaClassExpr.newInstance(className, Config.URL_CLASS_LOADER);
            Class<?> clazz = jClazz.toData();
            int indx = className.lastIndexOf('.');
            if (indx > 0) {
              String contextName = className.substring(indx + 1) + '`';
              try {
                Context context = engine.beginPackage(contextName);
                context.setJavaClass(clazz);
                Method[] methods = clazz.getMethods();
                for (int i = 0; i < methods.length; i++) {
                  Method method = methods[i];
                  if (Modifier.isStatic(method.getModifiers())) {
                    ISymbol methodName = F.symbol(context, method.getName(), engine);
                  }
                }

              } finally {
                engine.endPackage();
              }
            }
            return jClazz;
          } catch (ClassNotFoundException cnfex) {
            LOGGER.log(engine.getLogLevel(), ast.topHead(), cnfex);
            return F.NIL;
          }
        }
        if (arg1 instanceof JavaClassExpr) {
          return arg1;
        }
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_1;
    }
  }

  /**
   *
   *
   * <pre>
   * <code>SameObjectQ[java-object1, java-object2]
   * </code>
   * </pre>
   *
   * <p>
   * gives <code>True</code> if the Java <code>==</code> operator for the Java objects gives true.
   * <code>False</code> otherwise.
   *
   * <p>
   * <strong>Note</strong>: the Java specific functions which call Java native classes are only
   * available in the MMA mode in a local installation. All symbol and method names have to be case
   * sensitive.
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * <code>&gt;&gt; loc1= JavaNew[&quot;java.util.Locale&quot;,&quot;US&quot;]
   * JavaObject[class java.util.Locale]
   *
   * &gt;&gt; loc2= JavaNew[&quot;java.util.Locale&quot;,&quot;US&quot;]
   * JavaObject[class java.util.Locale]
   *
   * &gt;&gt; SameObjectQ[loc1, loc2]
   * False
   *
   * &gt;&gt; SameObjectQ[loc1, loc1]
   * True
   * </code>
   * </pre>
   *
   * <h3>Related terms</h3>
   *
   * <p>
   * <a href="InstanceOf.md">InstanceOf</a>, <a href="JavaClass.md">JavaClass</a>,
   * <a href="JavaNew.md">JavaNew</a>, <a href="JavaObject.md">JavaObject</a>,
   * <a href="JavaObjectQ.md">JavaObjectQ</a>
   */
  private static class SameObjectQ extends AbstractEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr arg1 = ast.arg1();
      IExpr arg2 = ast.arg2();
      if (arg1 instanceof JavaObjectExpr && arg2 instanceof JavaObjectExpr) {
        return F.booleSymbol(((JavaObjectExpr) arg1).toData() == ((JavaObjectExpr) arg2).toData());
      }
      return F.False;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_2;
    }
  }

  public static Object[] determineParameters(final IAST ast, Parameter[] parameters, int offset) {
    try {
      Object[] params = new Object[parameters.length];
      for (int j = 0; j < parameters.length; j++) {
        Parameter p = parameters[j];
        IExpr arg = ast.get(j + offset);
        Class<?> clazz = p.getType();
        if (arg instanceof DataExpr<?>) {
          Object obj = ((DataExpr) arg).toData();
          if (clazz.isInstance(obj)) {
            params[j] = obj;
            continue;
          }
        }

        if (clazz.isInstance(arg)) {
          params[j] = arg;
        } else if (clazz.equals(boolean.class)) {
          if (arg.isTrue()) {
            params[j] = Boolean.TRUE;
          } else if (arg.isFalse()) {
            params[j] = Boolean.FALSE;
          } else {
            return null;
          }
        } else if (clazz.equals(double.class)) {
          params[j] = Double.valueOf(arg.evalDouble());
        } else if (clazz.equals(float.class)) {
          params[j] = Float.valueOf((float) arg.evalDouble());
        } else if (clazz.equals(int.class)) {
          int n = arg.toIntDefault();
          if (n == Integer.MIN_VALUE) {
            return null;
          }
          params[j] = Integer.valueOf(n);
        } else if (clazz.equals(long.class)) {
          long l = arg.toLongDefault();
          if (l == Long.MIN_VALUE) {
            return null;
          }
          params[j] = Long.valueOf(l);
        } else if (clazz.equals(short.class)) {
          int s = arg.toIntDefault();
          if (s < Short.MIN_VALUE || s > Short.MAX_VALUE) {
            return null;
          }
          params[j] = Short.valueOf((short) s);
        } else if (clazz.equals(byte.class)) {
          int b = arg.toIntDefault();
          if (b < Byte.MIN_VALUE || b > Byte.MAX_VALUE) {
            return null;
          }
          params[j] = Byte.valueOf((byte) b);
        } else if (clazz.equals(char.class)) {
          if (!arg.isString()) {
            return null;
          }
          String str = arg.toString();
          if (str.length() != 1) {
            return null;
          }
          params[j] = Character.valueOf(str.charAt(0));
        } else if (clazz.equals(String.class)) {
          if (!arg.isString()) {
            return null;
          }
          params[j] = arg.toString();
        } else if (clazz.equals(org.hipparchus.complex.Complex.class)) {
          org.hipparchus.complex.Complex complex = arg.evalComplex();
          if (complex == null) {
            return null;
          }
          params[j] = complex;
        } else if (clazz.equals(Class.class) && arg instanceof JavaClassExpr) {
          params[j] = ((JavaClassExpr) arg).toData();
        } else {
          params[j] = arg;
        }
      }
      return params;
    } catch (ArgumentTypeException atex) {

    }
    return null;
  }

  public static void initialize() {
    Initializer.init();
  }

  private JavaFunctions() {}
}

package org.matheclipse.core.io;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;

/**
 * Output streams whose behaviour is written in the Wolfram Language.
 *
 * <p>
 * <code>DefineOutputStreamMethod</code> names a set of functions - how a stream of that kind is
 * opened, what happens to the bytes written to it, what a flush does and what a close does - and
 * <code>OpenWrite[Method -&gt; name]</code> opens one. Everything written to the stream is then
 * handed back to those functions, so a package can put the output where it likes: the WLJS notebook
 * uses it to send what a kernel prints to the cell it is printing from, rather than to a console
 * nobody is looking at.
 *
 * <p>
 * The functions carry a <i>state</i> between them. The constructor answers with one, every write
 * and flush is given it and answers with the state to use next, and the close is given the last.
 * What the state is belongs to whoever wrote the method - WLJS uses a fresh symbol as a mutable
 * object - so it is threaded through untouched.
 */
public final class OutputStreamMethods {

  private OutputStreamMethods() {}

  /**
   * The methods defined so far.
   *
   * <p>
   * Process-global, like the paclet registry: a method is a definition of a kind of stream rather
   * than a value of one session, and the streams which use it outlive the expression that opened
   * them.
   */
  private static final Map<String, IAST> METHODS = new ConcurrentHashMap<String, IAST>();

  /** Remember the functions belonging to a method name. */
  public static void define(String name, IAST definitions) {
    METHODS.put(name, definitions);
  }

  /** The functions belonging to a method name, or <code>null</code> if it was never defined. */
  public static IAST definitions(String name) {
    return METHODS.get(name);
  }

  /** Forget every method. Tests share one process, so one must not leave its methods to the next. */
  public static void clear() {
    METHODS.clear();
  }

  /**
   * A stream which hands its bytes to the functions a method named.
   *
   * <p>
   * The bytes are collected until a flush, and a flush is what a <code>Print</code> ends with, so a
   * method sees one line at a time rather than one byte at a time - which is what a method that
   * turns its bytes back into a string can work with.
   */
  public static final class MethodOutputStream extends OutputStream {

    private final String methodName;
    private final IAST definitions;
    private IExpr state;
    private IASTAppendable buffer = F.ListAlloc();
    private boolean closed = false;

    private MethodOutputStream(String methodName, IAST definitions, IExpr state) {
      this.methodName = methodName;
      this.definitions = definitions;
      this.state = state;
    }

    /**
     * Open a stream of the given kind, or <code>null</code> if there is no such method or its
     * constructor refused.
     *
     * @param caller the built-in which is opening it, which the constructor is told
     */
    public static MethodOutputStream open(String methodName, IExpr streamName, boolean append,
        IExpr caller, IAST options, EvalEngine engine) {
      IAST definitions = definitions(methodName);
      if (definitions == null) {
        return null;
      }
      IExpr constructor = function(definitions, "ConstructorFunction");
      IExpr state = S.Null;
      if (constructor.isPresent()) {
        IExpr answer = engine.evaluate(F.ast(new IExpr[] {streamName, F.bool(append), caller,
            options}, constructor));
        // {True, state}: a constructor which says anything else has refused
        if (!answer.isList2() || !answer.first().isTrue()) {
          return null;
        }
        state = answer.second();
      }
      return new MethodOutputStream(methodName, definitions, state);
    }

    @Override
    public void write(int b) throws IOException {
      if (!closed) {
        buffer.append(F.ZZ(b & 0xFF));
      }
    }

    @Override
    public void write(byte[] bytes, int offset, int length) throws IOException {
      if (!closed) {
        for (int i = 0; i < length; i++) {
          buffer.append(F.ZZ(bytes[offset + i] & 0xFF));
        }
      }
    }

    @Override
    public void flush() throws IOException {
      if (closed || buffer.isEmpty()) {
        return;
      }
      IAST written = buffer;
      buffer = F.ListAlloc();
      EvalEngine engine = EvalEngine.get();
      call("WriteFunction", engine, state, written);
      call("FlushFunction", engine, state);
    }

    @Override
    public void close() throws IOException {
      if (closed) {
        return;
      }
      flush();
      closed = true;
      IExpr closer = function(definitions, "CloseFunction");
      if (closer.isPresent()) {
        // the close is handed the state itself rather than a list of arguments
        EvalEngine.get().evaluate(F.unaryAST1(closer, state));
      }
    }

    /** The stream's own name, which is the method it was opened with. */
    public String methodName() {
      return methodName;
    }

    /**
     * Call one of the method's functions and keep the state it answers with.
     *
     * <p>
     * The answer is <code>{value, state}</code>. A function which answers with anything else - or
     * which is not defined at all - leaves the state as it was, so a method needs only the
     * functions it actually uses.
     */
    private void call(String key, EvalEngine engine, IExpr... arguments) {
      IExpr function = function(definitions, key);
      if (function.isNIL()) {
        return;
      }
      IExpr answer = engine.evaluate(F.ast(arguments, function));
      if (answer.isList2()) {
        state = answer.second();
      }
    }
  }

  /** The function a method gives for one of the keys, or {@link F#NIL}. */
  private static IExpr function(IAST definitions, String key) {
    for (int i = 1; i < definitions.size(); i++) {
      IExpr rule = definitions.get(i);
      if (rule.isRuleAST() && rule.first().isString(key)) {
        return rule.second();
      }
    }
    return F.NIL;
  }
}

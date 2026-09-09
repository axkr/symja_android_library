package org.matheclipse.core.builtin;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractCoreFunctionEvaluator;
import org.matheclipse.core.eval.interfaces.AbstractEvaluator;
import org.matheclipse.core.eval.interfaces.AbstractSymbolEvaluator;
import org.matheclipse.core.eval.tasks.EventLoop;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.expression.data.LinkObjectExpr;
import org.matheclipse.core.io.link.LinkEntry;
import org.matheclipse.core.io.link.LinkRegistry;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * Two kernels talking to each other.
 *
 * <p>
 * One kernel launches another - <code>LinkLaunch["… -wstp"]</code> - and then writes expressions
 * to it and reads what comes back. That is how a notebook keeps its evaluations out of the kernel
 * which serves its pages: the one that answers the browser stays responsive while the other one is
 * busy with <code>Integrate</code>.
 *
 * <p>
 * Reading blocks the way the Wolfram Language blocks: the evaluation waits, and while it waits the
 * {@link EventLoop} runs whatever else has come due, so the kernel goes on serving its sockets.
 */
public class LinkFunctions {

  private static class Initializer {

    private static void init() {
      if (!Config.FUZZY_PARSER) {
        S.LinkLaunch.setEvaluator(new LinkLaunch());
        S.LinkCreate.setEvaluator(new LinkCreate());
        S.LinkConnect.setEvaluator(new LinkConnect());
        S.LinkActivate.setEvaluator(new LinkActivate());
        S.LinkWrite.setEvaluator(new LinkWrite());
        S.LinkRead.setEvaluator(new LinkRead());
        S.LinkReadyQ.setEvaluator(new LinkReadyQ());
        S.LinkClose.setEvaluator(new LinkClose());
        S.LinkInterrupt.setEvaluator(new LinkInterrupt());
        S.Links.setEvaluator(new Links());
        S.$ParentLink.setEvaluator(new $ParentLink());
      }
    }
  }

  /** How long one wait inside a blocking link read lasts before the loop looks again. */
  private static final long POLL_MILLIS = 5L;

  /** How long <code>LinkActivate</code> waits for the other end to say hello. */
  private static final long ACTIVATE_MILLIS = 30_000L;

  /** The link to the kernel which started this one, for <code>$ParentLink</code>. */
  private static volatile LinkEntry parentLink = null;

  public static void setParentLink(LinkEntry entry) {
    parentLink = entry;
  }

  public static LinkEntry parentLink() {
    return parentLink;
  }

  /** The link an expression names, or <code>null</code> if it names none. */
  public static LinkEntry entryOf(IExpr expr) {
    if (expr instanceof LinkObjectExpr) {
      return ((LinkObjectExpr) expr).entry();
    }
    return null;
  }

  private static IExpr sandbox(IAST ast, EvalEngine engine) {
    // The operation `1` is not allowed in sandbox mode.
    return Errors.printMessage(ast.topHead(), "sandbox", F.List(ast.topHead()), engine);
  }

  /**
   * Split a command line the way a shell would: on spaces, except inside quotes.
   * <code>"\"/path with spaces/symjascript\" -wstp"</code> is two words.
   */
  static List<String> splitCommand(String command) {
    List<String> words = new ArrayList<String>();
    StringBuilder word = new StringBuilder();
    char quote = 0;
    boolean started = false;
    for (int i = 0; i < command.length(); i++) {
      char c = command.charAt(i);
      if (quote != 0) {
        if (c == quote) {
          quote = 0;
        } else {
          word.append(c);
        }
        continue;
      }
      if (c == '"' || c == '\'') {
        quote = c;
        started = true;
        continue;
      }
      if (Character.isWhitespace(c)) {
        if (started) {
          words.add(word.toString());
          word.setLength(0);
          started = false;
        }
        continue;
      }
      word.append(c);
      started = true;
    }
    if (started) {
      words.add(word.toString());
    }
    return words;
  }

  /**
   * <code>LinkLaunch["command"]</code>: start a kernel and talk to it over its standard input and
   * output.
   *
   * <p>
   * The first word of the command is what this kernel would be relaunched as when it names this
   * kernel's own executable, which is how <code>First[$CommandLine] &lt;&gt; " -wstp"</code> works
   * whether Symja runs from a launcher script, a jar or a native binary.
   */
  private static class LinkLaunch extends AbstractEvaluator {
    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (!Config.isOSAccessEnabled(engine)) {
        return sandbox(ast, engine);
      }
      List<String> command;
      if (ast.arg1().isString()) {
        command = splitCommand(ast.arg1().toString());
      } else if (ast.arg1().isList()) {
        command = new ArrayList<String>();
        IAST list = (IAST) ast.arg1();
        for (int i = 1; i < list.size(); i++) {
          command.add(list.get(i).toString());
        }
      } else {
        return F.NIL;
      }
      if (command.isEmpty()) {
        return S.$Failed;
      }
      List<String> relaunch = Config.RELAUNCH_COMMAND;
      if (relaunch != null && !relaunch.isEmpty() && isThisExecutable(command.get(0))) {
        List<String> expanded = new ArrayList<String>(relaunch);
        expanded.addAll(command.subList(1, command.size()));
        command = expanded;
      }
      LinkEntry entry = LinkRegistry.launch(command);
      return entry == null ? S.$Failed : LinkObjectExpr.newInstance(entry);
    }

    /** Is this word the executable this kernel is running as? */
    private static boolean isThisExecutable(String word) {
      IAST commandLine = Config.COMMAND_LINE;
      if (commandLine == null || commandLine.size() < 2 || !commandLine.arg1().isString()) {
        return false;
      }
      String executable = commandLine.arg1().toString();
      return !executable.isEmpty() && (word.equals(executable) || word.endsWith("/" + executable)
          || executable.endsWith("/" + word));
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_2;
    }
  }

  /**
   * <code>LinkCreate["name"]</code>: listen for a kernel which will connect under this name.
   *
   * <p>
   * The link is answered at once and is usable as soon as the other end arrives; nothing has to
   * wait here, because the kernel which created it goes on to <code>LinkReadyQ</code> it.
   */
  private static class LinkCreate extends AbstractEvaluator {
    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (!Config.isOSAccessEnabled(engine)) {
        return sandbox(ast, engine);
      }
      if (!ast.arg1().isString()) {
        return F.NIL;
      }
      String name = ast.arg1().toString();
      LinkRegistry.PendingLink pending = LinkRegistry.create(name);
      if (pending == null) {
        return S.$Failed;
      }
      // wait for the other end on a thread of its own; the link answers LinkReadyQ meanwhile
      LinkEntry[] accepted = new LinkEntry[1];
      Thread thread = new Thread(() -> accepted[0] = pending.accept((int) ACTIVATE_MILLIS),
          "symja-link-accept-" + name);
      thread.setDaemon(true);
      thread.start();
      return new PendingLinkExpr(name, thread, accepted);
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_2;
    }
  }

  /**
   * A link which has been created but not yet connected to. It answers as a
   * <code>LinkObject</code> would once the other end arrives; until then it is not ready and has
   * nothing to read.
   */
  private static final class PendingLinkExpr extends org.matheclipse.core.expression.DataExpr<String> {
    private static final long serialVersionUID = 1L;
    private final transient Thread thread;
    private final transient LinkEntry[] accepted;

    PendingLinkExpr(String name, Thread thread, LinkEntry[] accepted) {
      super(S.LinkObject, name);
      this.thread = thread;
      this.accepted = accepted;
    }

    /** The link, once somebody has connected to it. */
    LinkEntry entry() {
      return accepted[0];
    }

    /** Wait for the other end. */
    LinkEntry await(long millis) {
      try {
        thread.join(millis);
      } catch (InterruptedException ie) {
        Thread.currentThread().interrupt();
      }
      return accepted[0];
    }

    @Override
    public IExpr copy() {
      return this;
    }

    @Override
    public boolean equals(Object obj) {
      return this == obj;
    }

    @Override
    public int hashCode() {
      return System.identityHashCode(this);
    }

    @Override
    public String toString() {
      return "LinkObject[" + fData + ", listening]";
    }
  }

  /** The link an expression names, waiting for a created link's other end if it has not come. */
  private static LinkEntry linkOf(IExpr expr) {
    if (expr instanceof PendingLinkExpr) {
      return ((PendingLinkExpr) expr).entry();
    }
    return entryOf(expr);
  }

  /**
   * <code>LinkConnect["name"]</code>: connect to a kernel listening under this name.
   */
  private static class LinkConnect extends AbstractEvaluator {
    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (!Config.isOSAccessEnabled(engine)) {
        return sandbox(ast, engine);
      }
      if (!ast.arg1().isString()) {
        return F.NIL;
      }
      LinkEntry entry = LinkRegistry.connect(ast.arg1().toString(), 5000L);
      return entry == null ? S.$Failed : LinkObjectExpr.newInstance(entry);
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_2;
    }
  }

  /**
   * <code>LinkActivate[link]</code>: wait until the kernel on the other end is there.
   *
   * <p>
   * <code>$Failed</code> when it never arrives, which is what a caller checks with
   * <code>FailureQ</code> before using the link.
   */
  private static class LinkActivate extends AbstractEvaluator {
    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr arg1 = ast.arg1();
      if (arg1 instanceof PendingLinkExpr) {
        LinkEntry accepted = ((PendingLinkExpr) arg1).await(ACTIVATE_MILLIS);
        return accepted == null ? S.$Failed : arg1;
      }
      LinkEntry entry = entryOf(arg1);
      if (entry == null) {
        return S.$Failed;
      }
      long deadline = System.currentTimeMillis() + ACTIVATE_MILLIS;
      while (!entry.sawHello() && !entry.isClosed() && System.currentTimeMillis() < deadline) {
        EventLoop.INSTANCE.pump(engine);
        EventLoop.INSTANCE.awaitEvent(POLL_MILLIS);
      }
      return entry.sawHello() ? arg1 : S.$Failed;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_1;
    }
  }

  /**
   * <code>LinkWrite[link, expr]</code>: hand one expression to the other kernel.
   *
   * <p>
   * The expression is evaluated first, as everything is: <code>LinkWrite[link, f[x]]</code> sends
   * what <code>f[x]</code> came to here. A caller which wants the other kernel to do the
   * evaluating writes <code>LinkWrite[link, Unevaluated[…]]</code>, which is how a notebook sends
   * a cell; an up-value of the second argument's head - <code>HeldRemotePacket /: LinkWrite[…]</code>
   * - is consulted before this ever runs.
   */
  private static class LinkWrite extends AbstractEvaluator {
    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      LinkEntry entry = linkOf(ast.arg1());
      if (entry == null) {
        return S.$Failed;
      }
      IExpr expr = ast.arg2();
      if (expr.isUnevaluated()) {
        expr = expr.first();
      }
      try {
        entry.write(expr);
      } catch (IOException ioe) {
        return S.$Failed;
      }
      return S.Null;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_2;
    }

  }

  /**
   * <code>LinkRead[link]</code>: the next packet, waiting for one if none has arrived.
   */
  private static class LinkRead extends AbstractEvaluator {
    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      LinkEntry entry = linkOf(ast.arg1());
      if (entry == null) {
        return S.$Failed;
      }
      while (true) {
        IExpr packet = entry.poll();
        if (packet != null) {
          return packet;
        }
        if (entry.isClosed()) {
          return S.EndOfFile;
        }
        EventLoop.INSTANCE.pump(engine);
        EventLoop.INSTANCE.awaitEvent(POLL_MILLIS);
      }
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_2;
    }
  }

  /** <code>LinkReadyQ[link]</code>: is there a packet to read? */
  private static class LinkReadyQ extends AbstractEvaluator {
    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      LinkEntry entry = linkOf(ast.arg1());
      return F.booleSymbol(entry != null && entry.ready());
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_2;
    }
  }

  /** <code>LinkClose[link]</code>: say goodbye and let the kernel on the other end go. */
  private static class LinkClose extends AbstractEvaluator {
    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      LinkEntry entry = linkOf(ast.arg1());
      if (entry == null) {
        return S.Null;
      }
      LinkRegistry.close(entry.uuid());
      return S.Null;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_1;
    }
  }

  /**
   * <code>LinkInterrupt[link]</code>: stop what the other kernel is doing.
   *
   * <p>
   * This is what a notebook's abort button reaches: the interrupt travels out of band, so it
   * arrives while the other kernel is busy rather than queueing behind the work it is meant to
   * stop.
   */
  private static class LinkInterrupt extends AbstractEvaluator {
    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      LinkEntry entry = linkOf(ast.arg1());
      if (entry == null) {
        return S.$Failed;
      }
      try {
        entry.interrupt();
      } catch (IOException ioe) {
        return S.$Failed;
      }
      return S.Null;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_2;
    }
  }

  /** <code>Links[]</code>: every link this kernel has open. */
  private static class Links extends AbstractEvaluator {
    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IASTAppendable result = F.ListAlloc(8);
      for (LinkEntry entry : LinkRegistry.links()) {
        if (!entry.isClosed()) {
          result.append(LinkObjectExpr.newInstance(entry));
        }
      }
      return result;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_0_0;
    }
  }

  /** <code>$ParentLink</code>: the link to the kernel which started this one, if any. */
  private static class $ParentLink extends AbstractSymbolEvaluator {
    @Override
    public IExpr evaluate(final ISymbol symbol, EvalEngine engine) {
      LinkEntry entry = parentLink;
      return entry == null ? S.Null : LinkObjectExpr.newInstance(entry);
    }
  }

  public static void initialize() {
    Initializer.init();
  }

  private LinkFunctions() {}
}

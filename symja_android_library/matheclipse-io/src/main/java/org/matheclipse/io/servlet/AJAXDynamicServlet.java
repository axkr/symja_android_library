package org.matheclipse.io.servlet;

import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.apache.commons.io.output.StringBuilderWriter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.util.WriterOutputStream;
import org.matheclipse.logging.ThreadLocalNotifyingAppender.ThreadLocalNotifierClosable;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.util.concurrent.MoreExecutors;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * Keeps the live <code>Dynamic</code> cells of a page up to date.
 *
 * <p>
 * <code>POST /ajax/dynamic/</code> with
 *
 * <pre>
 * id       the cell id handed out when the expression was evaluated
 * control  optional, the index of a control in that cell's last rendering
 * value    the value that control produced, as JSON
 * action   optional, the index of a Button in that cell's last rendering
 * poll     optional, the write count the browser last saw
 * refresh  optional, re-render this cell whether or not anything changed
 * dispose  optional, release the cell
 * </pre>
 *
 * <p>
 * The answer is always the same shape: the session's current write count and the cells that have to
 * be redrawn, each keyed by its id.
 *
 * <pre>
 * {"generation": 7, "cells": {"dy3": {"body": {...}, "controls": [...]}}}
 * </pre>
 *
 * That the answer can name cells other than the one that posted is the whole point:
 * <code>Slider[Dynamic[x]]</code> in one cell and <code>Dynamic[x]</code> in another are related
 * only through the symbol they share, and moving the slider has to redraw the other cell.
 */
public class AJAXDynamicServlet extends HttpServlet {

  private static final long serialVersionUID = 3175841512271650911L;

  private static final Logger LOGGER = LogManager.getLogger(AJAXDynamicServlet.class);

  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException {
    res.setContentType("application/json; charset=UTF-8");
    res.setCharacterEncoding("UTF-8");
    res.setHeader("Cache-Control", "no-cache");
    PrintWriter out = res.getWriter();

    HttpSession session = req.getSession();
    String sessionID = session.getId();
    String id = req.getParameter("id");
    if (id == null) {
      out.println(JSONBuilder.createJSONErrorString("No cell id posted!"));
      return;
    }

    if (req.getParameter("dispose") != null) {
      DynamicSession.dispose(sessionID, id);
      out.println("{\"disposed\": true}");
      return;
    }

    EvalEngine engine = AJAXQueryServlet.ENGINES.get(sessionID);
    if (engine == null) {
      out.println(JSONBuilder
          .createJSONErrorString("This interactive output has expired - evaluate the input again."));
      return;
    }

    int controlIndex = intParameter(req, "control");
    int actionIndex = intParameter(req, "action");
    long poll = longParameter(req, "poll");
    boolean refresh = req.getParameter("refresh") != null;

    JsonNode value = null;
    if (controlIndex >= 0) {
      try {
        String posted = req.getParameter("value");
        value = posted == null ? null : JSONBuilder.JSON_OBJECT_MAPPER.readTree(posted);
      } catch (Exception ex) {
        out.println(JSONBuilder
            .createJSONErrorString("Cannot read the control value: " + ex.getMessage()));
        return;
      }
    }

    final StringBuilderWriter outWriter = new StringBuilderWriter();
    WriterOutputStream wouts = new WriterOutputStream(outWriter);
    final StringBuilderWriter errorWriter = new StringBuilderWriter();
    WriterOutputStream werrors = new WriterOutputStream(errorWriter);
    try (PrintStream outs = new PrintStream(wouts);
        PrintStream errors = new PrintStream(werrors);
        ThreadLocalNotifierClosable c = ServletServer.setLogEventNotifier(outs, errors)) {

      engine.setOutPrintStream(outs);
      engine.setErrorPrintStream(errors);

      // see AJAXQueryServlet#sessionLock: one evaluation per session at a time, and never on the
      // engine's own monitor
      synchronized (AJAXQueryServlet.sessionLock(sessionID)) {
        out.println(evaluate(engine, sessionID, id, controlIndex, value, actionIndex, poll, refresh,
            outWriter, errorWriter));
      }
    } finally {
      EvalEngine.remove();
    }
  }

  /**
   * Do the work under a time limit, the way every other evaluation in these servlets is guarded: a
   * dynamic that does not finish must not hold on to the request thread.
   */
  private static String evaluate(EvalEngine engine, String sessionID, String id, int controlIndex,
      JsonNode value, int actionIndex, long poll, boolean refresh, StringBuilderWriter outWriter,
      StringBuilderWriter errorWriter) {
    ExecutorService executor = Executors.newSingleThreadExecutor();
    Future<String> task = executor.submit(() -> {
      try {
        EvalEngine.set(engine);
        ObjectNode answer;
        if (controlIndex >= 0) {
          answer = DynamicSession.applyControl(engine, sessionID, id, controlIndex, value,
              outWriter, errorWriter);
        } else if (actionIndex >= 0) {
          answer = DynamicSession.applyAction(engine, sessionID, id, actionIndex, outWriter,
              errorWriter);
        } else if (refresh) {
          answer = DynamicSession.refresh(engine, sessionID, id, outWriter, errorWriter);
        } else {
          answer = DynamicSession.changedSince(engine, sessionID, poll, outWriter, errorWriter);
        }
        return answer.toString();
      } catch (RuntimeException | IOException ex) {
        LOGGER.debug("Dynamic update failed", ex);
        return JSONBuilder.createJSONErrorString("Error: " + ex.getMessage());
      }
    });
    try {
      return task.get(Config.SERVER_REQUEST_TIMEOUT_SECONDS * 1000L, TimeUnit.MILLISECONDS);
    } catch (InterruptedException | ExecutionException | TimeoutException e) {
      return JSONBuilder.createJSONErrorString("Timeout exceeded. Calculation aborted!");
    } finally {
      if (!task.isDone()) {
        task.cancel(true);
      }
      MoreExecutors.shutdownAndAwaitTermination(executor, 1, TimeUnit.SECONDS);
    }
  }

  private static int intParameter(HttpServletRequest req, String name) {
    String value = req.getParameter(name);
    if (value == null) {
      return -1;
    }
    try {
      return Integer.parseInt(value);
    } catch (NumberFormatException nfe) {
      return -1;
    }
  }

  private static long longParameter(HttpServletRequest req, String name) {
    String value = req.getParameter(name);
    if (value == null) {
      return 0L;
    }
    try {
      return Long.parseLong(value);
    } catch (NumberFormatException nfe) {
      return 0L;
    }
  }
}

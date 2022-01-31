package org.matheclipse.discord;

import java.io.StringWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.basic.ToggleFeature;
import org.matheclipse.core.builtin.IOFunctions;
import org.matheclipse.core.eval.EvalControlledCallable;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.ExprEvaluator;
import org.matheclipse.core.eval.exception.AbortException;
import org.matheclipse.core.eval.exception.FailedException;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.form.Documentation;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IStringX;
import org.matheclipse.gpl.numbertheory.BigIntegerPrimality;
import org.matheclipse.parser.client.ParserConfig;
import org.matheclipse.parser.client.Scanner;
import org.matheclipse.parser.client.SyntaxError;
import org.matheclipse.parser.client.math.MathException;
import discord4j.core.DiscordClientBuilder;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.lifecycle.ReadyEvent;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.User;
import discord4j.core.object.entity.channel.MessageChannel;

public class SymjaBot {
  private static final DateTimeFormatter TIME_FORMATTER =
      DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");

  public static void main(String[] args) {
    if (args.length > 0) {
      Locale.setDefault(Locale.US);
      ParserConfig.PARSER_USE_LOWERCASE_SYMBOLS = true;
      ToggleFeature.COMPILE = false;
      Config.JAVA_UNSAFE = true;
      Config.SHORTEN_STRING_LENGTH = 512;
      Config.USE_VISJS = true;
      Config.FILESYSTEM_ENABLED = false;
      Config.FUZZY_PARSER = true;
      Config.UNPROTECT_ALLOWED = false;
      Config.USE_MANIPULATE_JS = true;
      Config.JAS_NO_THREADS = true;
      Config.MATHML_TRIG_LOWERCASE = false;
      Config.MAX_AST_SIZE = 10000;
      Config.MAX_OUTPUT_SIZE = 10000;
      Config.MAX_BIT_LENGTH = 200000;
      Config.MAX_POLYNOMIAL_DEGREE = 100;
      Config.MAX_INPUT_LEAVES = 100L;
      Config.MAX_MATRIX_DIMENSION_SIZE = 100;
      Config.PRIME_FACTORS = new BigIntegerPrimality();
      EvalEngine.get().setPackageMode(true);

      String theDiscordToken = args[0];
      GatewayDiscordClient client =
          DiscordClientBuilder.create(theDiscordToken).build().login().block();

      client.getEventDispatcher().on(ReadyEvent.class).subscribe(event -> {
        LocalDateTime now = LocalDateTime.now();
        final User self = event.getSelf();
        System.out.println(String.format(TIME_FORMATTER.format(now) + ": Logged in as %s#%s",
            self.getUsername(), self.getDiscriminator()));
      });
      client.on(MessageCreateEvent.class).subscribe(event -> {
        final Message message = event.getMessage();
        if (filterMessage(message)) {
          createMessage(message);
        }
      });
      client.onDisconnect().block();
    } else {
      System.out.println("The discord bot token has to be set as the first argument.");
    }
  }

  private static void createMessage(final Message message) {
    String content = message.getContent().trim();
    if (content.length() > 3) {
      String start = content.substring(0, 3);
      content = content.substring(3);
      String postfix = Scanner.balanceCode(content);
      if (postfix != null && postfix.length() > 0) {
        // there are open brackets which needs to be balanced
        content = content + postfix;
      }
      // if (start.equals("!@@")) {
      // IExpr result = parseExpr(content);
      // double[] v = result.toDoubleVector();
      // if (v != null) {
      // String str = ASCIIGraph.fromSeries(v).plot();
      // final MessageChannel channel = message.getChannel().block();
      // channel.createMessage("```\n" + str + "\n```").block();
      // }
      // } else
      if (start.equals("!~~")) {
        String result = interpreter("N(" + content + ")");
        final MessageChannel channel = message.getChannel().block();
        channel.createMessage(result).block();
      } else {
        String result = interpreter(content);
        final MessageChannel channel = message.getChannel().block();
        channel.createMessage(result).block();
      }
    }
  }

  private static boolean filterMessage(Message message) {
    String content = message.getContent().trim();
    if (content.length() > 3) {
      String start = content.substring(0, 3);
      if (start.equals("!>>") || start.equals("!~~") || start.equals("!@@")) {
        return true;
      }
    }
    return false;
  }

  private static String interpreter(final String trimmedInput) {
    ExprEvaluator evaluator = new ExprEvaluator(false, (short) 100);
    IExpr result;
    final StringWriter buf = new StringWriter();
    try {
      if (trimmedInput.length() > 1 && trimmedInput.charAt(0) == '?') {
        StringBuilder docBuf = new StringBuilder();
        Documentation.getMarkdown(docBuf, trimmedInput.substring(1));
        String docString = docBuf.toString();
        if (docString.length() > 0) {
          int indx = docString.indexOf("### Github");
          if (indx > 0) {
            docString = docString.substring(0, indx);
          }
          return docString;
        }
        Documentation.usageDocumentation(docBuf, trimmedInput.substring(1));
        docString = docBuf.toString();
        if (docString.length() > 0) {
          int indx = docString.indexOf("### Github");
          if (indx > 0) {
            docString = docString.substring(0, indx);
          }
          return docString;
        }
        return "No help page found";
      }
      System.out.println(trimmedInput);

      result = evaluator.evaluateWithTimeout(trimmedInput, 30, TimeUnit.SECONDS, true,
          new EvalControlledCallable(evaluator.getEvalEngine()));

      if (result != null) {
        return printResultShortened(trimmedInput, result);
      }
    } catch (final AbortException re) {
      // try {
      return printResultShortened(trimmedInput, S.$Aborted);
      // } catch (IOException e) {
      // Validate.printException(buf, e);
      // stderr.println(buf.toString());
      // stderr.flush();
      // return "";
      // }
    } catch (final FailedException re) {
      // try {
      return printResultShortened(trimmedInput, S.$Failed);
      // } catch (IOException e) {
      // Validate.printException(buf, e);
      // stderr.println(buf.toString());
      // stderr.flush();
      // return "";
      // }
    } catch (final SyntaxError se) {
      String msg = se.getMessage();
      // stderr.println(msg);
      // stderr.flush();
      return msg;
    } catch (final RuntimeException re) {
      Throwable me = re.getCause();
      if (me instanceof MathException) {
        Validate.printException(buf, me);
      } else {
        Validate.printException(buf, re);
      }
      // stderr.println(buf.toString());
      // stderr.flush();
      return "";
    } catch (final Exception e) {
      Validate.printException(buf, e);
      // stderr.println(buf.toString());
      // stderr.flush();
      return "";
    } catch (final OutOfMemoryError e) {
      Validate.printException(buf, e);
      // stderr.println(buf.toString());
      // stderr.flush();
      return "";
    } catch (final StackOverflowError e) {
      Validate.printException(buf, e);
      // stderr.println(buf.toString());
      // stderr.flush();
      return "";
    }
    return buf.toString();
  }

  private static String printResultShortened(String strInput, IExpr result) {
    StringBuilder buf = new StringBuilder("\nInput:\n```mma\n" + strInput + "\n```");

    String output;
    if (result.isASTSizeGE(S.JSFormData, 2)) {
      output = IOFunctions.shorten(result.first());
      buf.append("\n```javascript\n" + output + "\n```\n");
      return buf.toString();
    }
    if (result.isPresent()) {
      output = IOFunctions.shorten(result);
    } else {
      output = "Null";
    }
    if (result.isString()) {
      IStringX str = (IStringX) result;
      int mimeType = str.getMimeType();
      switch (mimeType) {
        case IStringX.APPLICATION_JAVA:
          buf.append("\nOutput:\n```java\n" + output + "\n```\n");
          return buf.toString();
        case IStringX.APPLICATION_JAVASCRIPT:
          buf.append("\nOutput:\n```javascript\n" + output + "\n```\n");
          return buf.toString();
        case IStringX.APPLICATION_SYMJA:
          buf.append("\nOutput:\n```\n" + output + "\n```\n");
          return buf.toString();
        case IStringX.TEXT_LATEX:
          buf.append("\nOutput:\n```LaTeX\n" + output + "\n```\n");
          return buf.toString();
        case IStringX.TEXT_MATHML:
          buf.append("\nOutput:\n```xml\n" + output + "\n```\n");
          return buf.toString();
      }
      buf.append(output);
      return buf.toString();
    }
    buf.append("\nOutput:\n```mma\n" + output + "\n```\n");
    return buf.toString();
  }

}

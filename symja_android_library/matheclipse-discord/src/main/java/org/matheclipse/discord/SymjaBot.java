package org.matheclipse.discord;

import java.io.StringWriter;
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
import org.matheclipse.core.expression.S;
import org.matheclipse.core.form.Documentation;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IStringX;
import org.matheclipse.gpl.numbertheory.BigIntegerPrimality;
import org.matheclipse.parser.client.FEConfig;
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

  public static void main(String[] args) {
    if (args.length > 0) {
      Locale.setDefault(Locale.US);
      FEConfig.PARSER_USE_LOWERCASE_SYMBOLS = true;
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

      client
          .getEventDispatcher()
          .on(ReadyEvent.class)
          .subscribe(
              event -> {
                final User self = event.getSelf();
                System.out.println(
                    String.format(
                        "Logged in as %s#%s", self.getUsername(), self.getDiscriminator()));
              });
      client
          .on(MessageCreateEvent.class)
          .subscribe(
              event -> {
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
        //        stderr.println("Automatically closing brackets: " + postfix);
        content = content + postfix;
      }
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
      if (start.equals("!>>") || start.equals("!~~")) {
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
        return "No help page found";
      }
      //      System.out.println(trimmedInput);

      result =
          evaluator.evaluateWithTimeout(
              trimmedInput,
              30,
              TimeUnit.SECONDS,
              true,
              new EvalControlledCallable(evaluator.getEvalEngine()));

      if (result != null) {
        return printResultShortened(result);
      }
    } catch (final AbortException re) {
      //      try {
      return printResultShortened(S.$Aborted);
      //      } catch (IOException e) {
      //      Validate.printException(buf, e);
      //        stderr.println(buf.toString());
      //        stderr.flush();
      //        return "";
      //      }
    } catch (final FailedException re) {
      //      try {
      return printResultShortened(S.$Failed);
      //      } catch (IOException e) {
      //        Validate.printException(buf, e);
      //        stderr.println(buf.toString());
      //        stderr.flush();
      //        return "";
      //      }
    } catch (final SyntaxError se) {
      String msg = se.getMessage();
      //      stderr.println(msg);
      //      stderr.flush();
      return msg;
    } catch (final RuntimeException re) {
      Throwable me = re.getCause();
      if (me instanceof MathException) {
        Validate.printException(buf, me);
      } else {
        Validate.printException(buf, re);
      }
      //      stderr.println(buf.toString());
      //      stderr.flush();
      return "";
    } catch (final Exception e) {
      Validate.printException(buf, e);
      //      stderr.println(buf.toString());
      //      stderr.flush();
      return "";
    } catch (final OutOfMemoryError e) {
      Validate.printException(buf, e);
      //      stderr.println(buf.toString());
      //      stderr.flush();
      return "";
    } catch (final StackOverflowError e) {
      Validate.printException(buf, e);
      //      stderr.println(buf.toString());
      //      stderr.flush();
      return "";
    }
    return buf.toString();
  }

  private static String printResultShortened(IExpr result) {
    String output;
    if (result.isASTSizeGE(S.JSFormData, 2)) {
      output = IOFunctions.shorten(result.first());
      return "\n```javascript\n" + output + "\n```\n";
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
          return "\n```java\n" + output + "\n```\n";
        case IStringX.APPLICATION_JAVASCRIPT:
          return "\n```javascript\n" + output + "\n```\n";
        case IStringX.APPLICATION_SYMJA:
          return "\n```\n" + output + "\n```\n";
        case IStringX.TEXT_LATEX:
          return "\n```LaTeX\n" + output + "\n```\n";
        case IStringX.TEXT_MATHML:
          return "\n```xml\n" + output + "\n```\n";
      }
      return output;
    }
    return "\n```mma\n" + output + "\n```\n";
  }
}

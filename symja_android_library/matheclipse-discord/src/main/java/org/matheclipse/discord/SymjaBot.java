package org.matheclipse.discord;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import javax.imageio.ImageIO;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.basic.ToggleFeature;
import org.matheclipse.core.builtin.ConstantDefinitions;
import org.matheclipse.core.eval.Errors;
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
import org.matheclipse.image.bridge.fig.Histogram;
import org.matheclipse.image.bridge.fig.ListPlot;
import org.matheclipse.image.bridge.fig.Plot;
import org.matheclipse.image.expression.data.ImageExpr;
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
import discord4j.core.object.entity.channel.GuildMessageChannel;
import discord4j.core.object.entity.channel.MessageChannel;
import discord4j.core.spec.EmbedCreateSpec;
import discord4j.core.spec.MessageCreateSpec;
import reactor.core.publisher.Mono;

public class SymjaBot {
  private static final DateTimeFormatter TIME_FORMATTER =
      DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");

  /**
   * Time out in seconds, if no result could be created by the Symja interpreter.
   */
  private static int TIMEOUT_SECONDS = 30;

  public static void main(String[] args) {
    if (args.length > 0) {
      Locale.setDefault(Locale.US);
      ParserConfig.PARSER_USE_LOWERCASE_SYMBOLS = true;
      ToggleFeature.COMPILE = false;
      ToggleFeature.COMPILE_PRINT = true;
      Config.USE_MANIPULATE_JS = false;
      Config.JAVA_UNSAFE = true;
      Config.SHORTEN_STRING_LENGTH = 512;
      Config.USE_VISJS = true;
      Config.FILESYSTEM_ENABLED = false;
      Config.FUZZY_PARSER = true;
      Config.UNPROTECT_ALLOWED = false;
      Config.USE_MANIPULATE_JS = true;
      Config.JAS_NO_THREADS = true;
      Config.MATHML_TRIG_LOWERCASE = false;
      Config.MAX_AST_SIZE = 20000;
      Config.MAX_OUTPUT_SIZE = 10000;
      Config.MAX_BIT_LENGTH = 200000;
      Config.MAX_POLYNOMIAL_DEGREE = 100;
      Config.MAX_INPUT_LEAVES = 100L;
      Config.MAX_MATRIX_DIMENSION_SIZE = 100;
      Config.PRIME_FACTORS = new BigIntegerPrimality();
      EvalEngine.get().setPackageMode(true);
      F.initSymbols();
      initFunctions();
      System.out.println("Symja Version: " + Config.VERSION + " initialized");
      String theDiscordToken = args[0];
      GatewayDiscordClient client =
          DiscordClientBuilder.create(theDiscordToken).build().login().block();
      if (client != null) {
        client.getEventDispatcher().on(ReadyEvent.class).subscribe(event -> {
          LocalDateTime now = LocalDateTime.now();
          final User self = event.getSelf();
          System.out.println(String.format(TIME_FORMATTER.format(now) + ": Logged in as %s#%s",
              self.getUsername(), self.getDiscriminator()));
        });
        client.on(MessageCreateEvent.class).subscribe(event -> {
          final Message message = event.getMessage();
          Optional<User> author = message.getAuthor();
          if (author.isPresent()) {
            User user = author.get();
            if (!user.isBot()) {
              if (filterMessage(message)) {
                createMessage(message);
              } else if (filterInlineMessage(message)) {
                createInlineMessages(message);
              }
            }
          } ;
        });
        client.onDisconnect().block();
      } else {
        System.out.println("DiscordClientBuilder...block() returns null.");
      }
    } else {
      System.out.println("The discord bot token has to be set as the first argument.");
    }
  }

  public static void initFunctions() {
    // S.ArrayPlot.setEvaluator(new ArrayPlot());
    S.ListPlot.setEvaluator(new ListPlot());
    // S.ListLogPlot.setEvaluator(new ListLogPlot());
    // S.ListLogLogPlot.setEvaluator(new ListLogLogPlot());
    S.Histogram.setEvaluator(new Histogram());
    S.Plot.setEvaluator(new Plot());
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
      Mono<MessageChannel> mChannel = message.getChannel();
      if (start.equals("!~~")) {
        Object result = interpreter("N(" + content + ")", TIMEOUT_SECONDS);
        if (result instanceof ImageExpr) {
          sendBufferedImage((ImageExpr) result, mChannel);
          return;
        }
        final MessageChannel channel = mChannel.block();
        if (channel != null) {
          channel.createMessage("\n" + result.toString().trim()).block();
        }
      } else if (start.equals("!>>")) {
        Object result = interpreter(content, TIMEOUT_SECONDS);
        if (result instanceof ImageExpr) {
          sendBufferedImage((ImageExpr) result, mChannel);
          return;
        }
        final MessageChannel channel = mChannel.block();
        if (channel != null) {
          channel.createMessage("\n" + result.toString().trim()).block();
        }
      }
    }
  }

  private static void createInlineMessages(final Message message) {
    String content = message.getContent().trim();
    String result = inlineInterpreter(content);
    Mono<MessageChannel> mChannel = message.getChannel();
    final MessageChannel channel = mChannel.block();
    if (channel != null) {
      channel.createMessage(result.toString()).block();
    }
  }

  static String inlineInterpreter(String content) {
    StringBuilder buf = new StringBuilder(content.length() + 100);
    int lastIndex = 0;
    int beginIndex = 0;
    int offset = 7;
    while (true) {
      offset = 7;
      beginIndex = content.indexOf("\n```mma", beginIndex);
      if (beginIndex < 0) {
        offset = 9;
        beginIndex = content.indexOf("\n```symja");
      }
      if (beginIndex > 0) {
        int endIndex = content.indexOf("\n```", beginIndex + offset);
        if (endIndex > beginIndex) {
          buf.append(content.substring(lastIndex, beginIndex));
          lastIndex = endIndex + 4;

          String subSource = content.substring(beginIndex + offset + 1, endIndex);
          String postfix = Scanner.balanceCode(subSource);
          if (postfix != null && postfix.length() > 0) {
            // there are open brackets which needs to be balanced
            subSource = subSource + postfix;
          }
          Object result = interpreter(subSource, 30);
          buf.append("\n" + result.toString().trim() + "\n");

          System.out.println(subSource);
          beginIndex++;
          continue;
        }
      }
      buf.append(content.substring(lastIndex));
      break;
    }
    return buf.toString();
  }

  private static void sendBufferedImage(ImageExpr imageExpr, Mono<MessageChannel> mChannel) {
    BufferedImage bufferedImage = imageExpr.getBufferedImage();
    try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
      ImageIO.write(bufferedImage, "png", outputStream);
      InputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());
      EmbedCreateSpec embed = EmbedCreateSpec.builder().image("attachment://file-name.png").build();
      mChannel //
          .ofType(GuildMessageChannel.class)
          .flatMap(ch -> ch.createMessage(MessageCreateSpec.builder() //
              .addFile("file-name.png", inputStream).addEmbed(embed).build()))
          .block();
    } catch (IOException e) {
      e.printStackTrace();
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

  private static boolean filterInlineMessage(Message message) {
    String content = message.getContent().trim();
    int beginIndex = content.indexOf("\n```mma");
    if (beginIndex < 0) {
      beginIndex = content.indexOf("\n```symja");
    }
    if (beginIndex > 0) {
      int endIndex = content.indexOf("\n```", beginIndex + 6);
      if (endIndex > beginIndex) {
        String subStr = content.substring(beginIndex + 7, endIndex);
        return true;
      }
    }
    return false;
  }

  public static Object interpreter(final String trimmedInput, int seconds) {
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

      result = evaluator.evaluateWithTimeout(trimmedInput, seconds, TimeUnit.SECONDS, true,
          new EvalControlledCallable(evaluator.getEvalEngine()));
      if (result instanceof ImageExpr) {
        return result;
      }
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
      return "\n```mma\n" + msg + "\n```\n";
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
      output = Errors.shorten(result.first());
      buf.append("\n```javascript\n" + output + "\n```\n");
      return buf.toString();
    }
    if (result.isPresent()) {
      output = Errors.shorten(result);
    } else {
      output = "Null";
    }
    if (result.isString()) {
      IStringX str = (IStringX) result;
      int mimeType = str.getMimeType();

      String strTemp = str.toString();
      if (strTemp.length() < 8196) {
        output = strTemp;
      }
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
        case IStringX.TEXT_JSON:
          buf.append("\nOutput:\n```json\n" + output + "\n```\n");
          return buf.toString();
      }
      buf.append(output);
      return buf.toString();
    }
    buf.append("\nOutput:\n```mma\n" + output + "\n```\n");
    return buf.toString();
  }

}

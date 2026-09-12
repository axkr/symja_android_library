package org.matheclipse.io.servlet;

import java.io.File;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.eval.exception.ReturnException;
import jakarta.servlet.Servlet;

public class ServletServer {
  private static final Logger LOGGER = LogManager.getLogger(ServletServer.class);

  /** If <code>true</code>, use localhost string */
  public static boolean LOCALHOST_STRING = false;

  /** The port for running the Symja notebook interface. Default is 8080 */
  public static int PORT = 8080;

  /**
   * The <code>*.ipynb</code> notebook file which is opened in the browser page after startup, or
   * <code>null</code> if the page should start with an empty input. Set with the
   * <code>-notebook</code> command line argument. The notebook is only shown, never evaluated - see
   * {@link AJAXNotebookServlet}.
   */
  public static String NOTEBOOK_FILE = null;

  public static final String MYAPP = "/";

  public static void main(final String[] args) {
    try {
      if (setArgs("ServletServer", args) < 0) {
        return;
      }
    } catch (RuntimeException rex) {
      return;
    }
    Class<ServletServer> serverClass = ServletServer.class;
    String deploymentName = "symja.war";
    Class<AJAXQueryServlet> ajaxServlet = AJAXQueryServlet.class;
    ClassLoader classLoader = serverClass.getClassLoader();

    ServletServer.runServer(deploymentName, classLoader, ajaxServlet, PORT, "index.html");
  }

  /**
   * Start the embedded notebook server. The Undertow deployment itself is built by
   * {@link UndertowLauncher}, so that this class - whose fields {@link AJAXNotebookServlet} reads -
   * links in a deployment that does not ship Undertow.
   *
   * @param deploymentName the <code>*.war</code> deployment name.
   * @param classLoader the current class loader
   * @param ajaxServlet the name of the AJAX servlet class
   * @param port typical port <code>8080</code>
   * @param welcomeFile the page served for <code>/</code>
   */
  protected static void runServer(String deploymentName, ClassLoader classLoader,
      Class<? extends Servlet> ajaxServlet, int port, String welcomeFile) {
    UndertowLauncher.runServer(deploymentName, classLoader, ajaxServlet, port, welcomeFile);
  }

  protected static int setArgs(final String serverClass, final String args[]) {
    for (int i = 0; i < args.length; i++) {
      final String arg = args[i];

      if (arg.equals("-localhost") || arg.equals("-l")) {
        LOCALHOST_STRING = true;
      } else if (arg.equals("-port") || arg.equals("-p")) {
        if (i + 1 >= args.length) {
          LOGGER.error("You must specify a port number when using the -port argument");
          throw ReturnException.RETURN_FALSE;
        }

        String portStr = args[i + 1];
        i++;
        PORT = Integer.parseInt(portStr);
      } else if (arg.equals("-notebook") || arg.equals("-n")) {
        if (i + 1 >= args.length) {
          LOGGER.error("You must specify a file name when using the -notebook argument");
          throw ReturnException.RETURN_FALSE;
        }

        String fileName = args[i + 1];
        i++;
        File file = new File(fileName);
        if (!file.isFile() || !file.canRead()) {
          LOGGER.error("Cannot read the notebook file: {}", fileName);
          throw ReturnException.RETURN_FALSE;
        }
        NOTEBOOK_FILE = file.getAbsolutePath();
      } else if (arg.equals("-help") || arg.equals("-h")) {
        printUsage(serverClass);
        return -1;

      } else if (arg.charAt(0) == '-') {
        // we don't have any more args to recognize!
        final String msg = "Unknown arg: " + arg;
        LOGGER.warn(msg);
        printUsage(serverClass);
        return -4;
      }
    }
    printUsage(serverClass);
    return 1;
  }

  /** Prints the usage of how to use this class to stdout */
  private static void printUsage(final String serverClass) {
    final String lineSeparator = System.getProperty("line.separator");
    final StringBuilder msg = new StringBuilder();
    msg.append(Config.SYMJA);
    msg.append(Config.COPYRIGHT);
    msg.append("Symja Browser Wiki: "
        + "https://github.com/axkr/symja_android_library/wiki/Browser-apps" + lineSeparator);
    msg.append(lineSeparator);
    msg.append("org.matheclipse.io.servlet." + serverClass + " [options]" + lineSeparator);
    msg.append(lineSeparator);
    msg.append("Program arguments: " + lineSeparator);
    msg.append("  -h or -help print usage messages" + lineSeparator);
    msg.append("  -l or -localhost set the name to \"localhost\"" + lineSeparator);
    msg.append("         in the browser; the default is the IP address" + lineSeparator);
    msg.append("  -p or -port set the port (default port is 8080)" + lineSeparator);
    msg.append("  -n or -notebook open the given *.ipynb notebook in the browser" + lineSeparator);
    msg.append("         page; its cells are shown but not evaluated" + lineSeparator);
    msg.append("****+****+****+****+****+****+****+****+****+****+****+****+");

    System.out.println(msg.toString());
    System.out.flush();
  }
}

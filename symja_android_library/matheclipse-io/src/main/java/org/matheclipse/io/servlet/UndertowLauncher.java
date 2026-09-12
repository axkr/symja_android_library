package org.matheclipse.io.servlet;

import static io.undertow.Handlers.resource;
import static io.undertow.servlet.Servlets.defaultContainer;
import static io.undertow.servlet.Servlets.deployment;
import static io.undertow.servlet.Servlets.listener;
import static io.undertow.servlet.Servlets.servlet;
import java.awt.Desktop;
import java.net.InetAddress;
import java.net.URI;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import io.undertow.Handlers;
import io.undertow.Undertow;
import io.undertow.server.HttpHandler;
import io.undertow.server.handlers.PathHandler;
import io.undertow.server.handlers.resource.ClassPathResourceManager;
import io.undertow.servlet.api.DeploymentInfo;
import io.undertow.servlet.api.DeploymentManager;
import jakarta.servlet.MultipartConfigElement;
import jakarta.servlet.Servlet;

/**
 * Builds and starts the embedded Undertow deployment of the standalone notebook server.
 *
 * <p>
 * Every reference to <code>io.undertow</code> in this package lives here, and nothing an evaluation
 * touches reaches this class. That is the point of the split: the servlets are also deployed inside
 * containers that bring their own - a war on App Engine, for one - and those deployments exclude
 * Undertow. A JVM verifies a class as a whole, so leaving this code in {@link ServletServer} made
 * <code>ServletServer.NOTEBOOK_FILE</code>, read by {@link AJAXNotebookServlet}, enough to fail
 * linking when Undertow is not on the classpath.
 */
final class UndertowLauncher {

  private static final Logger LOGGER = LogManager.getLogger(UndertowLauncher.class);

  private UndertowLauncher() {}

  /**
   * @param deploymentName the <code>*.war</code> deployment name.
   * @param classLoader the current class loader
   * @param ajaxServlet the name of the AJAX servlet class
   * @param port typical port <code>8080</code>
   * @param welcomeFile the page served for <code>/</code>
   */
  static void runServer(String deploymentName, ClassLoader classLoader,
      Class<? extends Servlet> ajaxServlet, int port, String welcomeFile) {
    try {
      // https://stackoverflow.com/a/41652378/24819
      String host = ServletServer.LOCALHOST_STRING ? "localhost"
          : InetAddress.getLocalHost().getHostAddress();
      DeploymentInfo servletBuilder = deployment().setClassLoader(classLoader)
          .setContextPath(ServletServer.MYAPP).setDeploymentName(deploymentName)
          .addServlets(servlet("query", ajaxServlet).setLoadOnStartup(1).addMapping("/query/"),
              servlet("doc", AJAXDocServlet.class).addMapping("/doc/*"),
              servlet("search", AJAXSearchServlet.class).addMapping("/doc/search/"),
              servlet("notebook", AJAXNotebookServlet.class).addMapping("/notebook/"),
              servlet("manipulate", AJAXManipulateServlet.class).addMapping("/manipulate/"),
              servlet("dynamic", AJAXDynamicServlet.class).addMapping("/dynamic/"),
              servlet("tracedialog", AJAXTraceDialogServlet.class).addMapping("/tracedialog/"),
              // a browser session cannot hand the kernel a path into its own file system, so a
              // file is carried across instead: upload writes into the session's sandbox
              // directory, download reads back out of it
              servlet("upload", AJAXUploadServlet.class).addMapping("/upload/")
                  // the deployment is built programmatically, so the @MultipartConfig annotation
                  // on the servlet is never scanned and the limits have to be set here
                  .setMultipartConfig(new MultipartConfigElement("", SessionSandbox.MAX_FILE_BYTES,
                      SessionSandbox.MAX_FILE_BYTES + 1024L * 1024L, 1024 * 1024)),
              servlet("download", AJAXDownloadServlet.class).addMapping("/download/"))
          // frees the engine, the evaluation lock and the live widgets of an ended session
          .addListener(listener(SymjaSessionListener.class));

      DeploymentManager manager = defaultContainer().addDeployment(servletBuilder);
      manager.deploy();

      HttpHandler servletHandler = manager.start();

      PathHandler path = Handlers.path() // Handlers.redirect(MYAPP)
          .addPrefixPath("/ajax", servletHandler)
          .addPrefixPath("/", resource(new ClassPathResourceManager(classLoader, "public/"))
              .addWelcomeFiles(welcomeFile));

      Undertow server = Undertow.builder().addHttpListener(port, host).setHandler(path).build();
      server.start();

      URI uri = new URI("http://" + host + ":" + port + "/" + welcomeFile);
      // this print line is intentionally and should display the uri to the user
      System.out.println("Open browser URL: " + uri);
      LOGGER.info("Open browser URL: {}", uri);

      if (Desktop.isDesktopSupported()) {
        Desktop.getDesktop().browse(uri);
      }

    } catch (Exception ex) {
      ex.printStackTrace();
    }
  }
}

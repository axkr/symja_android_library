package org.matheclipse.io.servlet;

public class MMAServletServer {

  public static final String MYAPP = "/";

  public static void main(final String[] args) {
    try {
      if (ServletServer.setArgs("MMAServletServer", args) < 0) {
        return;
      }
    } catch (RuntimeException rex) {
      return;
    }
    Class<MMAServletServer> serverClass = MMAServletServer.class;
    String deploymentName = "mmasymja.war";
    Class<MMAAJAXQueryServlet> ajaxServlet = MMAAJAXQueryServlet.class;
    ClassLoader classLoader = serverClass.getClassLoader();
    ServletServer.runServer(
        deploymentName, classLoader, ajaxServlet, ServletServer.PORT, "indexmma.html");
  }
}

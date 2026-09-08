package org.matheclipse.core.system;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.io.net.SocketRegistry;

/**
 * TCP sockets over the loopback interface.
 *
 * <p>
 * Every test opens its server on port <code>0</code> and asks the socket which port it actually
 * got, so two runs at once cannot collide.
 */
public class SocketFunctionsTest extends ExprEvaluatorTestCase {

  @AfterEach
  public void closeSockets() {
    SocketRegistry.closeAll();
  }

  @Test
  public void testWriteAndReadOverLoopback() {
    boolean osAccess = Config.OS_ACCESS_ENABLED;
    Config.OS_ACCESS_ENABLED = true;
    try {
      check("server = SocketOpen(\"127.0.0.1\", 0); Head(server)", //
          "SocketObject");
      check("port = server[\"DestinationPort\"]; port > 0", //
          "True");
      check("client = SocketConnect(\"127.0.0.1\", port); Head(client)", //
          "SocketObject");
      check("WriteString(client, \"hello\")", //
          "");
      // the connection reaches the server through the selector thread, so wait for it
      check("Pause(0.5); Length(server[\"ConnectedClients\"])", //
          "1");
      check("accepted = First(server[\"ConnectedClients\"]);"
          + "ByteArrayToString(SocketReadMessage(accepted))", //
          "hello");
      check("MemberQ(Sockets(), client)", //
          "True");
      check("Head(Close(client))", //
          "SocketObject");
      check("Head(Close(server))", //
          "SocketObject");
    } finally {
      Config.OS_ACCESS_ENABLED = osAccess;
    }
  }

  @Test
  public void testAHandlerSeesWhatArrives() {
    boolean osAccess = Config.OS_ACCESS_ENABLED;
    Config.OS_ACCESS_ENABLED = true;
    try {
      check("received = {}", //
          "{}");
      check("server = SocketOpen(\"127.0.0.1\", 0); port = server[\"DestinationPort\"];"
          + "listener = SocketListen(server, HandlerFunctions -> "
          + "<|\"Received\" -> Function(AppendTo(received, "
          + "ByteArrayToString(#[\"DataByteArray\"])))|>); Head(listener)", //
          "SocketListener");
      check("client = SocketConnect(\"127.0.0.1\", port); WriteString(client, \"ping\")", //
          "");
      check("Pause(0.5); received", //
          "{ping}");
      check("DeleteObject(listener)", //
          "True");
      check("Close(client); Close(server); True", //
          "True");
    } finally {
      Config.OS_ACCESS_ENABLED = osAccess;
    }
  }

  @Test
  public void testWithoutOSAccessSocketsDoNothing() {
    boolean osAccess = Config.OS_ACCESS_ENABLED;
    Config.OS_ACCESS_ENABLED = false;
    try {
      check("SocketOpen(\"127.0.0.1\", 0)", //
          "SocketOpen(127.0.0.1,0)");
      check("Sockets()", //
          "Sockets()");
    } finally {
      Config.OS_ACCESS_ENABLED = osAccess;
    }
  }
}

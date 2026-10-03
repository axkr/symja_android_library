package org.matheclipse.io.servlet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

public class PushChannelTest {

  @Test
  public void aBrowserIsLetInOnlyFromAPageOfTheServer() {
    assertTrue(UndertowLauncher.sameOrigin("http://localhost:8080", "localhost:8080"));
    assertTrue(UndertowLauncher.sameOrigin("https://Docs.Example.org", "docs.example.org"));
    assertFalse(UndertowLauncher.sameOrigin("http://elsewhere.example", "localhost:8080"));
    assertFalse(UndertowLauncher.sameOrigin("http://localhost:9090", "localhost:8080"));
    assertFalse(UndertowLauncher.sameOrigin("null", "localhost:8080"));
    assertFalse(UndertowLauncher.sameOrigin(null, "localhost:8080"));
    assertFalse(UndertowLauncher.sameOrigin("http://localhost:8080", null));
  }

  @Test
  public void aChannelSendsOnceItIsBound() {
    PushChannel channel = new PushChannel("/push");
    assertEquals("/push", channel.path());
    // not connected to a server yet: nothing happens, and nothing fails
    channel.send("lost");
    assertNull(channel.greeting());

    List<String> sent = new ArrayList<>();
    channel.bind(sent::add);
    channel.setGreeting(() -> "hello");
    channel.send("first");
    channel.send("second");
    assertEquals(List.of("first", "second"), sent);
    assertEquals("hello", channel.greeting());
  }
}

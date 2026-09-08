package org.matheclipse.graphtheory.system;

import org.junit.jupiter.api.Test;

/**
 * WXF serialization of a graph.
 *
 * <p>
 * This is the round trip the module move had to preserve: <code>WL</code> writes a graph through
 * {@link org.matheclipse.core.interfaces.IGraphExpr#fullForm()} — the ordinary
 * <code>Graph(vertices, edges, options)</code> expression — so core never has to name the
 * implementation, and reading the bytes back re-evaluates that expression into a graph again.
 */
public class GraphSerializationTest extends AbstractTestCase {

  @Test
  public void testBinarySerializeGraph() {

    check("BinarySerialize( EdgeWeight->{0.0,1.0,1.0} ) // Normal ", //
        "{56,58,102,2,115,4,82,117,108,101,115,10,69,100,103,101,87,101,105,103,104,116,\n" //
            + "102,3,115,4,76,105,115,116,114,0,0,0,0,0,0,0,0,114,0,0,0,0,0,0,240,63,114,0,0,0,\n" //
            + "0,0,0,240,63}");
    check("BinarySerialize( {1->2,2->3,3->1} ) // Normal ", //
        "{56,58,102,3,115,4,76,105,115,116,102,2,115,4,82,117,108,101,67,1,67,2,102,2,115,\n"
            + "4,82,117,108,101,67,2,67,3,102,2,115,4,82,117,108,101,67,3,67,1}");
    check("BinarySerialize(Graph({1,2,3},{1->2,2->3,3->1},{EdgeWeight->{0.0,1.0,1.0}})) // Normal ", //
        "{56,58,102,3,115,5,71,114,97,112,104,102,3,115,4,76,105,115,116,67,1,67,2,67,3,\n" //
            + "102,3,115,4,76,105,115,116,102,2,115,12,68,105,114,101,99,116,101,100,69,100,103,\n" //
            + "101,67,1,67,2,102,2,115,12,68,105,114,101,99,116,101,100,69,100,103,101,67,2,67,\n" //
            + "3,102,2,115,12,68,105,114,101,99,116,101,100,69,100,103,101,67,3,67,1,102,1,115,\n" //
            + "4,76,105,115,116,102,2,115,4,82,117,108,101,115,10,69,100,103,101,87,101,105,103,\n" //
            + "104,116,102,3,115,4,76,105,115,116,114,0,0,0,0,0,0,0,0,114,0,0,0,0,0,0,240,63,\n" //
            + "114,0,0,0,0,0,0,240,63}");
  }
}

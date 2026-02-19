package org.matheclipse.core.reflection.system;

import java.util.ArrayList;
import java.util.List;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * Implements TreeForm(expr)
 * <p>
 * Displays expr as a tree with different levels at different depths by directly generating a
 * Graphics AST.
 */
public class TreeForm extends AbstractFunctionEvaluator {

  private static class TreeNode {
    IExpr label; // The Head or Atom to display
    IExpr fullExpr; // The full expression at this node (for Tooltips)
    int depth;
    double x, y;
    double width; // Estimated display width
    List<TreeNode> children = new ArrayList<>();

    public TreeNode(IExpr label, IExpr fullExpr, int depth) {
      this.label = label;
      this.fullExpr = fullExpr;
      this.depth = depth;
      // Estimate width based on string length.
      // 0.6 is a rough heuristic for char width relative to height in standard fonts
      String str = label.toString();
      this.width = Math.max(1.0, str.length() * 0.6);
    }
  }

  @Override
  public IExpr evaluate(final IAST ast, final EvalEngine engine) {
    if (ast.argSize() < 1) {
      return F.NIL;
    }

    IExpr expr = ast.arg1();
    int maxLevel = Integer.MAX_VALUE;

    // TreeForm[expr, n]
    if (ast.argSize() >= 2 && ast.arg2().isInteger()) {
      maxLevel = ast.arg2().toIntDefault(Integer.MAX_VALUE);
    }

    // Capture Options
    IASTAppendable graphicsOptions = F.ListAlloc();
    boolean showLabels = true; // Default to showing text labels
    boolean tooltips = true;

    for (int i = 1; i < ast.size(); i++) {
      IExpr arg = ast.get(i);
      if (arg.isRuleAST()) {
        graphicsOptions.append(arg);
        // Check VertexLabeling option
        if (arg.first() == S.VertexLabels) {
          IExpr val = ((IAST) arg).arg2();
          if (val.isFalse()) {
            showLabels = false;
            tooltips = false;
          } else if (val.equals(S.Automatic)) {
            showLabels = true;
            tooltips = true;
          }
        }
      }
    }

    // 1. Build the Tree Structure
    TreeNode root = buildTree(expr, 0, maxLevel);

    // 2. Calculate Layout considering widths and max depth
    calculateLayout(root);

    // 3. Generate Graphics Primitives
    IASTAppendable primitives = F.ListAlloc();

    // 3a. Draw Edges (Gray Lines)
    primitives.append(F.Gray);
    drawEdges(root, primitives);

    // 3b. Draw Vertices (Text or Points)
    primitives.append(F.Black);
    drawVertices(root, primitives, showLabels, tooltips);

    // 4. Default Options
    if (!containsOption(graphicsOptions, S.AspectRatio)) {
      graphicsOptions.append(F.Rule(S.AspectRatio, S.Automatic));
    }

    IExpr result = F.Graphics(primitives, graphicsOptions);
    // System.out.println("Generated TreeForm Graphics: " + result);
    return result;
  }

  /**
   * Recursively builds the internal tree structure.
   */
  private TreeNode buildTree(IExpr expr, int currentDepth, int maxLevel) {
    // Determine the label: If AST, use Head. If Atom, use Atom itself.
    TreeNode node;
    if (expr.isAST() && currentDepth < maxLevel) {
      IExpr label = expr.isAST() ? expr.head() : expr;
      node = new TreeNode(label, expr, currentDepth);
      for (IExpr arg : (IAST) expr) {
        TreeNode child = buildTree(arg, currentDepth + 1, maxLevel);
        node.children.add(child);
      }
    } else {
      node = new TreeNode(expr, expr, currentDepth);
    }
    return node;
  }

  /**
   * Calculates X and Y coordinates.
   */
  private void calculateLayout(TreeNode root) {
    // 1. Calculate X based on accumulation of children widths
    // A simplified layout strategy:
    // - Post-order traversal to determine subtree width.
    // - Pre-order traversal to assign positions.
    // Here we use a simpler strategy suitable for standard trees:
    // Flatten leaves and space them out based on labels, then parent centers over children.

    // Using a simpler recursive approach to assign X:
    // We maintain a "next available X" per depth level or globally for leaves?
    // Let's use the standard "Leaves define width" approach but account for variable
    // label widths.

    CurrentX tracker = new CurrentX();
    assignX(root, tracker);

    // 2. Assign Y
    // Scale Y by a factor to prevent overlapping vertical levels if text is high
    double yStep = 2.0;
    assignY(root, yStep);
  }

  private static class CurrentX {
    double value = 0.0;
  }

  private void assignX(TreeNode node, CurrentX tracker) {
    if (node.children.isEmpty()) {
      // It's a leaf. Its position is current accumulator + half its width
      node.x = tracker.value + (node.width / 2.0);
      tracker.value += node.width + 0.5; // +0.5 padding between nodes
    } else {
      // Process children
      for (TreeNode child : node.children) {
        assignX(child, tracker);
      }

      // Center parens over children
      double firstChildX = node.children.get(0).x;
      double lastChildX = node.children.get(node.children.size() - 1).x;
      double childrenCenter = (firstChildX + lastChildX) / 2.0;

      // If the node is wider than its children span, we might need to shift things.
      // But for simple "TreeForm" logic, centering usually suffices or
      // pushing the tracker if the parent is huge.
      // In this specific implementation, we prioritize children structure
      // and let the parent float centered above.
      // If parent is extremely wide, it might overlap neighbors, but
      // that is a complex Reingold-Tilford edge case.
      // We ensure the parent is at least as far right as the tracker allows
      // to avoid overlapping left-side subtrees if the parent is a leaf-like node previously?
      // (Not applicable here as we recurse first).

      // Check if current tracker (pushed by previous siblings of this parent or previous subtrees)
      // forces us to push the children? No, we process children first in this recursion,
      // so tracker is already at the end of the children.

      node.x = childrenCenter;

      // Optimization: If the parent label is very wide, we should technically
      // ensure we reserve that space.
      // The simple centering over children assumes children are wide enough.
      // If node.width > (lastChildX - firstChildX), this node sticks out.
      // We won't re-adjust children here to keep code simple.
    }
  }

  private void assignY(TreeNode node, double yStep) {
    node.y = -node.depth * yStep;
    for (TreeNode child : node.children) {
      assignY(child, yStep);
    }
  }

  private void drawEdges(TreeNode node, IASTAppendable primitives) {
    for (TreeNode child : node.children) {
      // Line[{{x1, y1}, {x2, y2}}]
      IAST p1 = F.List(F.num(node.x), F.num(node.y));
      IAST p2 = F.List(F.num(child.x), F.num(child.y));
      primitives.append(F.Line(F.List(p1, p2)));

      // Recurse
      drawEdges(child, primitives);
    }
  }

  private void drawVertices(TreeNode node, IASTAppendable primitives, boolean showLabels,
      boolean tooltips) {
    IAST pos = F.List(F.num(node.x), F.num(node.y));

    IExpr graphicElement;

    if (showLabels) {
      // Use Framed to create a box around the label with LightYellow background and LightGray
      // border
      IAST framedLabel = F.Framed(node.label, F.Rule(S.FrameStyle, S.LightGray),
          F.Rule(S.Background, S.LightYellow));

      // Use pure text vector centering
      graphicElement = F.Text(framedLabel, pos, F.List(F.C0, F.C0));
    } else {
      // Just a Point
      graphicElement = F.Point(pos);
    }

    if (tooltips) {
      // Tooltip[Element, FullExpression]
      primitives.append(F.Tooltip(graphicElement, node.fullExpr));
    } else {
      primitives.append(graphicElement);
    }

    for (TreeNode child : node.children) {
      drawVertices(child, primitives, showLabels, tooltips);
    }
  }


  private boolean containsOption(IAST options, ISymbol key) {
    for (IExpr rule : options) {
      if (rule.isRuleAST(key)) {
        return true;
      }
    }
    return false;
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return ARGS_1_2;
  }
}

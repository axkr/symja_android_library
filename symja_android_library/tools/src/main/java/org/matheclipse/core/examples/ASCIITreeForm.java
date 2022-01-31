package org.matheclipse.core.examples;

import java.util.ArrayList;
import java.util.List;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;

/** Display an ASCII tree of a Symja expression */
public class ASCIITreeForm {

  static class TreeNode<T> {
    private T data;
    private List<TreeNode<T>> children;

    TreeNode(T data, List<TreeNode<T>> children) {
      this.data = data;
      this.children = children;
    }

    public T getData() {
      return data;
    }

    public List<TreeNode<T>> getChildren() {
      return children;
    }
  }

  private static String asciiDisplay(TreeNode<String> root) {
    int indent = 0;
    StringBuilder sb = new StringBuilder();
    asciiTail(root, indent, sb);
    return sb.toString();
  }

  private static void asciiTail(TreeNode<String> node, int indent, StringBuilder sb) {
    sb.append(getIndentString(indent) + "+-" + node.getData() + "\n");
    for (TreeNode<String> t : node.getChildren()) {
      if (t.getChildren() == null) {
        sb.append(getIndentString(indent + 1) + "+-" + t.getData() + "\n");
      } else {
        asciiTail(t, indent + 1, sb);
      }
    }
  }

  private static String getIndentString(int indent) {
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < indent; i++) {
      if (i == 0 || i > 1) {
        sb.append(" ");
      } else {
        sb.append("| ");
      }
    }
    return sb.toString();
  }

  private static TreeNode<String> convert(IExpr expr) {
    if (expr.isAST()) {
      IAST ast = (IAST) expr;
      List<TreeNode<String>> children = new ArrayList<TreeNode<String>>();
      for (int i = 1; i < ast.size(); i++) {
        children.add(convert(ast.get(i)));
      }
      return new TreeNode<String>(ast.head().toString(), children);
    }
    return new TreeNode<String>(expr.toString(), null);
  }

  public static void main(String[] args) {
    IExpr expr = F.Plus(F.x, F.Times(F.Pi, F.Sin(F.y), F.z));
    TreeNode<String> tn1 = convert(expr);

    // Print:
    // +-Plus
    // +-x
    // +-Times
    // | +-Pi
    // | +-Sin
    // | +-y
    // | +-z

    System.out.println(asciiDisplay(tn1));
  }
}

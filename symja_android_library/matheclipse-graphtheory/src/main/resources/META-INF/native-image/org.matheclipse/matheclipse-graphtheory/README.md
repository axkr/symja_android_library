# GraalVM reachability metadata for matheclipse-graphtheory

## reflection

* **`ExprEdge`, `ExprWeightedEdge` - no-argument constructors.** Graphs are built as
  `new DefaultDirectedGraph<IExpr, ExprEdge>(ExprEdge.class)` and similar (`GraphExpr`,
  `GraphUtil`, `GraphImport`). JGraphT turns the class into an edge supplier and creates every
  edge with `getDeclaredConstructor().newInstance()`. Native-image only keeps a constructor that
  is registered, so without these entries every `Graph[...]` fails with
  `NoSuchMethodException: ...ExprEdge.<init>()`.

  Any new edge class passed to a JGraphT graph constructor by `.class` needs an entry here too.

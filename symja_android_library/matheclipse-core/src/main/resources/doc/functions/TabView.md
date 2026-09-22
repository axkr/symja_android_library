## TabView

```
TabView({lbl1 -> expr1, lbl2 -> expr2, ...})
```

> shows a strip of tabs labelled `lbl1, lbl2, ...` over the pane of the tab selected, the first.

```
TabView({lbl1 -> expr1, lbl2 -> expr2, ...}, i)
```

> selects tab number `i`.

```
TabView({v1 -> {lbl1, expr1}, v2 -> {lbl2, expr2}, ...}, v)
```

> selects the tab whose value is `v`.

The selector may be a `Dynamic`, as a `Manipulate` writes it to drive the tabs from a control of
its own; it is read at its current value, and one that names no tab shows the first. As a picture
- `ExportString(..., "SVG")`, or a cell of a `Grid` - the selected tab is set in bold above its
pane, and the web notebook shows the selected pane on its own, a 3D pane in its interactive view.

See
* [Wolfram Documentation - TabView](https://reference.wolfram.com/language/ref/TabView.html)

### Examples

```
>> TabView({"a" -> 1, "b" -> 2}, 2)
TabView({a->1,b->2},2)
```

### Related terms
[Manipulate](Manipulate.md)

## Selectable

```
Selectable
```

> an option for displayed objects, cells and notebooks giving whether their contents can be selected interactively (Wolfram Language 3.0).

Symja recognises `Selectable` as a built-in `System` symbol, so a package or a front end that writes it - the WLJS notebook's graphics code does - refers to that symbol rather than creating one in its own context. Symja does not act on it.

### Implementation status

* &#x1F9EA; - experimental

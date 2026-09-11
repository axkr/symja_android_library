## ColorOutput

```
ColorOutput
```

> an option for graphics functions giving the type of colour output to produce (Wolfram Language 2.0; now obsolete, superseded by `ColorFunction` and `ColorData`).

Symja recognises `ColorOutput` as a built-in `System` symbol, so a package or a front end that writes it - the WLJS notebook's graphics code does - refers to that symbol rather than creating one in its own context. Symja does not act on it.

### Implementation status

* &#x1F9EA; - experimental

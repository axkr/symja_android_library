## TickLabels

```
TickLabels
```

> an option for `AxisObject` giving how the tick marks are labelled: `Automatic`, `All`, `None` or a list of labels (Wolfram Language 12.3).

Symja recognises `TickLabels` as a built-in `System` symbol, so a package or a front end that writes it - the WLJS notebook's graphics code does - refers to that symbol rather than creating one in its own context. Symja does not act on it.

It is not an option of `Graphics` in the Wolfram Language. The WLJS notebook uses it as one for the colour-scale legend it draws (`TickLabels -> {False, False, False, True}`), which is how the name reaches Symja.

### Implementation status

* &#x1F9EA; - experimental

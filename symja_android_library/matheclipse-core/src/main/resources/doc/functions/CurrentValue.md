## CurrentValue

```
CurrentValue
```

> a function giving the current value of an item of the Wolfram System and its interface (Wolfram Language 6.0).

Symja recognises `CurrentValue` as a built-in `System` symbol, so a package or a front end that writes it - the WLJS notebook's graphics code does - refers to that symbol rather than creating one in its own context. Symja does not act on it.

### Implementation status

* &#x1F9EA; - experimental

## AutomaticImageSize

```
AutomaticImageSize
```

> an undocumented option name: the Wolfram Language reference has no page for it.

Symja recognises `AutomaticImageSize` as a built-in `System` symbol, so a package or a front end that writes it - the WLJS notebook's graphics code does - refers to that symbol rather than creating one in its own context. Symja does not act on it.

The WLJS notebook lists it among the options of `Graphics3D` it understands, which is why Symja knows the name; whether it is a `System` symbol in the Wolfram Language is not confirmed by the documentation.

### Implementation status

* &#x1F9EA; - experimental

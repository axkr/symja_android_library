## ControllerMethod

```
ControllerMethod
```

> an option for `Manipulate`, `Graphics3D`, `Plot3D` and related functions giving how an external controller device applies (Wolfram Language 6.0).

Symja recognises `ControllerMethod` as a built-in `System` symbol, so a package or a front end that writes it - the WLJS notebook's graphics code does - refers to that symbol rather than creating one in its own context. Symja does not act on it.

### Implementation status

* &#x1F9EA; - experimental

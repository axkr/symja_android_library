## URLExecute

```
URLExecute("url")
```

> read the web address `"url"` and import what it answers with. If the server says it answers with JSON (its `Content-Type` is `application/json`, `text/json` or ends with `+json`) the answer is imported like `ImportString(answer, "JSON")`: a JSON object becomes a list of rules and a JSON array a list. Any other answer is returned as a string.

```
URLExecute("url", {"param1" -> value1, "param2" -> value2, ...})
```

> append the parameters, URL-encoded, to the query of `"url"`. The parameters may also be given as an association.

```
URLExecute("url", {"param1" -> value1, ...}, "format")
```

> import the answer in `"format"`, one of the formats of `ImportString` like `"JSON"`, `"RawJSON"`, `"Table"` or `"Text"`, whatever the server says it answers with. The `"String"` and `"ExpressionJSON"` formats are not supported, because they would evaluate the answer as Symja code.

In a session which is confined to a directory or which has no file system access, only `http` and `https` addresses on the public internet can be read.

### Examples

Search the Wikipedia API for "Leonhard Euler" and get the answer as nested lists of rules:

```
>> URLExecute("https://en.wikipedia.org/w/api.php", {"action" -> "query", "list" -> "search", "srsearch" -> "Leonhard Euler", "format" -> "json"})
```

The result has the shape:

```
{"batchcomplete" -> "", "continue" -> {"sroffset" -> 10, "continue" -> "-||"}, "query" -> {"searchinfo" -> {"totalhits" -> ...}, "search" -> {{"ns" -> 0, "title" -> "Leonhard Euler", "pageid" -> 17902, ...}, ...}}}
```

### Related terms 
[Import](Import.md), [ImportString](ImportString.md), [URLDecode](URLDecode.md), [URLEncode](URLEncode.md)

### Implementation status

* &#x2611; - partially implemented

### Github

* [Implementation of URLExecute](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/FileFunctions.java) 

# Date
- 20200822

# Context
- matheclipse-api module. New parameter `s` or `strict` for `SymjaServer`.

# Decision
- if parameter 's' or 'strict' is set to a value (i.e. `s=true`) the web API uses "strict Symja syntax" parser rules and doesn't try to interpret the input with the FuzzyParser.

#Consequences
- you can evaluate a Symja expression in the matheclipse-api module without "side-effects" from fuzzy parsing.
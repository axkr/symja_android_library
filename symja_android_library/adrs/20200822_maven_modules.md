# Date
- 20200822

# Context
- the main library is getting to big to be used in Android eco system

# Decision
- we will reduce `matheclipse-core` from big libraries to make the porting to Android easier
- we will use `matheclipse-io` to use more heavier libraries like "tablesaw" and "file IO"

#Consequences
- the goal should be to use `matheclipse-core` directly for Java 8 Android development
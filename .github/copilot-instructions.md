# GitHub Copilot Instructions for Symja Android Library

## Project Overview
This is a multi-module Maven Java project for the Symja symbolic mathematics library. The project consists of several interconnected modules that provide symbolic computation, mathematical parsing, and various mathematical operations.

## Module Structure
- **matheclipse-core**: Core symbolic mathematics engine
- **matheclipse-parser**: Mathematical expression parsing
- **matheclipse-api**: Public API interfaces
- **matheclipse-io**: Input/output operations
- **matheclipse-external**: External library integrations
- **matheclipse-gpl**: GPL-licensed components
- **matheclipse-image**: Image processing capabilities
- **matheclipse-nlp**: Natural language processing
- **matheclipse-script**: Scripting capabilities
- **matheclipse-discord**: Discord bot integration
- **matheclipse-logging**: Logging utilities
- **matheclipse-md2html**: Markdown to HTML conversion
- **tools**: Development and utility tools

## Coding Standards & Best Practices

### General Java Guidelines
- Use Java 11+ features when appropriate
- Follow standard Java naming conventions (camelCase for methods/variables, PascalCase for classes)
- Prefer immutable objects and functional programming patterns where applicable
- Use meaningful variable and method names that reflect mathematical concepts
- Document complex mathematical algorithms thoroughly

### Mathematical Code Patterns
- When implementing mathematical functions, include comprehensive Javadoc with mathematical notation
- Use descriptive parameter names (e.g., `coefficient`, `exponent`, `realPart`, `imaginaryPart`)
- Include examples in documentation showing mathematical usage
- Handle special cases (NaN, infinity, zero) explicitly
- Consider numerical stability for floating-point operations

### Testing with JUnit Jupiter
- Use JUnit Jupiter for all unit tests
- Test class naming: `ClassNameTest` (e.g., `PolynomialTest`)
- Test method naming: `should_expectedBehavior_when_condition()` (e.g., `should_returnZero_when_multiplyingByZero()`)
- Use `@DisplayName` for complex mathematical test descriptions
- Group related tests using `@Nested` classes
- Use parameterized tests (`@ParameterizedTest`) for testing multiple mathematical cases
- Include edge cases: zero, infinity, NaN, negative numbers, complex numbers
- Test mathematical properties (commutativity, associativity, distributivity)

### Maven Module Dependencies
- Keep module dependencies minimal and well-defined
- Core mathematical functionality should be in matheclipse-core
- Parser-related code belongs in matheclipse-parser 
- Public JSON API should be in matheclipse-api
- External integrations go in matheclipse-external

### Error Handling
- Use specific exception types for mathematical errors (e.g., `DivisionByZeroException`, `InvalidExpressionException`)
- Provide meaningful error messages that include mathematical context
- Handle symbolic vs. numerical computation errors appropriately
- Use proper exception hierarchy for mathematical operations

### Performance Considerations
- Cache frequently computed mathematical results
- Use appropriate data structures for mathematical objects (e.g., sparse representations for polynomials)
- Consider memory usage for large symbolic expressions
- Profile mathematical operations for performance bottlenecks

### Documentation Standards
- Include mathematical notation in Javadoc using LaTeX-style syntax when helpful
- Provide usage examples for mathematical functions
- Document algorithm complexity (time and space)
- Reference mathematical literature for complex algorithms
- Include pre-conditions and post-conditions for mathematical operations

### Code Examples

#### Test Method Example
```java
@Test
@DisplayName("Should compute correct derivative for polynomial x^3 + 2x^2 + x + 1")
void should_computeCorrectDerivative_when_polynomialIsCubic() {
    // Given
    Polynomial polynomial = new Polynomial(1, 1, 2, 3); // 3x^3 + 2x^2 + x + 1
    
    // When
    Polynomial derivative = polynomial.derivative();
    
    // Then
    Polynomial expected = new Polynomial(0, 1, 4, 9); // 9x^2 + 4x + 1
    assertThat(derivative).isEqualTo(expected);
}
```

#### Parameterized Test Example
```java
@ParameterizedTest
@DisplayName("Should evaluate trigonometric functions correctly for special angles")
@CsvSource({
    "0, 0, 1",
    "30, 0.5, 0.866",
    "45, 0.707, 0.707",
    "60, 0.866, 0.5",
    "90, 1, 0"
})
void should_evaluateTrigFunctions_when_usingSpecialAngles(
        double degrees, double expectedSin, double expectedCos) {
    // Given
    double radians = Math.toRadians(degrees);
    
    // When & Then
    assertThat(MathFunctions.sin(radians)).isCloseTo(expectedSin, within(0.001));
    assertThat(MathFunctions.cos(radians)).isCloseTo(expectedCos, within(0.001));
}
```

### Integration Guidelines
- When adding new mathematical functions, ensure they integrate properly with the symbolic engine
- Consider both symbolic and numerical evaluation modes
- if possible use similar camel-cased method names derived from Sympy (https://github.com/sympy/sympy) function names
- Implement proper toString() methods for mathematical expressions  

### Debugging Mathematical Code
- Include detailed logging for complex mathematical computations
- Use assertions to verify mathematical invariants
- Provide helper methods to visualize mathematical expressions
- Include step-by-step computation traces for debugging

### External Dependencies
- Prefer well-maintained mathematical libraries (hipparchus, apfloat, JAS Java Algebra system, JGraphT, LogicNG etc.)
- Document mathematical library choices and their trade-offs
- Isolate external dependencies in matheclipse-external module
- Provide fallback implementations when possible

### Version Control Practices
- Use descriptive commit messages that include mathematical context
- Group related mathematical functionality in single commits
- Tag releases with clear version numbers following semantic versioning
- Include mathematical examples in commit descriptions for complex changes

## Common Mathematical Patterns to Implement
- Visitor pattern for expression traversal
- Strategy pattern for different evaluation modes (symbolic vs. numerical)
- Factory pattern for mathematical object creation 
- Builder pattern for complex mathematical expressions
- Template method pattern for common mathematical algorithms
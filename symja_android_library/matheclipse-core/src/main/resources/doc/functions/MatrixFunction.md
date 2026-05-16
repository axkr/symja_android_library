## MatrixFunction

```
MatrixFunction(function-head, matrix)
```

> computes the matrix function of the `matrix`.
 
See
* [Wikipedia - Analytic function of a matrix](https://en.wikipedia.org/wiki/Analytic_function_of_a_matrix)
* [Wikipedia - Matrix exponential](https://en.wikipedia.org/wiki/Matrix_exponential)

### Examples

An example from Wikipedia:

```
>> mf=MatrixFunction(Gamma, {{1,3},{2,1}})
{{1/2*(Gamma(1-Sqrt(6))+Gamma(1+Sqrt(6))),1/2*Sqrt(3/2)*(-Gamma(1-Sqrt(6))+Gamma(1+Sqrt(6)))},{(-Gamma(1-Sqrt(6))+Gamma(1+Sqrt(6)))/Sqrt(6),1/2*(Gamma(1-Sqrt(6))+Gamma(1+Sqrt(6)))}}

>> N(mf)
{{2.81144,0.407999},{0.271999,2.81144}}
```

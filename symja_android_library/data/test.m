BeginPackage["test`"]
Unprotect @@ Names["test`*"];
ClearAll @@ Names["test`*"];
 
f::usage = "f[x]"
g::usage = "g[x]"
Begin["`Private`"]
 
f[x_] := Module[{}, x^2];
g[x_] := Module[{}, x^4];
 
End[]
Protect @@ Names["test`*"];
EndPackage[]
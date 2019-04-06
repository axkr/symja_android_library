(* Mathematica Package *)
(* Created by Mathematica Plugin for IntelliJ IDEA, see http://wlplugin.halirutan.de/ *)

(* :Title: SymJa *)
(* :Context: SymJa` *)
(* :Author: Patrick Scheibe *)
(* :Date: 2018-09-26 *)

(* :Package Version: 0.1 *)
(* :Mathematica Version: 11.3 *)
(* :Discussion: This package provides functionality to automatically extract integration rules from Rubi and to
 * create JUnit tests from the test-suite problems of Rubi *)

BeginPackage["SymJa`"];

ExportRubiRules::usage = "ExportRubiRules[] write the full set of Rubi integration rules into a file";

Begin["`Private`"];

$LoadShowSteps = False;
Get["Rubi`"];

End[]; (* `Private` *)

EndPackage[]
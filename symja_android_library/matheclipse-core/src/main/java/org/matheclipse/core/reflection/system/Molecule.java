package org.matheclipse.core.reflection.system;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.expression.data.MoleculeExpr;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IAssociation;
import org.matheclipse.core.interfaces.IExpr;

/**
 * Molecule("C") Molecule(List("C", "O"), List(Bond(List(1, 2), "Single")))
 */
public class Molecule extends AbstractFunctionEvaluator {

  private static final Map<String, String> NAME_TO_SMILES = new HashMap<>();

  static {
    NAME_TO_SMILES.put("acetaldehyde", "CC=O");
    NAME_TO_SMILES.put("acetic acid", "CC(=O)O");
    NAME_TO_SMILES.put("acetone", "CC(=O)C");
    NAME_TO_SMILES.put("acetylene", "C#C");
    NAME_TO_SMILES.put("benzene", "c1ccccc1");
    NAME_TO_SMILES.put("carbon dioxide", "O=C=O");
    NAME_TO_SMILES.put("carbon monoxide", "[C-]#[O+]");
    NAME_TO_SMILES.put("ethanol", "CCO");

    // Curated names use explicitly branched Hydrogens
    NAME_TO_SMILES.put("water", "O([H])[H]");
    NAME_TO_SMILES.put("methane", "C([H])([H])([H])[H]");
    NAME_TO_SMILES.put("ammonia", "N([H])([H])[H]");
    NAME_TO_SMILES.put("ethane", "C([H])([H])([H])C([H])([H])[H]");
  }

  @Override
  public IExpr evaluate(final IAST ast, EvalEngine engine) {
    if (ast.argSize() == 1) {
      IExpr arg1 = ast.arg1();
      if (arg1.isString()) {
        String input = arg1.toString().replaceAll("\"", "");
        String smiles = NAME_TO_SMILES.getOrDefault(input.toLowerCase(), input);

        MolGraph graph = SmilesParser.parse(smiles);
        if (graph != null) {
          IAST normalAST = graph.toExpr();
          return MoleculeExpr.newInstance(normalAST);
        } else {
          Errors.printMessage(S.Molecule, "nointerp", F.List(arg1));
          return F.NIL;
        }
      }
    } else if (ast.argSize() == 3) {
      return MoleculeExpr.newInstance(ast);
    }
    return F.NIL;
  }

  // --- Internal AST and Parsing Classes ---

  private enum BondKind {
    Single, Double, Triple, Aromatic;

    public int order() {
      switch (this) {
        case Double:
          return 2;
        case Triple:
          return 3;
        default:
          return 1;
      }
    }
  }

  private static class AtomData {
    String symbol;
    long formalCharge = 0;
    Long massNumber = null;
    boolean aromatic = false;
    Integer explicitH = null;
    int chirality = 0; // 0 = none, 1 = @ (Counterclockwise), 2 = @@ (Clockwise)

    AtomData(String symbol) {
      this.symbol = symbol;
    }

    public IExpr toExpr() {
      if (formalCharge == 0 && massNumber == null && explicitH == null) {
        return F.stringx(symbol);
      }

      IASTAppendable args = F.ast(S.Atom);
      args.append(F.stringx(symbol));

      if (formalCharge != 0) {
        args.append(F.Rule(F.stringx("FormalCharge"), F.ZZ(formalCharge)));
      }
      if (explicitH != null) {
        args.append(F.Rule(F.stringx("HydrogenCount"), F.ZZ(explicitH)));
      }
      if (massNumber != null) {
        args.append(F.Rule(F.stringx("MassNumber"), F.ZZ(massNumber)));
      }

      return args;
    }
  }

  private static class MolGraph {
    List<AtomData> atoms = new ArrayList<>();
    List<Bond> bonds = new ArrayList<>();

    static class Bond {
      int from;
      int to;
      BondKind kind;

      Bond(int from, int to, BondKind kind) {
        this.from = from;
        this.to = to;
        this.kind = kind;
      }

      public IExpr toExpr() {
        IASTAppendable indices = F.ListAlloc();
        indices.append(F.ZZ(from + 1));
        indices.append(F.ZZ(to + 1));
        return F.Bond(indices, F.stringx(kind.name()));
      }
    }

    public IAST toExpr() {
      IASTAppendable atomList = F.ListAlloc();
      for (AtomData a : atoms) {
        atomList.append(a.toExpr());
      }

      IASTAppendable bondList = F.ListAlloc();
      for (Bond b : bonds) {
        bondList.append(b.toExpr());
      }

      IASTAppendable stereoList = F.ListAlloc();
      for (int i = 0; i < atoms.size(); i++) {
        AtomData a = atoms.get(i);
        if (a.chirality > 0) {
          IAssociation assoc = F.assoc();
          assoc.append(F.Rule(F.stringx("StereoType"), F.stringx("Tetrahedral")));
          assoc.append(F.Rule(F.stringx("ChiralCenter"), F.ZZ(i + 1)));
          assoc.append(F.Rule(F.stringx("Direction"),
              F.stringx(a.chirality == 1 ? "Counterclockwise" : "Clockwise")));
          stereoList.append(assoc);
        }
      }

      IASTAppendable molAst = F.ast(S.Molecule);
      molAst.append(atomList);
      molAst.append(bondList);

      if (stereoList.argSize() > 0) {
        molAst.append(F.List(F.Rule(S.StereochemistryElements, stereoList)));
      } else {
        molAst.append(F.ListAlloc());
      }

      return molAst;
    }
  }

  private static class SmilesParser {
    public static MolGraph parse(String input) {
      MolGraph graph = new MolGraph();
      char[] bytes = input.toCharArray();
      int pos = 0;
      Integer prev = null;
      List<Integer> stack = new ArrayList<>();
      BondKind pending = null;

      // Ring bonds are deferred and appended at the end of the primary traversal
      List<MolGraph.Bond> ringBonds = new ArrayList<>();

      class RingClosure {
        int number, atom;
        BondKind bond;

        RingClosure(int number, int atom, BondKind bond) {
          this.number = number;
          this.atom = atom;
          this.bond = bond;
        }
      }
      List<RingClosure> rings = new ArrayList<>();

      while (pos < bytes.length) {
        char c = bytes[pos];
        if (c == '(') {
          if (prev != null)
            stack.add(prev);
          pos++;
        } else if (c == ')') {
          if (pending != null || stack.isEmpty())
            return null;
          prev = stack.remove(stack.size() - 1);
          pos++;
        } else if (c == '-') {
          pending = BondKind.Single;
          pos++;
        } else if (c == '=') {
          pending = BondKind.Double;
          pos++;
        } else if (c == '#') {
          pending = BondKind.Triple;
          pos++;
        } else if (c == ':') {
          pending = BondKind.Aromatic;
          pos++;
        } else if (c >= '0' && c <= '9') {
          if (prev == null)
            return null;
          int ringNum = c - '0';
          boolean found = false;
          for (int i = 0; i < rings.size(); i++) {
            if (rings.get(i).number == ringNum) {
              RingClosure open = rings.remove(i);
              BondKind kind = pending != null ? pending : open.bond;
              if (kind == null) {
                boolean prevAromatic = graph.atoms.get(prev).aromatic;
                boolean openAromatic = graph.atoms.get(open.atom).aromatic;
                // An implicit bond is Aromatic ONLY if BOTH connected atoms are aromatic
                kind = (prevAromatic && openAromatic) ? BondKind.Aromatic : BondKind.Single;
              }
              // Defer ring closures
              ringBonds.add(new MolGraph.Bond(prev, open.atom, kind));
              found = true;
              pending = null;
              break;
            }
          }
          if (!found) {
            rings.add(new RingClosure(ringNum, prev, pending));
            pending = null;
          }
          pos++;
        } else if (Character.isLetter(c)) {
          String sym = String.valueOf(c);
          boolean isAromatic = Character.isLowerCase(c);
          if (pos + 1 < bytes.length && Character.isLowerCase(bytes[pos + 1])) {
            String testSym = sym + bytes[pos + 1];
            if (testSym.equals("Cl") || testSym.equals("Br")) {
              sym = testSym;
              pos++;
              isAromatic = false;
            }
          }

          boolean validAliphatic =
              Arrays.asList("B", "C", "N", "O", "P", "S", "F", "Cl", "Br", "I").contains(sym);
          boolean validAromatic = Arrays.asList("b", "c", "n", "o", "p", "s").contains(sym);

          if (!validAliphatic && !validAromatic) {
            return null;
          }

          AtomData atom = new AtomData(sym.toUpperCase());
          atom.aromatic = isAromatic;
          graph.atoms.add(atom);
          int idx = graph.atoms.size() - 1;

          if (prev != null) {
            BondKind kind = pending;
            if (kind == null) {
              boolean prevAromatic = graph.atoms.get(prev).aromatic;
              kind = (prevAromatic && atom.aromatic) ? BondKind.Aromatic : BondKind.Single;
            }
            graph.bonds.add(new MolGraph.Bond(prev, idx, kind));
          }
          pending = null;
          prev = idx;
          pos++;
        } else if (c == '[') {
          int endPos = input.indexOf(']', pos);
          if (endPos == -1)
            return null;
          String bracketContent = input.substring(pos + 1, endPos);
          int p = 0;

          // 1. Mass Number
          Long mass = null;
          int massStart = p;
          while (p < bracketContent.length() && Character.isDigit(bracketContent.charAt(p)))
            p++;
          if (p > massStart)
            mass = Long.parseLong(bracketContent.substring(massStart, p));

          // 2. Symbol
          String sym = "";
          boolean aromatic = false;
          if (p < bracketContent.length()) {
            char first = bracketContent.charAt(p++);
            sym += first;
            if (Character.isLowerCase(first)) {
              aromatic = true;
              if (p < bracketContent.length() && Character.isLowerCase(bracketContent.charAt(p))) {
                sym += bracketContent.charAt(p++);
              }
            } else if (Character.isUpperCase(first)) {
              if (p < bracketContent.length() && Character.isLowerCase(bracketContent.charAt(p))) {
                sym += bracketContent.charAt(p++);
              }
            }
          }
          if (aromatic)
            sym = sym.substring(0, 1).toUpperCase() + (sym.length() > 1 ? sym.substring(1) : "");
          AtomData atom = new AtomData(sym);
          atom.massNumber = mass;
          atom.aromatic = aromatic;

          // 3. Chirality (@ and @@)
          while (p < bracketContent.length() && bracketContent.charAt(p) == '@') {
            atom.chirality++;
            p++;
          }

          // 4. Hydrogen Count
          if (p < bracketContent.length() && bracketContent.charAt(p) == 'H') {
            p++;
            int hStart = p;
            while (p < bracketContent.length() && Character.isDigit(bracketContent.charAt(p)))
              p++;
            if (p > hStart)
              atom.explicitH = Integer.parseInt(bracketContent.substring(hStart, p));
            else
              atom.explicitH = 1;
          } else if (!sym.equals("H")) {
            atom.explicitH = 0;
          }

          // 5. Formal Charge
          if (p < bracketContent.length()) {
            char sign = bracketContent.charAt(p);
            if (sign == '+' || sign == '-') {
              p++;
              int chargeUnit = (sign == '+') ? 1 : -1;
              int chargeStart = p;
              while (p < bracketContent.length() && Character.isDigit(bracketContent.charAt(p)))
                p++;
              if (p > chargeStart) {
                atom.formalCharge =
                    chargeUnit * Integer.parseInt(bracketContent.substring(chargeStart, p));
              } else {
                long charge = chargeUnit;
                while (p < bracketContent.length() && bracketContent.charAt(p) == sign) {
                  charge += chargeUnit;
                  p++;
                }
                atom.formalCharge = charge;
              }
            }
          }

          graph.atoms.add(atom);
          int idx = graph.atoms.size() - 1;

          if (prev != null) {
            BondKind kind = pending;
            if (kind == null) {
              boolean prevAromatic = graph.atoms.get(prev).aromatic;
              kind = (prevAromatic && atom.aromatic) ? BondKind.Aromatic : BondKind.Single;
            }
            graph.bonds.add(new MolGraph.Bond(prev, idx, kind));
          }
          pending = null;
          prev = idx;
          pos = endPos + 1;
        } else {
          return null;
        }
      }

      if (!rings.isEmpty() || !stack.isEmpty() || pending != null || graph.atoms.isEmpty()) {
        return null;
      }

      // Canonicalize by pushing ring closures to the very end of the bond list
      graph.bonds.addAll(ringBonds);

      return graph;
    }
  }
}

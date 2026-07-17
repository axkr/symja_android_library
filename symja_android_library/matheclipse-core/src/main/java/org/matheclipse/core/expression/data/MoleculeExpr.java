package org.matheclipse.core.expression.data;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.DataExpr;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;

/**
 * Represents a Molecule expression stored as data inside the MathEclipse expression tree. It wraps
 * an {@link IAST} sequence describing the molecule.
 */
public class MoleculeExpr extends DataExpr<IAST> implements Externalizable {


  public MoleculeExpr() {
    super(S.Molecule, null);
  }

  protected MoleculeExpr(IAST molecule) {
    super(S.Molecule, molecule);
  }

  public static MoleculeExpr newInstance(IAST molecule) {
    return new MoleculeExpr(molecule);
  }

  @Override
  public IExpr copy() {
    return new MoleculeExpr(fData);
  }

  /**
   * Evaluates the applied point query: RegionNearestFunction(region)(point)
   */
  public IExpr evaluate(final IAST ast, EvalEngine engine) {
    return F.NIL;
  }

  @Override
  public IAST fullForm() {
    return fData;
  }

  @Override
  public int hierarchy() {
    return IExpr.DATAID;
  }

  @Override
  public IAST normal(boolean nilIfUnevaluated) {
    if (fData == null) {
      return F.NIL;
    }
    return F.unaryAST1(S.Molecule, fData);
  }

  @Override
  public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
    this.fData = (IAST) in.readObject();
  }

  @Override
  public void writeExternal(ObjectOutput out) throws IOException {
    out.writeObject(this.fData);
  }

  @Override
  public String toString() {
    return fHead.toString() + "(" + fData.toString() + ")";
  }


}

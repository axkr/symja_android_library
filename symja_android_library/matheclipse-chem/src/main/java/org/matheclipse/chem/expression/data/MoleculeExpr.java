package org.matheclipse.chem.expression.data;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import org.matheclipse.chem.convert.ChemConvert;
import org.matheclipse.core.expression.DataExpr;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.smiles.SmilesGenerator;

/**
 * A chemical structure, wrapping CDK's {@link IAtomContainer} directly rather than the
 * <code>Molecule(atoms, bonds)</code> expression form.
 *
 * <p>
 * Storing the native object matters for more than tidiness: every substructure and property
 * function would otherwise re-parse the expression on each call, so a
 * <code>MoleculeContainsQ</code> over a list of ten thousand molecules would re-parse ten thousand
 * times per query.
 *
 * <p>
 * {@link IAtomContainer} is neither serialisable nor value-comparable, so all three of
 * {@link #writeExternal}, {@link #equals} and {@link #hashCode} route through the canonical SMILES
 * instead. The expression form is derived lazily by {@link #normal}.
 */
public class MoleculeExpr extends DataExpr<IAtomContainer> implements Externalizable {

  private static final long serialVersionUID = 8836172593474542307L;

  /** Canonical SMILES, computed on demand and then cached; the identity of this molecule. */
  private transient String fCanonicalSmiles;

  /** The <code>Molecule(atoms, bonds)</code> form, computed on demand and then cached. */
  private transient IAST fNormal;

  public MoleculeExpr() {
    super(S.Molecule, null);
  }

  protected MoleculeExpr(IAtomContainer molecule) {
    super(S.Molecule, molecule);
  }

  public static MoleculeExpr newInstance(IAtomContainer molecule) {
    return new MoleculeExpr(molecule);
  }

  /**
   * The canonical SMILES of this molecule, or the empty string when it cannot be generated.
   *
   * <p>
   * This is the value identity of a <code>MoleculeExpr</code>: two structures are the same molecule
   * exactly when their canonical SMILES agree.
   */
  public String canonicalSmiles() {
    if (fCanonicalSmiles == null) {
      fCanonicalSmiles = generateSmiles(fData, ChemConvert.CANONICAL_SMILES);
    }
    return fCanonicalSmiles;
  }

  private static String generateSmiles(IAtomContainer molecule, int flavor) {
    if (molecule == null) {
      return "";
    }
    try {
      return new SmilesGenerator(flavor).create(molecule);
    } catch (CDKException e) {
      return "";
    } catch (RuntimeException e) {
      return "";
    }
  }

  @Override
  public IExpr copy() {
    return new MoleculeExpr(fData);
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj instanceof MoleculeExpr) {
      MoleculeExpr other = (MoleculeExpr) obj;
      if (fData == null || other.fData == null) {
        return fData == other.fData;
      }
      return canonicalSmiles().equals(other.canonicalSmiles());
    }
    return false;
  }

  @Override
  public int hashCode() {
    return fData == null ? 541 : 541 + canonicalSmiles().hashCode();
  }

  @Override
  public int hierarchy() {
    return IExpr.MOLECULEID;
  }

  @Override
  public IAST fullForm() {
    return normal(false);
  }

  /**
   * The <code>Molecule({atoms}, {bonds})</code> expression form, so printing, pattern matching and
   * <code>SameQ</code> keep working. Derived from the wrapped structure rather than stored.
   */
  @Override
  public IAST normal(boolean nilIfUnevaluated) {
    if (fData == null) {
      return F.NIL;
    }
    if (fNormal == null) {
      // three arguments, as Molecule produced before this module existed: atoms, bonds, options
      fNormal = F.ternaryAST3(S.Molecule, ChemConvert.atomList(fData),
          ChemConvert.bondList(fData), ChemConvert.optionList(fData));
    }
    return fNormal;
  }

  /**
   * {@inheritDoc}
   *
   * <p>
   * {@link IAtomContainer} is not serialisable, so the canonical SMILES is written and re-parsed.
   * A molecule which does not survive that round trip fails loudly rather than deserialising into
   * something subtly different.
   */
  @Override
  public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
    String smiles = in.readUTF();
    if (smiles.isEmpty()) {
      this.fData = null;
      return;
    }
    IAtomContainer molecule = ChemConvert.fromSMILES(smiles);
    if (molecule == null) {
      throw new IOException("cannot deserialize Molecule from SMILES: " + smiles);
    }
    this.fData = molecule;
  }

  @Override
  public void writeExternal(ObjectOutput out) throws IOException {
    if (fData == null) {
      out.writeUTF("");
      return;
    }
    String smiles = canonicalSmiles();
    if (smiles.isEmpty()) {
      throw new IOException("cannot serialize Molecule: no canonical SMILES");
    }
    out.writeUTF(smiles);
  }

  @Override
  public String toString() {
    return "Molecule(" + canonicalSmiles() + ")";
  }
}

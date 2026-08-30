package org.matheclipse.dataset;

import org.matheclipse.core.basic.Config;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IASTDataset;
import org.matheclipse.dataset.expression.ASTDataset;
import org.matheclipse.core.io.TableFormatIO;
import org.matheclipse.dataset.builtin.DatasetFunctions;
import org.matheclipse.dataset.io.TablesawFormatIO;

/**
 * Initialize the optional <code>matheclipse-dataset</code> module.
 *
 * <p>
 * Registers <code>Dataset</code>, <code>SemanticImport</code> and
 * <code>SemanticImportString</code>, which are all backed by the vendored
 * <code>tech.tablesaw</code> fork in this module. Called from
 * <code>org.matheclipse.io.IOInit</code>, so both servlets and both consoles get them; the same
 * pattern <code>matheclipse-chem</code> uses for <code>ChemInit</code> and
 * <code>matheclipse-image</code> for <code>ImageInit</code>.
 *
 * <p>
 * Also installs {@link TableFormatIO}, through which <code>Import</code>,
 * <code>ImportString</code>, <code>Export</code> and <code>ExportString</code> in
 * <code>matheclipse-core</code> reach the CSV, TSV and XLSX readers and the CSV/TSV writer without
 * depending on this module.
 *
 * <p>
 * The <i>built-in symbols</i> registered here need the file system, so they stay behind the
 * {@link Config#FILESYSTEM_ENABLED} gate <code>DatasetFunctions</code> already used. The format
 * reader/writer does not - <code>ImportString</code> and <code>ExportString</code> work on strings
 * - so it is installed unconditionally.
 */
public class DatasetInit {

  public static void init() {
    // the CSV, TSV and XLSX readers and the CSV/TSV writer that Import, ImportString, Export and
    // ExportString reach through the interface in matheclipse-core. Installed unconditionally:
    // ImportString and ExportString work on strings and need no file system.
    TableFormatIO.install(new TablesawFormatIO());

    // lets the structural built-ins in matheclipse-core give a Dataset back rather than the plain
    // list of rows they compute - see IASTDataset#onDatasetRows
    IASTDataset.installRowFactory(DatasetInit::datasetOf);

    if (Config.FILESYSTEM_ENABLED) {
      S.SemanticImport
          .setEvaluator(new org.matheclipse.dataset.builtin.SemanticImport());
      S.SemanticImportString
          .setEvaluator(new org.matheclipse.dataset.builtin.SemanticImportString());
    }
    DatasetFunctions.initialize();
  }

  /**
   * A dataset of whatever collection this is, and {@link F#NIL} for anything that is not one.
   *
   * <p>
   * A dataset wraps a collection and a scalar comes back bare - <code>Total</code> of a dataset of
   * numbers is a number in the reference, not a dataset of one - so an atom is declined here and
   * the caller keeps what it had.
   *
   * <p>
   * An <b>empty</b> collection is still a collection: <code>dataset[6, "c"]</code> on a cell
   * holding <code>{}</code> is an empty dataset, the same as any other cell holding a list. The
   * empty case is decided before the two <code>forAll</code> tests below, which are vacuously true
   * of it and would otherwise read it as a list of no rows.
   */
  private static org.matheclipse.core.interfaces.IExpr datasetOf(
      org.matheclipse.core.interfaces.IExpr collection) {
    if (collection.isList()) {
      org.matheclipse.core.interfaces.IAST list =
          (org.matheclipse.core.interfaces.IAST) collection;
      if (list.argSize() == 0) {
        return ASTDataset.newVector(list);
      }
      return list.forAll(x -> x.isAssociation()) ? ASTDataset.newListOfAssociations(list)
          : ASTDataset.newVector(list);
    }
    if (collection.isAssociation()) {
      org.matheclipse.core.interfaces.IAssociation assoc =
          (org.matheclipse.core.interfaces.IAssociation) collection;
      if (assoc.argSize() == 0) {
        return ASTDataset.newAssociation(assoc);
      }
      return assoc.forAll(x -> x.isRuleAST() && x.second().isAssociation())
          ? ASTDataset.newAssociationOfAssociations(assoc)
          : ASTDataset.newAssociation(assoc);
    }
    return org.matheclipse.core.expression.F.NIL;
  }
}

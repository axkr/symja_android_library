/*
 *  Copyright 2014 TWO SIGMA OPEN SOURCE, LLC
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.twosigma.beakerx.symjamma.evaluator;

import static com.twosigma.beakerx.symjamma.evaluator.EnvVariablesFilter.envVariablesFilter;
import java.io.File;
import java.util.Locale;
import java.util.concurrent.Executors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.ExprEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.form.output.OutputFormFactory;
import org.matheclipse.core.form.tex.TeXFormFactory;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IExpr.SourceCodeProperties;
import org.matheclipse.core.interfaces.IExpr.SourceCodeProperties.Prefix;
import org.matheclipse.io.IOInit;
import org.matheclipse.parser.client.ParserConfig;
import com.twosigma.beakerx.BeakerXClient;
import com.twosigma.beakerx.TryResult;
import com.twosigma.beakerx.autocomplete.AutocompleteResult;
import com.twosigma.beakerx.autocomplete.MagicCommandAutocompletePatterns;
import com.twosigma.beakerx.evaluator.BaseEvaluator;
import com.twosigma.beakerx.evaluator.JobDescriptor;
import com.twosigma.beakerx.evaluator.TempFolderFactory;
import com.twosigma.beakerx.evaluator.TempFolderFactoryImpl;
import com.twosigma.beakerx.jvm.object.SimpleEvaluationObject;
import com.twosigma.beakerx.jvm.threads.BeakerCellExecutor;
import com.twosigma.beakerx.jvm.threads.CellExecutor;
import com.twosigma.beakerx.kernel.Classpath;
import com.twosigma.beakerx.kernel.EvaluatorParameters;
import com.twosigma.beakerx.kernel.ExecutionOptions;
import com.twosigma.beakerx.kernel.ImportPath;
import com.twosigma.beakerx.kernel.PathToJar;
import com.twosigma.beakerx.mimetype.MIMEContainer;
import com.twosigma.beakerx.symjamma.autocomplete.SymjaMMAAutocomplete;
import com.twosigma.beakerx.symjamma.output.LatexNotebookOutput;
import com.twosigma.beakerx.symjamma.output.MarkdownNotebookOutput;

public class SymjaMMAEvaluator extends BaseEvaluator {
  static final int OUTPUTFORM = 0;

  static final int JAVAFORM = 1;

  static final int TRADITIONALFORM = 2;

  static final int PRETTYFORM = 3;

  static final int INPUTFORM = 4;

  static final int TEXFORM = 5;

  static {
    // distinguish between lower- and uppercase identifiers
    ParserConfig.PARSER_USE_LOWERCASE_SYMBOLS = false;
    F.initSymbols();
    IOInit.init();
  }

  /** 60 seconds timeout limit as the default value for Symja expression evaluation. */
  long fSeconds = 60;

  int fUsedForm = OUTPUTFORM;

  ExprEvaluator fEvaluator;

  OutputFormFactory fOutputFactory;

  OutputFormFactory fOutputTraditionalFactory;

  OutputFormFactory fInputFactory;

  private SymjaMMAAutocomplete gac;

  // private BeakerXUrlClassLoader beakerxUrlClassLoader;

  public SymjaMMAEvaluator(
      String id,
      String sId,
      CellExecutor cellExecutor,
      TempFolderFactory tempFolderFactory,
      EvaluatorParameters evaluatorParameters,
      BeakerXClient beakerxClient,
      MagicCommandAutocompletePatterns autocompletePatterns) {
    super(
        id,
        sId,
        cellExecutor,
        tempFolderFactory,
        evaluatorParameters,
        beakerxClient,
        autocompletePatterns);
    gac = createAutocomplete(autocompletePatterns);
    outDir = envVariablesFilter(outDir, System.getenv());
    EvalEngine engine = new EvalEngine(false);
    fEvaluator = new ExprEvaluator(engine, false, (short) 100);
    fEvaluator.getEvalEngine().setFileSystemEnabled(true);
    // DecimalFormatSymbols usSymbols = new DecimalFormatSymbols(Locale.US);
    // DecimalFormat decimalFormat = new DecimalFormat("0.0####", usSymbols);
    fOutputFactory = OutputFormFactory.get(false, false, 5, 7);
    fOutputTraditionalFactory = OutputFormFactory.get(true, false, 5, 7);
    fInputFactory = OutputFormFactory.get(false, false, 5, 7);
    fInputFactory.setInputForm(true);
    fUsedForm = TEXFORM;
  }

  public SymjaMMAEvaluator(
      String id,
      String sId,
      EvaluatorParameters evaluatorParameters,
      BeakerXClient beakerxClient,
      MagicCommandAutocompletePatterns autocompletePatterns) {
    this(
        id,
        sId,
        new BeakerCellExecutor("symjamma"),
        new TempFolderFactoryImpl(),
        evaluatorParameters,
        beakerxClient,
        autocompletePatterns);
  }

  @Override
  protected void addImportToClassLoader(ImportPath arg0) {
    // TODO Auto-generated method stub

  }

  @Override
  protected void addJarToClassLoader(PathToJar pathToJar) {
    // this.beakerxUrlClassLoader.addJar(pathToJar);
  }

  @Override
  public AutocompleteResult autocomplete(String code, int caretPosition) {
    return gac.find(code, caretPosition);
  }

  private SymjaMMAAutocomplete createAutocomplete(
      MagicCommandAutocompletePatterns autocompletePatterns) {
    return new SymjaMMAAutocomplete(autocompletePatterns);
  }

  private String createClasspath(Classpath classPath) {
    StringBuilder cppBuilder = new StringBuilder();
    for (String pt : classPath.getPathsAsStrings()) {
      cppBuilder.append(pt);
      cppBuilder.append(File.pathSeparator);
    }
    String cpp = cppBuilder.toString();
    cpp += File.pathSeparator;
    cpp += System.getProperty("java.class.path");
    return cpp;
  }

  @Override
  protected void doResetEnvironment() {
    String cpp = createClasspath(classPath);
    gac = createAutocomplete(autocompletePatterns);
    executorService.shutdown();
    executorService = Executors.newSingleThreadExecutor();
  }

  @Override
  public TryResult evaluate(
      SimpleEvaluationObject seo, String code, ExecutionOptions executionOptions) {
    return evaluate(
        seo, new SymjaMMAWorkerThread(this, new JobDescriptor(code, seo, executionOptions)));
  }

  @Override
  public void exit() {
    super.exit();
    killAllThreads();
    executorService.shutdown();
    executorService = Executors.newSingleThreadExecutor();
  }

  @Override
  public ClassLoader getClassLoader() {
    // TODO Auto-generated method stub
    return null;
  }

  /**
   * Set the mode for the output format possible values <code>input, output, tex, java, traditional
   * </code>
   *
   * @param symjaMMACodeRunner
   * @param trimmedInput
   * @return
   */
  MIMEContainer metaCommand(
      final SymjaMMACodeRunner symjaMMACodeRunner, final String trimmedInput) {
    String command = trimmedInput.substring(1).toLowerCase(Locale.ENGLISH);
    if (command.equals("java")) {
      fUsedForm = SymjaMMAEvaluator.JAVAFORM;
      return new MarkdownNotebookOutput("Enabling output for JavaForm");
    } else if (command.equals("traditional")) {
      fUsedForm = SymjaMMAEvaluator.TRADITIONALFORM;
      return new MarkdownNotebookOutput("Enabling output for TraditionalForm");
    } else if (command.equals("output")) {
      fUsedForm = SymjaMMAEvaluator.OUTPUTFORM;
      return new MarkdownNotebookOutput("Enabling output for OutputForm");
      // } else if (command.equals("pretty")) {
      // symjammaEvaluator.fUsedForm = SymjaMMAEvaluator.PRETTYFORM;
      // return new MarkdownNotebookOutput("Enabling output for PrettyPrinterForm");
    } else if (command.equals("input")) {
      fUsedForm = SymjaMMAEvaluator.INPUTFORM;
      return new MarkdownNotebookOutput("Enabling output for InputForm");
    } else if (command.equals("tex")) {
      fUsedForm = SymjaMMAEvaluator.TEXFORM;
      return new MarkdownNotebookOutput("Enabling output for TeXForm");
    } else if (command.equals("timeoutoff")) {
      fSeconds = -1;
      return new MarkdownNotebookOutput("Disabling timeout for evaluation");
    } else if (command.equals("timeouton")) {
      fSeconds = 60;
      return new MarkdownNotebookOutput("Enabling timeout for evaluation to 60 seconds.");
    }
    return null;
  }

  private static final SourceCodeProperties JAVA_FORM_PROPERTIES =
      SourceCodeProperties.of(false, false, Prefix.CLASS_NAME, false);

  /**
   * Print the result in the default output form
   *
   * @param symjaMMACodeRunner
   * @param result
   * @return
   */
  TryResult printForm(final SymjaMMACodeRunner symjaMMACodeRunner, final Object result) {
    switch (fUsedForm) {
      case SymjaMMAEvaluator.JAVAFORM:
        return TryResult.createResult(((IExpr) result).internalJavaString(JAVA_FORM_PROPERTIES, -1, x -> null));
      case SymjaMMAEvaluator.TRADITIONALFORM:
        StringBuilder traditionalBuffer = new StringBuilder();
        fOutputTraditionalFactory.reset(false);
        if (fOutputTraditionalFactory.convert(traditionalBuffer, (IExpr) result)) {
          return TryResult.createResult(traditionalBuffer.toString());
        } else {
          return TryResult.createResult("ERROR-IN-TRADITIONALFORM");
        }
        // case SymjaMMAEvaluator.PRETTYFORM:
        // ASCIIPrettyPrinter3 prettyBuffer = new ASCIIPrettyPrinter3();
        // prettyBuffer.convert(result);
        // stdout.println();
        // String[] outputExpression = prettyBuffer.toStringBuilder();
        // ASCIIPrettyPrinter3.prettyPrinter(stdout, outputExpression, "Out[" + COUNTER + "]: ");
        // return "";
      case SymjaMMAEvaluator.INPUTFORM:
        StringBuilder inputBuffer = new StringBuilder();
        fInputFactory.reset(false);
        if (fInputFactory.convert(inputBuffer, (IExpr) result)) {
          return TryResult.createResult(inputBuffer.toString());
        } else {
          return TryResult.createResult("ERROR-IN-INPUTFORM");
        }
      case SymjaMMAEvaluator.TEXFORM:
        final TeXFormFactory fTeXFactory = new TeXFormFactory();
        final StringBuilder texBuilder = new StringBuilder();
        texBuilder.append("$$");
        if (fTeXFactory.convert(texBuilder, (IExpr) result, 0)) {
          texBuilder.append("$$");
          return TryResult.createResult(new LatexNotebookOutput(texBuilder.toString()));
        } else {
          return TryResult.createResult("ERROR-IN-TEXFORM");
        }
    }
    return TryResult.createResult(result);
  }
}

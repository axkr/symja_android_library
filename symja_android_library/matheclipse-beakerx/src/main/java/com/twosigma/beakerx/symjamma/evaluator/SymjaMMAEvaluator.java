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
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;
import java.util.concurrent.Executors;

import org.matheclipse.core.basic.Config;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.ExprEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.form.output.OutputFormFactory;

import com.twosigma.beakerx.BeakerXClient;
import com.twosigma.beakerx.TryResult;
import com.twosigma.beakerx.autocomplete.AutocompleteResult;
import com.twosigma.beakerx.autocomplete.MagicCommandAutocompletePatterns;
import com.twosigma.beakerx.evaluator.BaseEvaluator;
import com.twosigma.beakerx.evaluator.JobDescriptor;
import com.twosigma.beakerx.evaluator.TempFolderFactory;
import com.twosigma.beakerx.evaluator.TempFolderFactoryImpl;
import com.twosigma.beakerx.jvm.classloader.BeakerXUrlClassLoader;
import com.twosigma.beakerx.jvm.object.SimpleEvaluationObject;
import com.twosigma.beakerx.jvm.threads.BeakerCellExecutor;
import com.twosigma.beakerx.jvm.threads.CellExecutor;
import com.twosigma.beakerx.kernel.Classpath;
import com.twosigma.beakerx.kernel.EvaluatorParameters;
import com.twosigma.beakerx.kernel.ExecutionOptions;
import com.twosigma.beakerx.kernel.ImportPath;
import com.twosigma.beakerx.kernel.PathToJar;
import com.twosigma.beakerx.symjamma.autocomplete.SymjaMMAAutocomplete;

public class SymjaMMAEvaluator extends BaseEvaluator {
	/**
	 * 60 seconds timeout limit as the default value for Symja expression evaluation.
	 */
	private long fSeconds = 60;

	private final static int OUTPUTFORM = 0;

	private final static int JAVAFORM = 1;

	private final static int TRADITIONALFORM = 2;

	private final static int PRETTYFORM = 3;

	private final static int INPUTFORM = 4;
	private int fUsedForm = OUTPUTFORM;

	public ExprEvaluator fEvaluator;

	public OutputFormFactory fOutputFactory;

	private OutputFormFactory fOutputTraditionalFactory;

	private OutputFormFactory fInputFactory;
	private SymjaMMAAutocomplete gac;
	private BeakerXUrlClassLoader beakerxUrlClassLoader;

	static {
		// distinguish between lower- and uppercase identifiers
		Config.PARSER_USE_LOWERCASE_SYMBOLS = false;
		F.initSymbols(null, null, true);

	}

	public SymjaMMAEvaluator(String id, String sId, EvaluatorParameters evaluatorParameters,
			BeakerXClient beakerxClient, MagicCommandAutocompletePatterns autocompletePatterns) {
		this(id, sId, new BeakerCellExecutor("symjamma"), new TempFolderFactoryImpl(), evaluatorParameters,
				beakerxClient, autocompletePatterns);
	}

	public SymjaMMAEvaluator(String id, String sId, CellExecutor cellExecutor, TempFolderFactory tempFolderFactory,
			EvaluatorParameters evaluatorParameters, BeakerXClient beakerxClient,
			MagicCommandAutocompletePatterns autocompletePatterns) {
		super(id, sId, cellExecutor, tempFolderFactory, evaluatorParameters, beakerxClient, autocompletePatterns);
		gac = createAutocomplete(autocompletePatterns);
		outDir = envVariablesFilter(outDir, System.getenv());
		EvalEngine engine = new EvalEngine(false);
		fEvaluator = new ExprEvaluator(engine, false, 100);
		fEvaluator.getEvalEngine().setFileSystemEnabled(true);
		DecimalFormatSymbols usSymbols = new DecimalFormatSymbols(Locale.US);
		DecimalFormat decimalFormat = new DecimalFormat("0.0####", usSymbols);
		fOutputFactory = OutputFormFactory.get(false, false, decimalFormat);
		fOutputTraditionalFactory = OutputFormFactory.get(true, false, decimalFormat);
		fInputFactory = OutputFormFactory.get(false, false, decimalFormat);
		fInputFactory.setQuotes(true);
	}

	@Override
	public TryResult evaluate(SimpleEvaluationObject seo, String code, ExecutionOptions executionOptions) {
		return evaluate(seo, new SymjaMMAWorkerThread(this, new JobDescriptor(code, seo, executionOptions)));
	}

	@Override
	protected void doResetEnvironment() {
		String cpp = createClasspath(classPath);
		gac = createAutocomplete(autocompletePatterns);
		executorService.shutdown();
		executorService = Executors.newSingleThreadExecutor();
	}

	@Override
	public void exit() {
		super.exit();
		killAllThreads();
		executorService.shutdown();
		executorService = Executors.newSingleThreadExecutor();
	}

	@Override
	protected void addJarToClassLoader(PathToJar pathToJar) {
		this.beakerxUrlClassLoader.addJar(pathToJar);
	}

	private SymjaMMAAutocomplete createAutocomplete(MagicCommandAutocompletePatterns autocompletePatterns) {
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
	public AutocompleteResult autocomplete(String code, int caretPosition) {
		return gac.find(code, caretPosition);
	}

	@Override
	protected void addImportToClassLoader(ImportPath arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public ClassLoader getClassLoader() {
		// TODO Auto-generated method stub
		return null;
	}

}

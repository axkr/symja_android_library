/*
 * Copyright 2017 TWO SIGMA OPEN SOURCE, LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package com.twosigma.beakerx.symjamma.evaluator;

import java.util.concurrent.Callable;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.twosigma.beakerx.TryResult;
import com.twosigma.beakerx.evaluator.JobDescriptor;

class SymjaMMAWorkerThread implements Callable<TryResult> {
  private static final Logger LOGGER = LogManager.getLogger();

  private final JobDescriptor j;
  protected SymjaMMAEvaluator symjaEvaluator;

  SymjaMMAWorkerThread(SymjaMMAEvaluator symjaEvaluator, JobDescriptor j) {
    this.symjaEvaluator = symjaEvaluator;
    this.j = j;
  }

  @Override
  public TryResult call() {
    // NamespaceClient nc = null;
    TryResult r;
    try {
      j.outputObject.started();
      String code = j.codeToBeExecuted;
      r = symjaEvaluator.executeTask(new SymjaMMACodeRunner(symjaEvaluator, code, j.outputObject),
          j.getExecutionOptions());
    } catch (Throwable e) {
      if (e instanceof SymjaMMANotFoundException) {
        LOGGER.warn(e.getLocalizedMessage());
        r = TryResult.createError(e.getLocalizedMessage());
      } else {
        LOGGER.error("SymjaMMAWorkerThread.call() failed", e);
        r = TryResult.createError(e.getLocalizedMessage());
      }
    }
    return r;
  }
}

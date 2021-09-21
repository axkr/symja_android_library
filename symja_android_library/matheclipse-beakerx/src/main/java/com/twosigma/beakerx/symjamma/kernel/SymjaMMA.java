/*
 *  Copyright 2017 TWO SIGMA OPEN SOURCE, LLC
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
package com.twosigma.beakerx.symjamma.kernel;

import static com.twosigma.beakerx.DefaultJVMVariables.IMPORTS;
import static com.twosigma.beakerx.kernel.Utils.uuid;
import java.util.HashMap;
import com.twosigma.beakerx.BeakerXCommRepository;
import com.twosigma.beakerx.CommRepository;
import com.twosigma.beakerx.NamespaceClient;
import com.twosigma.beakerx.evaluator.Evaluator;
import com.twosigma.beakerx.handler.KernelHandler;
import com.twosigma.beakerx.kernel.CacheFolderFactory;
import com.twosigma.beakerx.kernel.CloseKernelAction;
import com.twosigma.beakerx.kernel.CustomMagicCommandsEmptyImpl;
import com.twosigma.beakerx.kernel.EvaluatorParameters;
import com.twosigma.beakerx.kernel.Kernel;
import com.twosigma.beakerx.kernel.KernelConfigurationFile;
import com.twosigma.beakerx.kernel.KernelRunner;
import com.twosigma.beakerx.kernel.KernelSocketsFactory;
import com.twosigma.beakerx.kernel.KernelSocketsFactoryImpl;
import com.twosigma.beakerx.kernel.handler.CommOpenHandler;
import com.twosigma.beakerx.kernel.magic.command.MagicCommandConfiguration;
import com.twosigma.beakerx.kernel.magic.command.MagicCommandConfigurationImpl;
import com.twosigma.beakerx.kernel.restserver.BeakerXServer;
import com.twosigma.beakerx.kernel.restserver.impl.GetUrlArgHandler;
import com.twosigma.beakerx.message.Message;
import com.twosigma.beakerx.symjamma.comm.SymjaMMACommOpenHandler;
import com.twosigma.beakerx.symjamma.evaluator.SymjaMMAEvaluator;
import com.twosigma.beakerx.symjamma.handler.SymjaMMAKernelInfoHandler;

public class SymjaMMA extends Kernel {

  private SymjaMMA(
      final String id,
      final Evaluator evaluator,
      KernelSocketsFactory kernelSocketsFactory,
      CommRepository commRepository,
      BeakerXServer beakerXServer,
      MagicCommandConfiguration magicCommandConfiguration
      //                 ,BeakerXJson beakerXJson
      ) {
    super(
        id,
        evaluator,
        kernelSocketsFactory,
        new CustomMagicCommandsEmptyImpl(),
        commRepository,
        beakerXServer,
        magicCommandConfiguration
        //            ,beakerXJson
        );
  }

  public SymjaMMA(
      final String id,
      final Evaluator evaluator,
      KernelSocketsFactory kernelSocketsFactory,
      CloseKernelAction closeKernelAction,
      CacheFolderFactory cacheFolderFactory,
      CommRepository commRepository,
      BeakerXServer beakerXServer,
      MagicCommandConfiguration magicCommandConfiguration
      //                ,BeakerXJson beakerXJson
      ) {
    super(
        id,
        evaluator,
        kernelSocketsFactory,
        closeKernelAction,
        cacheFolderFactory,
        new CustomMagicCommandsEmptyImpl(),
        commRepository,
        beakerXServer,
        magicCommandConfiguration);
    //            beakerXJson);
  }

  @Override
  public CommOpenHandler getCommOpenHandler(Kernel kernel) {
    return new SymjaMMACommOpenHandler(kernel);
  }

  @Override
  public KernelHandler<Message> getKernelInfoHandler(Kernel kernel) {
    return new SymjaMMAKernelInfoHandler(kernel);
  }

  public static void main(final String[] args) {
    KernelRunner.run(
        () -> {
          String id = uuid();
          KernelConfigurationFile configurationFile = new KernelConfigurationFile(args);
          KernelSocketsFactoryImpl kernelSocketsFactory =
              new KernelSocketsFactoryImpl(configurationFile);

          BeakerXCommRepository beakerXCommRepository = new BeakerXCommRepository();
          NamespaceClient namespaceClient =
              NamespaceClient.create(id, configurationFile, beakerXCommRepository);
          MagicCommandConfiguration magicCommandTypesFactory = new MagicCommandConfigurationImpl();
          SymjaMMAEvaluator evaluator =
              new SymjaMMAEvaluator(
                  id,
                  id,
                  getEvaluatorParameters(),
                  namespaceClient,
                  magicCommandTypesFactory.patterns());
          return new SymjaMMA(
              id,
              evaluator,
              kernelSocketsFactory,
              beakerXCommRepository,
              new SymjaMMABeakerXServer(new GetUrlArgHandler(namespaceClient)),
              magicCommandTypesFactory);
          //              new BeakerXJsonConfig());
        });
  }

  public static EvaluatorParameters getEvaluatorParameters() {
    HashMap<String, Object> kernelParameters = new HashMap<>();
    kernelParameters.put(IMPORTS, new SymjaMMADefaultVariables().getImports());
    return new EvaluatorParameters(kernelParameters);
  }
}

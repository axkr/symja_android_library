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

package com.twosigma.beakerx.symjamma.inspect;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class SerializeInspect {
  private static final Logger LOGGER = LogManager.getLogger();

  private Gson gson = new Gson();

  public String toJson(HashMap<String, ClassInspect> object) {
    Type type = new TypeToken<HashMap<String, ClassInspect>>() {}.getType();
    return gson.toJson(object, type);
  }

  public void saveToFile(String json) {
    File file = new File("beakerx_inspect.json");
    try (FileWriter fileWriter = new FileWriter(file)) {
      fileWriter.write(json);
      fileWriter.flush();
    } catch (IOException e) {
      LOGGER.error("SerializeInspect.saveToFile() failed", e);
    }
  }

  public HashMap<String, ClassInspect> fromJson(String json) {
    Type type = new TypeToken<HashMap<String, ClassInspect>>() {}.getType();
    return gson.fromJson(json, type);
  }
}

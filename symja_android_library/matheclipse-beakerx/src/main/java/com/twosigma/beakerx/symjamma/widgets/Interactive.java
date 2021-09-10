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
package com.twosigma.beakerx.symjamma.widgets;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.twosigma.beakerx.widget.InteractiveBase;
import com.twosigma.beakerx.widget.Label;


public class Interactive extends InteractiveBase {

  private static Label label;

  private static final Logger LOGGER = LogManager.getLogger();

  //  @SuppressWarnings("unchecked")
  //  public static void interact(MethodClosure function, Object... parameters) {
  //
  //    final List<ValueWidget<?>> widgets = widgetsFromAbbreviations(parameters);
  //
  //    for (ValueWidget<?> widget : widgets) {
  //      widget.getComm().addMsgCallbackList(widget.new ValueChangeMsgCallbackHandler() {
  //
  //        private void processCode(Object... params) throws Exception {
  //          Object call = function.call(getWidgetValues());
  //          if (call instanceof String || call instanceof Number) {
  //            label.setValue(call);
  //          }
  //        }
  //
  //        @Override
  //        public void updateValue(Object value, Message message) {
  //          try {
  //            processCode();
  //          } catch (Exception e) {
  //            throw new IllegalStateException("Error occurred during updating interactive
  // widget.", e);
  //          }
  //        }
  //
  //        private Object[] getWidgetValues() {
  //          List<Object> ret = new ArrayList<>(widgets.size());
  //          for (ValueWidget<?> wid : widgets) {
  //            ret.add(wid.getValue());
  //          }
  //          return ret.toArray(new Object[ret.size()]);
  //        }
  //
  //      });
  //      logger.info("interact Widget: " + widget.getClass().getName());
  //    }
  //
  //    widgets.forEach(Widget::display);
  //    Object response = function.call(widgets.stream().map(ValueWidget::getValue).toArray());
  //    if (response instanceof Widget) {
  //      ((Widget) response).display();
  //    } else {
  //      label = new Label();
  //      label.setValue(response);
  //      label.display();
  //    }
  //  }

}

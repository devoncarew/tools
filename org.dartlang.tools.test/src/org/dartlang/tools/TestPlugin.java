/*
 * Copyright (c) 2015, the Dart project authors.
 * 
 * Licensed under the Eclipse Public License v1.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 * 
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package org.dartlang.tools;

import java.util.Arrays;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.Status;
import org.osgi.framework.BundleContext;

public class TestPlugin extends Plugin {
  public static final String PLUGIN_ID = "org.dartlang.tests";

  private static TestPlugin plugin;

  public static IStatus createStatus(Throwable e) {
    return new Status(IStatus.ERROR, PLUGIN_ID, e.toString(), e);
  }

  public static TestPlugin getPlugin() {
    return plugin;
  }

  public void log(Throwable e) {
    getPlugin().getLog().log(new Status(IStatus.ERROR, PLUGIN_ID, e.getMessage(), e));
  }

  private boolean shouldRunTests() {
    return Arrays.asList(Platform.getApplicationArgs()).contains("--test");
  }

  @Override
  public void start(BundleContext context) throws Exception {
    plugin = this;

    super.start(context);

    if (shouldRunTests()) {
      TestRunner.runTests(AllTests.class, true);
    }
  }

  @Override
  public void stop(BundleContext context) throws Exception {
    super.stop(context);

    plugin = null;
  }
}

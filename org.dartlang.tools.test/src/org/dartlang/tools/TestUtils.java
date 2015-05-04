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

import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.intro.IIntroManager;
import org.eclipse.ui.intro.IIntroPart;

public class TestUtils {
  public static void closeWelcome() {
    final IIntroManager introManager = PlatformUI.getWorkbench().getIntroManager();
    IIntroPart part = introManager.getIntro();
    if (part != null) {
      introManager.closeIntro(part);
    }
  }

  public static void preFlight() {
    closeWelcome();
  }

  public static void syncExec(final TestRunnable r) throws Throwable {
    final Throwable t[] = new Throwable[1];

    Display.getDefault().syncExec(new Runnable() {
      @Override
      public void run() {
        try {
          r.run();
        } catch (Throwable e) {
          t[0] = e;
        }
      };
    });

    if (t[0] != null) {
      throw t[0];
    }
  }
}

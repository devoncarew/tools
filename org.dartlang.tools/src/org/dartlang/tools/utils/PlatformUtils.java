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
package org.dartlang.tools.utils;

import org.dartlang.tools.DartPlugin;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

public class PlatformUtils {
  public static IWorkbenchPage getActivePage() {
    return PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
  }

  public static Shell getShell() {
    return PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
  }

  public static void openUrl(String url) {
    if (url != null && !url.isEmpty()) {
      Program.launch(url);
    }
  }

  public static IViewPart showView(final String id) {
    final IViewPart[] result = new IViewPart[1];

    Display.getDefault().syncExec(new Runnable() {
      @Override
      public void run() {
        try {
          result[0] = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView(
              id);
        } catch (PartInitException e) {
          DartPlugin.logError(e);
        }
      }
    });

    return result[0];
  }

  private PlatformUtils() {

  }
}

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

import org.eclipse.jface.window.DefaultToolTip;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Shell;

// TODO: Animate in and out.

public class ToastUI {

  public static void showToast(final String message) {
    Display.getDefault().asyncExec(new Runnable() {
      @Override
      public void run() {
        final Shell shell = PlatformUtils.getShell();
        DefaultToolTip toolTip = new DefaultToolTip(shell) {
          @Override
          public Point getLocation(Point size, Event event) {
            final Rectangle workbenchWindowBounds = shell.getBounds();
            int xCoord = workbenchWindowBounds.x + workbenchWindowBounds.width - size.x - 24;
            int yCoord = workbenchWindowBounds.y + workbenchWindowBounds.height - size.y - 36;
            return new Point(xCoord, yCoord);
          }
        };
        toolTip.setHideDelay(3000);
        toolTip.setText(message);
        toolTip.show(new Point(0, 0));
      }
    });
  }

  private ToastUI() {

  }
}

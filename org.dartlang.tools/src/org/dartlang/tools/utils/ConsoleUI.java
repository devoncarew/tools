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

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsoleConstants;
import org.eclipse.ui.console.IConsoleManager;
import org.eclipse.ui.console.IConsoleView;
import org.eclipse.ui.console.MessageConsole;
import org.eclipse.ui.console.MessageConsoleStream;

public class ConsoleUI {
  private static String NAME = "Dart Tools";

  static {
    init();
  }

  private static MessageConsole console;
  private static MessageConsoleStream stdout;
  private static MessageConsoleStream stderr;

  static void init() {
    IConsoleManager consoleManager = ConsolePlugin.getDefault().getConsoleManager();
    console = new MessageConsole(NAME, null);
    consoleManager.addConsoles(new IConsole[] {console});

    stdout = console.newMessageStream();
    stdout.setEncoding("UTF-8");
    stdout.setActivateOnWrite(true);

    stderr = console.newMessageStream();
    stderr.setEncoding("UTF-8");
    stderr.setActivateOnWrite(true);

    Display.getDefault().asyncExec(new Runnable() {
      @Override
      public void run() {
        stderr.setColor(Display.getDefault().getSystemColor(SWT.COLOR_RED));
      }
    });
  }

  public static void separator() {
    if (console.getDocument().getLength() > 0) {
      stdout("\n");
    }
  }

  public static final void show() {
    IConsoleView view = (IConsoleView) PlatformUtils.showView(IConsoleConstants.ID_CONSOLE_VIEW);
    if (view != null) {
      view.display(console);
    }
  }

  public static void stderr(String str) {
    //if (str.endsWith("\n")) {
    stderr.print(str);
    //} else {
    //  stderr.println(str);
    //}
  }

  public static void stdout(String str) {
    //if (str.endsWith("\n")) {
    stdout.print(str);
    //} else {
    //  stdout.println(str);
    //}
  }

  private ConsoleUI() {

  }
}

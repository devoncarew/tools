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
package org.dartlang.tools.preferences;

import org.dartlang.tools.DartPlugin;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

public class DartPreferencePage extends PreferencePage implements IWorkbenchPreferencePage {
  public static final String PAGE_ID = "org.dartlang.tools.preferences.DartPreferencePage";

  public DartPreferencePage() {
    setPreferenceStore(DartPlugin.getPlugin().getPreferenceStore());
    setDescription("Dart Settings");
  }

  @Override
  protected Control createContents(Composite parent) {
    Label label = new Label(parent, SWT.NONE);
    //label.setText("Dart prefs");
    return label;
  }

  @Override
  public void init(IWorkbench workbench) {
  }
}

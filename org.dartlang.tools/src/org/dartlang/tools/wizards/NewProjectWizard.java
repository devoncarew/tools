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
package org.dartlang.tools.wizards;

import org.dartlang.tools.DartPlugin;
import org.dartlang.tools.builder.DartNature;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.ui.wizards.newresource.BasicNewProjectResourceWizard;

public class NewProjectWizard extends BasicNewProjectResourceWizard {
  @Override
  public boolean performFinish() {
    if (super.performFinish()) {
      IProject project = getNewProject();

      try {
        DartNature.addNature(project);
      } catch (CoreException e) {
        DartPlugin.showError("Error creating project", e);
      }

      return true;
    } else {
      return false;
    }
  }
}

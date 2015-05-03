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
package org.dartlang.tools.builder;

import org.eclipse.core.resources.ICommand;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IProjectNature;
import org.eclipse.core.runtime.CoreException;

public class DartNature implements IProjectNature {
  public static final String NATURE_ID = "org.dartlang.tools.dartNature";

  public static void addNature(IProject project) throws CoreException {
    IProjectDescription description = project.getDescription();
    String[] ids = description.getNatureIds();
    String[] newIDs = new String[ids.length + 1];
    System.arraycopy(ids, 0, newIDs, 0, ids.length);
    newIDs[ids.length] = NATURE_ID;
    description.setNatureIds(newIDs);
    project.setDescription(description, null);
  }

  public static boolean hasDartNature(IProject project) {
    try {
      return project == null ? false : project.hasNature(NATURE_ID);
    } catch (CoreException exception) {
      return false;
    }
  }

  private IProject project;

  @Override
  public void configure() throws CoreException {
    IProjectDescription description = project.getDescription();

    boolean found = false;
    for (ICommand command : description.getBuildSpec()) {
      if (command.getBuilderName().equals(DartBuilder.BUILDER_ID)) {
        found = true;
        break;
      }
    }

    if (!found) {
      ICommand command = description.newCommand();
      command.setBuilderName(DartBuilder.BUILDER_ID);
      ICommand[] oldCommands = description.getBuildSpec();
      int length = oldCommands.length;
      ICommand[] newCommands = new ICommand[length + 1];
      System.arraycopy(oldCommands, 0, newCommands, 0, length);
      newCommands[length] = command;
      description.setBuildSpec(newCommands);
      project.setDescription(description, null);
    }
  }

  @Override
  public void deconfigure() throws CoreException {
    IProjectDescription description = project.getDescription();
    ICommand[] oldCommands = description.getBuildSpec();
    int length = oldCommands.length;
    for (int i = 0; i < length; i++) {
      if (oldCommands[i].getBuilderName().equals(DartBuilder.BUILDER_ID)) {
        ICommand[] newCommands = new ICommand[length - 1];
        System.arraycopy(oldCommands, 0, newCommands, 0, i);
        System.arraycopy(oldCommands, i + 1, newCommands, i, length - i - 1);
        description.setBuildSpec(newCommands);
        project.setDescription(description, null);
        return;
      }
    }
  }

  @Override
  public IProject getProject() {
    return project;
  }

  @Override
  public void setProject(IProject project) {
    this.project = project;
  }
}

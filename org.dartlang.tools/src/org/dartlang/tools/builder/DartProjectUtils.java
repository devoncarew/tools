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

import java.lang.reflect.InvocationTargetException;

import org.dartlang.tools.DartPlugin;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.ICommand;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.operation.IRunnableContext;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.ide.undo.CreateProjectOperation;
import org.eclipse.ui.ide.undo.WorkspaceUndoUtil;

public class DartProjectUtils {

  public static IProjectDescription createProjectDescription(IWorkspaceRoot root,
      String projectName, IPath projectLocation, boolean isImport) {
    final IProjectDescription description = root.getWorkspace().newProjectDescription(projectName);
    if (projectLocation != null && !root.getLocation().isPrefixOf(projectLocation) && !isImport) {
      IPath fullProjectLocation = projectLocation.append(projectName);
      description.setLocation(fullProjectLocation);
    } else if (isImport) {
      description.setLocation(projectLocation);
    }
    description.setNatureIds(new String[] {DartNature.NATURE_ID});
    ICommand command = description.newCommand();
    command.setBuilderName(DartBuilder.BUILDER_ID);
    description.setBuildSpec(new ICommand[] {command});
    return description;
  }

  public static IProject importDartProject(final Shell shell,
      final IRunnableContext runnableContext, String projectName, String projectLocation) {
    IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
    final IProjectDescription description = createProjectDescription(
        root,
        projectName,
        projectLocation != null ? new Path(projectLocation) : null,
        true);
    final IProject project = root.getProject(description.getName());

    // Create the new project operation.
    IRunnableWithProgress op = new IRunnableWithProgress() {
      @Override
      public void run(IProgressMonitor monitor) throws InvocationTargetException {
        CreateProjectOperation op = new CreateProjectOperation(description, "Creating project");
        try {
          op.execute(monitor, WorkspaceUndoUtil.getUIInfoAdapter(shell));
          project.setDefaultCharset("UTF-8", null);
          if (!DartNature.hasDartNature(project)) {
            if (probablyDartProject(project)) {
              DartNature.addNature(project);
            }
          }
        } catch (ExecutionException e) {
          throw new InvocationTargetException(e);
        } catch (CoreException e) {
          throw new InvocationTargetException(e);
        }
      }
    };

    // Run the new project creation operation.
    try {
      runnableContext.run(true, true, op);
    } catch (InterruptedException e) {
      return null;
    } catch (InvocationTargetException e) {
      Throwable t = e.getTargetException();
      if (t instanceof ExecutionException && t.getCause() instanceof CoreException) {
        CoreException cause = (CoreException) t.getCause();
        DartPlugin.showError("Error Importing Project", cause);
      } else {
        DartPlugin.showError("Error Importing Project", t);
      }
      return null;
    }

    return project;
  }

  public static boolean probablyDartProject(IProject project) {
    return project.getFile("pubspec.yaml").exists();
  }

  private DartProjectUtils() {

  }
}

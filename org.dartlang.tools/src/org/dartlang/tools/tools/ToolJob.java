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
package org.dartlang.tools.tools;

import java.io.IOException;

import org.dartlang.tools.DartPlugin;
import org.dartlang.tools.utils.ConsoleUI;
import org.dartlang.tools.utils.ToastUI;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

import com.google.common.io.ByteStreams;

// TODO: do we always want to pipe stdout and stderr to the console?
// If so, should we change how we run these tools? async streaming of I/O to the console, instead
// of printing the results at the end?

public abstract class ToolJob extends Job {
  public final IResource resource;
  public final String name;

  public boolean showStdoutOnSuccess = false;

  public ToolJob(IResource resource, String name) {
    super("Running " + name + "…");

    this.resource = resource;
    this.name = name;
  }

  public abstract Process createProcess() throws IOException;

  public IContainer getContainer() {
    if (resource instanceof IContainer) {
      return (IContainer) resource;
    } else {
      return resource.getParent();
    }
  }

  @Override
  protected IStatus run(IProgressMonitor monitor) {
    try {
      monitor.beginTask(getName(), IProgressMonitor.UNKNOWN);

      Process process = createProcess();

      // TODO: give the user the ability to cancel the job

      // TODO: read from the input streams so the process doesn't block
      int exitCode = process.waitFor();

      String stdout = new String(ByteStreams.toByteArray(process.getInputStream()));
      String stderr = new String(ByteStreams.toByteArray(process.getErrorStream()));

      if (exitCode == 0) {
        String message = "'" + name + "' ran successfully.";

        ToastUI.showToast(message);

        if (showStdoutOnSuccess) {
          ConsoleUI.stdout(message);
          if (!stdout.isEmpty()) {
            ConsoleUI.stdout(stdout);
          }
          ConsoleUI.stdout();
        }
      } else {
        String message = "'" + name + "' failed [" + exitCode + "].";

        ToastUI.showToast(message);

        ConsoleUI.stdout(message);
        if (!stdout.isEmpty()) {
          ConsoleUI.stdout(stdout);
        }
        if (!stderr.isEmpty()) {
          ConsoleUI.stderr(stderr);
        }
        ConsoleUI.stdout();
      }

      getContainer().refreshLocal(IResource.DEPTH_INFINITE, null);
    } catch (InterruptedException e) {
      return DartPlugin.createStatus(e);
    } catch (IOException e) {
      ConsoleUI.stdout("'" + name + "' failed.");
      ConsoleUI.stderr(e.getMessage());
      return Status.OK_STATUS;
    } catch (CoreException e) {
      return DartPlugin.createStatus(e);
    }

    return Status.OK_STATUS;
  }
}

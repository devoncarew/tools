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

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.List;
import java.util.concurrent.TimeUnit;

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

import com.google.common.base.Charsets;
import com.google.common.base.Joiner;
import com.google.common.util.concurrent.Uninterruptibles;

// TODO: Possible support a quite mode, for when a tool is not directly invoked by the user.

public abstract class ToolJob extends Job {

  public static class ToolResult {
    public final int exitCode;
    public final String stdout;
    public final String stderr;

    public ToolResult(int exitCode, String stdout, String stderr) {
      this.exitCode = exitCode;
      this.stdout = stdout;
      this.stderr = stderr;
    }

    public String getLastStdoutLine() {
      if (stdout == null || stdout.isEmpty()) {
        return "";
      } else {
        String[] lines = stdout.split("\n");
        return lines[lines.length - 1];
      }
    }
  }

  public final IResource resource;
  public final String name;

  private Process process;
  private StringBuilder stdoutBuilder = new StringBuilder();
  private StringBuilder stderrBuilder = new StringBuilder();

  public ToolJob(IResource resource, String name) {
    super("Running " + name + "...");

    this.resource = resource;
    this.name = name;
  }

  public abstract ProcessBuilder createProcessBuilder() throws IOException;

  protected String createToastMessage(ToolResult result) {
    if (result.exitCode == 0) {
      return "'" + name + "' ran successfully.";
    } else {
      return "'" + name + "' failed [" + result.exitCode + "].";
    }
  }

  public IContainer getContainer() {
    if (resource instanceof IContainer) {
      return (IContainer) resource;
    } else {
      return resource.getParent();
    }
  }

  protected void handleStderr(String str) {
    ConsoleUI.stderr(str);
  }

  protected void handleStdout(String str) {
    ConsoleUI.stdout(str);
  }

  private boolean isProcessRunning() {
    if (process == null) {
      return false;
    }
    try {
      process.exitValue();
      return false;
    } catch (Throwable t) {
      return true;
    }
  }

  private void monitorStdErr() {
    new Thread() {
      @Override
      public void run() {
        try {
          Reader reader = new InputStreamReader(process.getErrorStream(), Charsets.UTF_8);
          char[] buf = new char[1024];
          int len = reader.read(buf);
          while (len != -1) {
            stderrBuilder.append(buf, 0, len);
            handleStderr(new String(buf, 0, len));
            len = reader.read(buf);
          }
        } catch (IOException ioe) {
        }
      }
    }.start();
  }

  private void monitorStdout() {
    new Thread() {
      @Override
      public void run() {
        try {
          Reader reader = new InputStreamReader(process.getInputStream(), Charsets.UTF_8);
          char[] buf = new char[1024];
          int len = reader.read(buf);
          while (len != -1) {
            stdoutBuilder.append(buf, 0, len);
            handleStdout(new String(buf, 0, len));
            len = reader.read(buf);
          }
        } catch (IOException ioe) {
        }
      }
    }.start();
  }

  private void monitorUserCancel(final IProgressMonitor monitor) {
    new Thread() {
      @Override
      public void run() {
        while (isProcessRunning()) {
          if (monitor.isCanceled()) {
            process.destroy();
            break;
          }
          Uninterruptibles.sleepUninterruptibly(100, TimeUnit.MILLISECONDS);
        }
      }
    }.start();
  }

  protected void processFinished(ToolResult result) {
    if (result.exitCode != 0) {
      ConsoleUI.stdout("exited with code " + result.exitCode + "\n");
    }

    String toastMessage = createToastMessage(result);

    if (toastMessage != null) {
      ToastUI.showToast(toastMessage);
    }
  }

  protected void processStarting(ProcessBuilder processBuilder) {
    ConsoleUI.separator();

    StringBuilder buf = new StringBuilder();
    if (processBuilder.directory() != null) {
      buf.append(processBuilder.directory().getPath() + ": ");
    }

    List<String> commands = processBuilder.command();

    if (!commands.isEmpty()) {
      int index = 0;

      if (commands.get(0).equals("/bin/bash")) {
        index = 3;
      }

      String cmd = commands.get(index);
      int slashIndex = cmd.lastIndexOf(File.separatorChar);
      buf.append(slashIndex == -1 ? cmd : cmd.substring(slashIndex + 1));
      buf.append(Joiner.on(" ").join(commands.subList(index + 1, commands.size())));
    }

    buf.append("\n");

    ConsoleUI.stdout(buf.toString());
  }

  @Override
  protected IStatus run(IProgressMonitor monitor) {
    try {
      monitor.beginTask(getName(), IProgressMonitor.UNKNOWN);

      ProcessBuilder processBuilder = createProcessBuilder();

      processStarting(processBuilder);

      process = processBuilder.start();

      monitorUserCancel(monitor);
      monitorStdout();
      monitorStdErr();

      int exitCode = process.waitFor();

      Uninterruptibles.sleepUninterruptibly(20, TimeUnit.MILLISECONDS);

      //String stdout = new String(ByteStreams.toByteArray(process.getInputStream()));
      //String stderr = new String(ByteStreams.toByteArray(process.getErrorStream()));

      processFinished(new ToolResult(exitCode, stdoutBuilder.toString(), stderrBuilder.toString()));

      getContainer().refreshLocal(IResource.DEPTH_INFINITE, null);
    } catch (InterruptedException e) {
      return DartPlugin.createStatus(e);
    } catch (IOException e) {
      ConsoleUI.stdout("'" + name + "' failed.\n");
      ConsoleUI.stderr(e.getMessage() + "\n");
      return Status.OK_STATUS;
    } catch (CoreException e) {
      return DartPlugin.createStatus(e);
    }

    return Status.OK_STATUS;
  }
}

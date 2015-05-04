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

import org.dartlang.tools.utils.PlatformUIUtils;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.ui.progress.IProgressConstants;
import org.junit.runner.Description;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;

class TestRunListener extends RunListener {
  private static String cleanTrace(String trace) {
    StringBuilder builder = new StringBuilder();

    for (String line : trace.split("\n")) {
      line = line.trim();

      if (line.startsWith("at sun.reflect.NativeMethodAccessorImpl.invoke0(")
          || line.startsWith("at java.lang.reflect.Method.invoke(")) {
        break;
      }

      builder.append("  " + line + "\n");
    }

    return builder.toString();
  }

  final IProgressMonitor monitor;

  final boolean exitWhenFinished;

  public TestRunListener(IProgressMonitor monitor, boolean exitWhenFinished) {
    this.monitor = monitor;
    this.exitWhenFinished = exitWhenFinished;
  }

  @Override
  public void testFailure(Failure failure) throws Exception {
    System.out.println("   fail: " + failure);
  }

  @Override
  public void testFinished(Description description) throws Exception {
    monitor.worked(1);
  }

  @Override
  public void testRunFinished(Result result) throws Exception {
    monitor.done();

    if (!result.wasSuccessful()) {
      System.out.println();
      System.out.println(result.getFailureCount() + " failures:");

      for (Failure failure : result.getFailures()) {
        System.out.println(failure);
        System.out.println(cleanTrace(failure.getTrace()));
      }
    }

    System.out.println(result.getRunCount() + " tests run; " + result.getFailureCount()
        + " failures.");

    if (exitWhenFinished) {
      // We do a hard exit.
      System.exit(result.wasSuccessful() ? 0 : 1);
    }
  }

  @Override
  public void testRunStarted(Description description) throws Exception {
    monitor.beginTask("Running tests...", description.testCount());
  }

  @Override
  public void testStarted(Description description) throws Exception {
    System.out.println("running: " + description);

//    if (monitor.isCanceled()) {
//      description.pleaseStop();
//    }

    monitor.subTask(description.getDisplayName());
  }
}

public class TestRunner extends Job {

  public static void runTests(Class<?> clazz) {
    new TestRunner(clazz, false).schedule(2000);
  }

  public static void runTests(Class<?> clazz, boolean exitWhenFinished) {
    new TestRunner(clazz, exitWhenFinished).schedule(2000);
  }

  final boolean exitWhenFinished;

  final Class<?> clazz;

  public TestRunner(Class<?> clazz, boolean exitWhenFinished) {
    super("Running Tests...");

    this.clazz = clazz;
    this.exitWhenFinished = exitWhenFinished;
  }

  @Override
  protected IStatus run(IProgressMonitor monitor) {
    // Refresh the workspace; out-of-sync resources will cause problems.
    try {
      ResourcesPlugin.getWorkspace().getRoot().refreshLocal(
          IResource.DEPTH_INFINITE,
          new NullProgressMonitor());
    } catch (CoreException e) {
      TestPlugin.getPlugin().log(e);
    }

    PlatformUIUtils.showView(IProgressConstants.PROGRESS_VIEW_ID);

    JUnitCore junitCore = new JUnitCore();
    junitCore.addListener(new TestRunListener(monitor, exitWhenFinished));
    junitCore.run(clazz);

    return Status.OK_STATUS;
  }
}

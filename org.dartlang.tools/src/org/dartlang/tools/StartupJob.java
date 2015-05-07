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

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;

import org.dartlang.tools.utils.ConsoleUI;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

import com.google.common.base.Charsets;
import com.google.common.io.CharStreams;
import com.google.common.io.Closeables;

class StartupJob extends Job {

  StartupJob() {
    super("Starting " + DartPlugin.getToolName());

    setSystem(true);
  }

  @Override
  protected IStatus run(IProgressMonitor monitor) {
    // Check to see if we need to show the changelog.
    String lastVersion = DartPlugin.getPrefs().getString("lastVersion");
    String currentVersion = DartPlugin.getVersionString();

    DartPlugin.getPrefs().putValue("lastVersion", currentVersion);

    if (!lastVersion.isEmpty() && !currentVersion.equals(lastVersion)) {
      URL url = DartPlugin.getPlugin().getBundle().getEntry("changelog.md");
      try {
        Reader reader = new InputStreamReader(url.openStream(), Charsets.UTF_8);
        String contents = CharStreams.toString(reader);
        Closeables.closeQuietly(reader);

        ConsoleUI.separator();
        ConsoleUI.stdout(DartPlugin.getToolName() + "\n");
        ConsoleUI.stdout("Upgraded to " + currentVersion + ".\n\n");
        ConsoleUI.stdout(contents.trim() + "\n");
      } catch (IOException e) {
        DartPlugin.logError(e);
      }
    }

    return Status.OK_STATUS;
  }

}

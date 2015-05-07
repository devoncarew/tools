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
import org.dartlang.tools.sdk.DartSdk;
import org.dartlang.tools.sdk.DartSdkManager;
import org.eclipse.core.resources.IResource;

// TODO: On the mac, run the pub commands using bash.

public class Pub {
  public Pub() {

  }

  /**
   * Run pub get for either the given pubspec or the given project.
   * 
   * @param resource
   */
  public void runPubGet(IResource resource) {
    if (DartSdkManager.getManager().hasSdk()) {
      new PubJob(resource, "get").schedule();
    } else {
      DartPlugin.showError(
          "Error Running Pub",
          "No SDK is configured; please open the Dart preferences page.");
    }
  }

  public void runPubUpgrade(IResource resource) {
    if (DartSdkManager.getManager().hasSdk()) {
      new PubJob(resource, "upgrade").schedule();
    } else {
      DartPlugin.showError(
          "Error Running Pub",
          "No SDK is configured; please open the Dart preferences page.");
    }
  }
}

class PubJob extends ToolJob {
  public final String command;

  public PubJob(IResource resource, String command) {
    super(resource, "Pub " + command);

    this.command = command;
  }

  @Override
  public ProcessBuilder createProcessBuilder() throws IOException {
    DartSdk sdk = DartSdkManager.getManager().getSdk();

    ProcessBuilder builder;

    if (DartPlugin.isMac()) {
      builder = new ProcessBuilder("/bin/bash", "-l", "-c", sdk.getBinPath("pub") + " " + command);
    } else {
      builder = new ProcessBuilder(sdk.getBinPath("pub"), command);
    }

    builder.directory(getContainer().getLocation().toFile());

    return builder;
  }

  @Override
  protected String createToastMessage(ToolResult result) {
    if (result.exitCode == 0) {
      return name + ": " + result.getLastStdoutLine().toLowerCase();
    } else {
      return super.createToastMessage(result);
    }
  }
}

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
package org.dartlang.tools.sdk;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import org.dartlang.tools.DartPlugin;

public class DartSdk {
  final private File directory;
  final private String version;

  public DartSdk(File directory) {
    this.directory = directory;
    this.version = calcVersion();
  }

  private String calcVersion() {
    File versionFile = new File(directory, "version");

    if (versionFile.exists()) {
      try {
        BufferedReader reader = new BufferedReader(new FileReader(versionFile));
        String str = reader.readLine().trim();
        reader.close();
        return str;
      } catch (IOException ioe) {
        return "";
      }
    } else {
      return "";
    }
  }

  @Override
  public boolean equals(Object object) {
    if (!(object instanceof DartSdk)) {
      return false;
    }

    DartSdk other = (DartSdk) object;
    return other.getDirectory().equals(getDirectory()) && other.getVersion().equals(getVersion());
  }

  public String getBinPath(String binTool) {
    File binDir = new File(directory, "bin");

    if (DartPlugin.isWindows()) {
      return new File(binDir, binTool + ".bat").getAbsolutePath();
    } else {
      return new File(binDir, binTool).getAbsolutePath();
    }
  }

  public File getDirectory() {
    return directory;
  }

  public String getVersion() {
    return version;
  }

  @Override
  public int hashCode() {
    return directory.getPath().hashCode();
  }

  public boolean isValid() {
    if (!directory.exists()) {
      return false;
    }
    return new File(directory, "bin").exists() && new File(directory, "version").exists();
  }

  @Override
  public String toString() {
    return getVersion();
  }
}

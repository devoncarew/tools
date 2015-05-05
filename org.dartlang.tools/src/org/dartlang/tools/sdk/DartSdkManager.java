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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.dartlang.tools.DartPlugin;
import org.dartlang.tools.utils.ProcessUtils;
import org.eclipse.jface.preference.IPreferenceStore;

public class DartSdkManager {
  public interface Listener {
    void handleSdkChanged(DartSdk sdk);
  }

  private static String SDK_LOCATION = "sdkLocation";

  private static DartSdk _resolveSdkFromVm(String vmPath) {
    if (vmPath != null && !vmPath.isEmpty()) {
      File file = new File(vmPath);
      try {
        // Resolve any symbolic links.
        file = file.getCanonicalFile();
      } catch (IOException e) {

      }
      File sdkDir = file.getParentFile().getParentFile();
      DartSdk sdk = new DartSdk(sdkDir);
      if (sdk.isValid()) {
        return sdk;
      }
    }

    return null;
  }

  public static DartSdk locateSdk() {
    if (DartPlugin.isMac()) {
      // /bin/bash -c "which dart", /bin/bash -c "echo $PATH"
      String result = ProcessUtils.exec(new String[] {"/bin/bash", "-l", "-c", "which dart"});
      return _resolveSdkFromVm(result);
    } else if (DartPlugin.isWindows()) {
      // TODO: Also use the PATH var?
      String result = ProcessUtils.exec(new String[] {"where", "dart.exe"});

      if (result != null && !result.isEmpty()) {
        if (result.contains("\n")) {
          result = result.split("\n")[0].trim();
        }

        return _resolveSdkFromVm(result);
      }
    } else {
      String result = ProcessUtils.exec(new String[] {"which", "dart"});
      return _resolveSdkFromVm(result);
    }

    return null;
  }

  private List<Listener> listeners = new ArrayList<Listener>();

  private static DartSdkManager manager;

  public static DartSdkManager getManager() {
    if (manager == null) {
      manager = new DartSdkManager();

      if (!manager.hasSdk()) {
        manager.sdk = locateSdk();
      }
    }

    return manager;
  }

  private DartSdk sdk;

  public DartSdkManager() {
    IPreferenceStore prefs = DartPlugin.getPrefs();
    String location = prefs.getString(SDK_LOCATION);
    if (!location.isEmpty()) {
      DartSdk newSdk = new DartSdk(new File(location));
      if (newSdk.isValid()) {
        setSdk(newSdk);
      }
    }
  }

  public void addListener(Listener listener) {
    if (!listeners.contains(listener)) {
      listeners.add(listener);
    }
  }

  private void fireSdkChanged() {
    for (Listener listener : listeners) {
      listener.handleSdkChanged(getSdk());
    }
  }

  public DartSdk getSdk() {
    return sdk;
  }

  public boolean hasSdk() {
    return getSdk() != null;
  }

  /**
   * Check for a new SDK.
   */
  public boolean refresh() {
    if (!hasSdk()) {
      DartSdk newSdk = locateSdk();
      if (newSdk != null) {
        setSdk(newSdk);
        return true;
      }
    } else {
      // Check for a new version.
      DartSdk newSdk = new DartSdk(getSdk().getDirectory());
      if (!newSdk.equals(getSdk())) {
        setSdk(newSdk);
        return true;
      }
    }

    return false;
  }

  public void removeListener(Listener listener) {
    listeners.remove(listener);
  }

  public void setSdk(DartSdk newSdk) {
    if (newSdk == null) {
      IPreferenceStore prefs = DartPlugin.getPrefs();
      prefs.setToDefault(SDK_LOCATION);
    } else {
      IPreferenceStore prefs = DartPlugin.getPrefs();
      prefs.setValue(SDK_LOCATION, newSdk.getDirectory().getPath());
    }

    if (sdk == null && newSdk == null) {

    } else if (sdk == null && newSdk != null) {
      sdk = newSdk;
      fireSdkChanged();
    } else if (sdk != null && newSdk == null) {
      sdk = null;
      fireSdkChanged();
    } else {
      if (!sdk.equals(newSdk)) {
        sdk = newSdk;
        fireSdkChanged();
      }
    }
  }
}

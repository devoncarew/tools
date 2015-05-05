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

import java.util.HashMap;
import java.util.Map;

import org.dartlang.tools.utils.PlatformUtils;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

public class DartPlugin extends AbstractUIPlugin {
  public static final String PLUGIN_ID = "org.dartlang.tools";

  private static DartPlugin plugin;

  public static IStatus createStatus(Throwable e) {
    return new Status(IStatus.ERROR, PLUGIN_ID, e.toString(), e);
  }

  public static Image getImage(String imagePath) {
    return getPlugin().getPluginImage(imagePath);
  }

  public static ImageDescriptor getImageDescriptor(String path) {
    return imageDescriptorFromPlugin(PLUGIN_ID, "icons/" + path);
  }

  public static DartPlugin getPlugin() {
    return plugin;
  }

  public static IPreferenceStore getPrefs() {
    return getPlugin().getPreferenceStore();
  }

  public static String getVersionString() {
    return getPlugin().getBundle().getVersion().toString();
  }

  public static boolean isLinux() {
    return !isMac() && !isWindows();
  }

  public static boolean isMac() {
    return Platform.getOS().toLowerCase().startsWith("mac");
  }

  public static boolean isWindows() {
    return Platform.getOS().toLowerCase().startsWith("win");
  }

  public static void logError(Throwable e) {
    if (plugin != null) {
      plugin.getLog().log(createStatus(e));
    }
  }

  public static void showError(final String title, final String message) {
    Display.getDefault().asyncExec(new Runnable() {
      @Override
      public void run() {
        IStatus status = new Status(IStatus.ERROR, PLUGIN_ID, message);
        ErrorDialog.openError(PlatformUtils.getShell(), title, message, status);
      }
    });
  }

  public static void showError(final String message, final Throwable t) {
    Display.getDefault().asyncExec(new Runnable() {
      @Override
      public void run() {
        IStatus status = new Status(IStatus.ERROR, PLUGIN_ID, message, t);
        ErrorDialog.openError(PlatformUtils.getShell(), "Error", message, status);
      }
    });
  }

  private Map<String, Image> imageMap = new HashMap<String, Image>();

  public DartPlugin() {

  }

  private Image getPluginImage(String imagePath) {
    if (imageMap.get(imagePath) == null) {
      ImageDescriptor imageDescriptor = imageDescriptorFromPlugin(PLUGIN_ID, "icons/" + imagePath);

      if (imageDescriptor != null) {
        imageMap.put(imagePath, imageDescriptor.createImage());
      }
    }

    return imageMap.get(imagePath);
  }

  @Override
  public void start(BundleContext context) throws Exception {
    super.start(context);
    plugin = this;
  }

  @Override
  public void stop(BundleContext context) throws Exception {
    plugin = null;
    super.stop(context);
  }
}

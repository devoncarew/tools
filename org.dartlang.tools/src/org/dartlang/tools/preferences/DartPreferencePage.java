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
package org.dartlang.tools.preferences;

import java.io.File;
import java.util.concurrent.TimeUnit;

import org.dartlang.tools.DartPlugin;
import org.dartlang.tools.sdk.DartSdk;
import org.dartlang.tools.sdk.DartSdkManager;
import org.dartlang.tools.utils.PlatformUtils;
import org.dartlang.tools.utils.ToastUI;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import com.google.common.util.concurrent.Uninterruptibles;

// TODO: check for updates: https://devoncarew.github.io/tools/site.xml
// http://help.eclipse.org/indigo/index.jsp?topic=%2Forg.eclipse.platform.doc.isv%2Fguide%2Fp2_api_overview.htm

public class DartPreferencePage extends PreferencePage implements IWorkbenchPreferencePage {
  public static final String PAGE_ID = "org.dartlang.tools.preferences.DartPreferencePage";

  public static final String DOWNLOADS_URL = "https://www.dartlang.org/downloads/";
  private static final String DOWNLOADS_URL_DISPLAY = "www.dartlang.org/downloads";

  private Text sdkPathText;
  private Label versionLabel;

  public DartPreferencePage() {
    setPreferenceStore(DartPlugin.getPlugin().getPreferenceStore());
  }

  @Override
  protected Control createContents(Composite parent) {
    Composite composite = new Composite(parent, SWT.NONE);
    GridDataFactory.fillDefaults().grab(true, false).applyTo(composite);
    GridLayoutFactory.fillDefaults().spacing(0, 10).applyTo(composite);

    createToolsGroup(composite);
    createSdkGroup(composite);

    return composite;
  }

  protected void createSdkGroup(Composite composite) {
    Group group = new Group(composite, SWT.NONE);
    group.setText("Dart SDK");
    GridDataFactory.swtDefaults().grab(true, false).align(SWT.FILL, SWT.TOP).applyTo(group);
    GridLayoutFactory.fillDefaults().numColumns(3).margins(10, 4).applyTo(group);

    Label label = new Label(group, SWT.NONE);
    label.setText("Version:");
    versionLabel = new Label(group, SWT.NONE);
    GridDataFactory.fillDefaults().align(SWT.LEFT, SWT.CENTER).applyTo(versionLabel);
    Button refreshButton = new Button(group, SWT.PUSH);
    refreshButton.setText("Refresh");
    GridDataFactory.fillDefaults().align(SWT.FILL, SWT.CENTER).applyTo(refreshButton);
    refreshButton.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent e) {
        handleRefresh();
      }
    });

    label = new Label(group, SWT.NONE);
    label.setText("Location:");
    sdkPathText = new Text(group, SWT.SINGLE | SWT.BORDER | SWT.READ_ONLY);
    GridDataFactory.fillDefaults().align(SWT.FILL, SWT.CENTER).hint(100, -1).grab(true, false).applyTo(
        sdkPathText);
    Button browseButton = new Button(group, SWT.PUSH);
    browseButton.setText("Browse…");
    GridDataFactory.fillDefaults().align(SWT.FILL, SWT.CENTER).applyTo(browseButton);
    browseButton.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent e) {
        handleBrowse();
      }
    });

    Label separatorLabel = new Label(group, SWT.SEPARATOR | SWT.HORIZONTAL);
    GridDataFactory.fillDefaults().align(SWT.FILL, SWT.CENTER).span(3, 1).grab(true, false).applyTo(
        separatorLabel);

    Link link = new Link(group, SWT.NONE);
    link.setText("View install instructions at <a>" + DOWNLOADS_URL_DISPLAY + "</a>.");
    GridDataFactory.fillDefaults().align(SWT.CENTER, SWT.CENTER).span(3, 1).grab(true, false).applyTo(
        link);
    link.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent e) {
        PlatformUtils.openUrl(DOWNLOADS_URL);
      }
    });

    DartSdkManager manager = DartSdkManager.getManager();
    manager.addListener(new DartSdkManager.Listener() {
      @Override
      public void handleSdkChanged(final DartSdk sdk) {
        Display.getDefault().asyncExec(new Runnable() {
          @Override
          public void run() {
            update(sdk);
          }
        });
      }
    });
    update(manager.getSdk());
  }

  protected void createToolsGroup(Composite composite) {
    Group group = new Group(composite, SWT.NONE);
    group.setText("Dart Tools for Eclipse");
    GridDataFactory.swtDefaults().grab(true, false).align(SWT.FILL, SWT.TOP).applyTo(group);
    GridLayoutFactory.fillDefaults().numColumns(3).margins(10, 4).applyTo(group);

    Label label = new Label(group, SWT.NONE);
    label.setText("Version:");
    label = new Label(group, SWT.NONE);
    label.setText(DartPlugin.getVersionString());
    GridDataFactory.fillDefaults().align(SWT.LEFT, SWT.CENTER).span(2, 1).applyTo(label);
  }

  protected void handleBrowse() {
    // Get a path.
    DirectoryDialog dialog = new DirectoryDialog(getShell(), SWT.APPLICATION_MODAL | SWT.OPEN);
    dialog.setText("Select the SDK Directory");
    String path = dialog.open();
    if (path == null) {
      return;
    }

    // Validate that it's an sdk.
    DartSdk sdk = new DartSdk(new File(path));
    if (sdk.isValid()) {
      DartSdkManager.getManager().setSdk(sdk);
    } else {
      DartPlugin.showError("Error Setting SDK", "Invalid SDK directory: " + path);
    }
  }

  protected void handleRefresh() {
    new Job("Locating Dart SDK...") {
      @Override
      protected IStatus run(IProgressMonitor monitor) {
        Uninterruptibles.sleepUninterruptibly(500, TimeUnit.MILLISECONDS);
        boolean updated = DartSdkManager.getManager().refresh();
        DartSdk sdk = DartSdkManager.getManager().getSdk();
        if (sdk == null) {
          DartPlugin.showError("Unable to Locate SDK", "No SDK found.");
        } else if (updated) {
          ToastUI.showToast("Now using Dart SDK " + sdk.getVersion() + ".");
        } else {
          ToastUI.showToast("SDK unchanged.");
        }
        return Status.OK_STATUS;
      }
    }.schedule();
  }

  @Override
  public void init(IWorkbench workbench) {

  }

  @Override
  protected void performDefaults() {
    super.performDefaults();

    DartSdkManager manager = DartSdkManager.getManager();
    manager.setSdk(DartSdkManager.locateSdk());
    if (manager.hasSdk()) {
      ToastUI.showToast("Using Dart SDK " + manager.getSdk().getVersion() + ".");
    } else {
      ToastUI.showToast("No SDK found.");
    }
  }

  private void update(DartSdk sdk) {
    if (sdk == null) {
      sdkPathText.setText("");
      versionLabel.setText("no SDK installed");
    } else {
      sdkPathText.setText(sdk.getDirectory().getPath());
      versionLabel.setText(sdk.getVersion());
    }
  }
}

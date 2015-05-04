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
package org.dartlang.tools.wizards;

import org.dartlang.tools.DartPlugin;
import org.dartlang.tools.builder.DartProjectUtils;
import org.dartlang.tools.perspective.DartPerspectiveFactory;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IImportWizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.WorkbenchException;
import org.eclipse.ui.wizards.newresource.BasicNewResourceWizard;

public class ImportProjectWizard extends Wizard implements INewWizard, IImportWizard {
  private IWorkbench workbench;
  private ImportProjectWizardPage importPage;

  public ImportProjectWizard() {
    setWindowTitle("Import Dart Project");
    setNeedsProgressMonitor(true);
  }

  @Override
  public void addPages() {
    addPage(importPage = new ImportProjectWizardPage(this));
  }

  @Override
  public void init(IWorkbench workbench, IStructuredSelection selection) {
    this.workbench = workbench;
  }

  @Override
  public boolean performFinish() {
    IProject project = DartProjectUtils.importDartProject(
        getShell(),
        getContainer(),
        importPage.getProjectName(),
        importPage.getProjectLocation());

    if (project == null) {
      return false;
    }

    try {
      PlatformUI.getWorkbench().showPerspective(
          DartPerspectiveFactory.PERSPECTIVE_ID,
          PlatformUI.getWorkbench().getActiveWorkbenchWindow());
    } catch (WorkbenchException e) {
      DartPlugin.logError(e);
    }

    BasicNewResourceWizard.selectAndReveal(project, workbench.getActiveWorkbenchWindow());

    return true;
  }
}

class ImportProjectWizardPage extends WizardPage {
  private Text existingSourcePathText;
  private Button browseButton;
  private Text projectNameText;

  protected ImportProjectWizardPage(ImportProjectWizard wizard) {
    super("importProject");

    setTitle(wizard.getWindowTitle());
    setDescription("Create a new Dart project from an existing folder");
    setImageDescriptor(DartPlugin.getImageDescriptor("wizards/importdir_wiz.png"));
  }

  @Override
  public void createControl(Composite parent) {
    initializeDialogUnits(parent);
    Composite container = new Composite(parent, SWT.NULL);
    GridLayoutFactory.swtDefaults().spacing(5, 1).applyTo(container);
    GridLayout gridLayout = new GridLayout();
    gridLayout.numColumns = 3;
    gridLayout.marginWidth = 10;
    gridLayout.marginHeight = 10;
    container.setLayout(gridLayout);

    Label directoryLabel = new Label(container, SWT.NONE);
    directoryLabel.setText("Existing Source:");

    existingSourcePathText = new Text(container, SWT.BORDER);
    GridDataFactory.swtDefaults().align(SWT.FILL, SWT.CENTER).grab(true, false).applyTo(
        existingSourcePathText);
    existingSourcePathText.addModifyListener(new ModifyListener() {
      @Override
      public void modifyText(ModifyEvent e) {
        updatePage();
      }
    });

    browseButton = new Button(container, SWT.NONE);
    browseButton.setText("Browse...");
    browseButton.setEnabled(true);
    browseButton.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent e) {
        String selPath = new DirectoryDialog(getShell()).open();

        if (selPath != null) {
          existingSourcePathText.setText(selPath);

          IPath path = Path.fromOSString(selPath.trim());
          if (path.isValidPath(selPath.trim())) {
            projectNameText.setText(path.lastSegment());
            projectNameText.forceFocus();
            projectNameText.selectAll();
            updatePage();
          }
        }
      }
    });

    Label projectLabel = new Label(container, SWT.NONE);
    projectLabel.setText("Project Name:");
    projectNameText = new Text(container, SWT.BORDER);
    GridDataFactory.swtDefaults().align(SWT.FILL, SWT.CENTER).grab(true, false).applyTo(
        projectNameText);
    projectNameText.addModifyListener(new ModifyListener() {
      @Override
      public void modifyText(ModifyEvent e) {
        updatePage();
      }
    });

    // spacer
    new Label(container, SWT.NONE);

    setPageComplete(false);
    setControl(container);
  }

  public String getProjectLocation() {
    return existingSourcePathText.getText().trim();
  }

  public String getProjectName() {
    return projectNameText.getText().trim();
  }

  private void updatePage() {
    final IWorkspace workspace = ResourcesPlugin.getWorkspace();

    final String name = projectNameText.getText().trim();

    // check whether the project name field is empty
    if (name.length() == 0) {
      setErrorMessage(null);
      setMessage("Enter a project name");
      setPageComplete(false);
      return;
    }

    // check whether the project name is valid
    final IStatus nameStatus = workspace.validateName(name, IResource.PROJECT);
    if (!nameStatus.isOK()) {
      setErrorMessage(nameStatus.getMessage());
      setPageComplete(false);
      return;
    }

    // check whether project already exists
    final IProject handle = workspace.getRoot().getProject(name);
    if (handle.exists()) {
      setErrorMessage("A project with this name already exists");
      setPageComplete(false);
      return;
    }

    final String location = existingSourcePathText.getText().trim();

    // check whether location is empty
    if (location.length() == 0) {
      setErrorMessage(null);
      setMessage("Select a directory to import");
      setPageComplete(false);
      return;
    }

    // check whether the location is a syntactically correct path
    if (!Path.EMPTY.isValidPath(location)) {
      setErrorMessage("Invalid project contents directory");
      setPageComplete(false);
      return;
    }

    IPath projectPath = Path.fromOSString(location);

    // validate the location
    final IStatus locationStatus = workspace.validateProjectLocation(handle, projectPath);
    if (!locationStatus.isOK()) {
      setErrorMessage(locationStatus.getMessage());
      setPageComplete(false);
      return;
    }

    setPageComplete(true);

    setErrorMessage(null);
    setMessage(null);
  }
}

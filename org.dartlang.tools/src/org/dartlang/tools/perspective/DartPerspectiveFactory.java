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
package org.dartlang.tools.perspective;

import org.eclipse.debug.ui.IDebugUIConstants;
import org.eclipse.search.ui.NewSearchUI;
import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;
import org.eclipse.ui.console.IConsoleConstants;
import org.eclipse.ui.progress.IProgressConstants;

public class DartPerspectiveFactory implements IPerspectiveFactory {
  public static final String PERSPECTIVE_ID = "org.dartlang.tools.dartPerspective";

  @SuppressWarnings("deprecation")
  @Override
  public void createInitialLayout(IPageLayout layout) {
    String editorArea = layout.getEditorArea();

    IFolderLayout left = layout.createFolder("left", IPageLayout.LEFT, 0.25f, editorArea);

    left.addView(IPageLayout.ID_PROJECT_EXPLORER);
    left.addPlaceholder(IPageLayout.ID_RES_NAV);

    IFolderLayout outputfolder = layout.createFolder(
        "outputfolder",
        IPageLayout.BOTTOM,
        0.75f,
        editorArea);
    outputfolder.addView(IPageLayout.ID_PROBLEM_VIEW);
    outputfolder.addPlaceholder(NewSearchUI.SEARCH_VIEW_ID);
    outputfolder.addPlaceholder(IConsoleConstants.ID_CONSOLE_VIEW);
    outputfolder.addPlaceholder(IPageLayout.ID_TASK_LIST);
    outputfolder.addPlaceholder(IProgressConstants.PROGRESS_VIEW_ID);

    IFolderLayout outlinefolder = layout.createFolder(
        "outlinefolder",
        IPageLayout.RIGHT,
        0.74f,
        editorArea);
    outlinefolder.addView(IPageLayout.ID_OUTLINE);

    layout.addActionSet(IPageLayout.ID_NAVIGATE_ACTION_SET);
    layout.addActionSet(IDebugUIConstants.LAUNCH_ACTION_SET);

    layout.addShowViewShortcut(NewSearchUI.SEARCH_VIEW_ID);
    layout.addShowViewShortcut(IConsoleConstants.ID_CONSOLE_VIEW);
    layout.addShowViewShortcut(IPageLayout.ID_OUTLINE);
    layout.addShowViewShortcut(IPageLayout.ID_PROBLEM_VIEW);
    layout.addShowViewShortcut(IPageLayout.ID_PROGRESS_VIEW);

    layout.addNewWizardShortcut("org.eclipse.ui.wizards.new.folder");
    layout.addNewWizardShortcut("org.dartlang.tools.wizards.new.project");

    layout.addPerspectiveShortcut(IDebugUIConstants.ID_DEBUG_PERSPECTIVE);
  }
}

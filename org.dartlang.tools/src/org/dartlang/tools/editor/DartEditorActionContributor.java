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
package org.dartlang.tools.editor;

import org.dartlang.tools.editor.util.DartEditorMessages;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.editors.text.TextEditorActionContributor;
import org.eclipse.ui.texteditor.ITextEditor;
import org.eclipse.ui.texteditor.ITextEditorActionConstants;
import org.eclipse.ui.texteditor.ITextEditorActionDefinitionIds;
import org.eclipse.ui.texteditor.RetargetTextEditorAction;

public class DartEditorActionContributor extends TextEditorActionContributor {
  protected RetargetTextEditorAction contentAssistProposal;
  protected RetargetTextEditorAction contentAssistTip;

  public DartEditorActionContributor() {
    contentAssistProposal = new RetargetTextEditorAction(
        DartEditorMessages.getResourceBundle(),
        "ContentAssistProposal.");
    contentAssistProposal.setActionDefinitionId(ITextEditorActionDefinitionIds.CONTENT_ASSIST_PROPOSALS);
    contentAssistTip = new RetargetTextEditorAction(
        DartEditorMessages.getResourceBundle(),
        "ContentAssistTip.");
    contentAssistTip.setActionDefinitionId(ITextEditorActionDefinitionIds.CONTENT_ASSIST_CONTEXT_INFORMATION);
  }

  @Override
  public void dispose() {
    doSetActiveEditor(null);
    super.dispose();
  }

  private void doSetActiveEditor(IEditorPart part) {
    super.setActiveEditor(part);

    ITextEditor editor = null;
    if (part instanceof ITextEditor) {
      editor = (ITextEditor) part;
    }

    contentAssistProposal.setAction(getAction(editor, ITextEditorActionConstants.CONTENT_ASSIST));
    contentAssistTip.setAction(getAction(
        editor,
        ITextEditorActionConstants.CONTENT_ASSIST_CONTEXT_INFORMATION));
  }

  @Override
  public void init(IActionBars bars) {
    super.init(bars);

    IMenuManager menuManager = bars.getMenuManager();
    IMenuManager editMenu = menuManager.findMenuUsingPath(IWorkbenchActionConstants.M_EDIT);
    if (editMenu != null) {
      editMenu.add(new Separator());
      editMenu.add(contentAssistProposal);
      editMenu.add(contentAssistTip);
    }

    IToolBarManager toolBarManager = bars.getToolBarManager();
    if (toolBarManager != null) {
      toolBarManager.add(new Separator());
      //toolBarManager.add(fTogglePresentation);
    }
  }

  @Override
  public void setActiveEditor(IEditorPart part) {
    super.setActiveEditor(part);
    doSetActiveEditor(part);
  }
}

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

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.text.ITextViewerExtension5;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.IVerticalRuler;
import org.eclipse.jface.text.source.projection.ProjectionViewer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.editors.text.TextEditor;
import org.eclipse.ui.views.contentoutline.IContentOutlinePage;

public class DartEditor extends TextEditor {
  private DartEditorOutlinePage outlinePage;

  public DartEditor() {

  }

  @Override
  protected void adjustHighlightRange(int offset, int length) {
    ISourceViewer viewer = getSourceViewer();
    if (viewer instanceof ITextViewerExtension5) {
      ITextViewerExtension5 extension = (ITextViewerExtension5) viewer;
      extension.exposeModelRange(new Region(offset, length));
    }
  }

  @Override
  public void createPartControl(Composite parent) {
    super.createPartControl(parent);

//    ProjectionViewer viewer = (ProjectionViewer) getSourceViewer();
//    
//    fProjectionSupport = new ProjectionSupport(viewer, getAnnotationAccess(), getSharedColors());
//    fProjectionSupport.addSummarizableAnnotationType("org.eclipse.ui.workbench.texteditor.error");
//    fProjectionSupport.addSummarizableAnnotationType("org.eclipse.ui.workbench.texteditor.warning");
//    fProjectionSupport.install();
//    
//    viewer.doOperation(ProjectionViewer.TOGGLE);
  }

  @Override
  protected ISourceViewer createSourceViewer(Composite parent, IVerticalRuler ruler, int styles) {
    fAnnotationAccess = createAnnotationAccess();
    fOverviewRuler = createOverviewRuler(getSharedColors());

    ISourceViewer viewer = new ProjectionViewer(
        parent,
        ruler,
        getOverviewRuler(),
        isOverviewRulerVisible(),
        styles);

    // Ensure decoration support has been created and configured.
    getSourceViewerDecorationSupport(viewer);

    return viewer;
  }

  @Override
  public void dispose() {
    if (outlinePage != null) {
      outlinePage.setInput(null);
    }

    super.dispose();
  }

  @Override
  public void doSetInput(IEditorInput input) throws CoreException {
    super.doSetInput(input);

    if (outlinePage != null) {
      outlinePage.setInput(input);
    }
  }

  @Override
  protected void editorContextMenuAboutToShow(IMenuManager menu) {
    super.editorContextMenuAboutToShow(menu);

    addAction(menu, "ContentAssistProposal");
    addAction(menu, "ContentAssistTip");
  }

  @SuppressWarnings("rawtypes")
  @Override
  public Object getAdapter(Class required) {
    if (IContentOutlinePage.class.equals(required)) {
      if (outlinePage == null) {
        outlinePage = new DartEditorOutlinePage(getDocumentProvider(), this);
        if (getEditorInput() != null) {
          outlinePage.setInput(getEditorInput());
        }
      }
      return outlinePage;
    }

    return super.getAdapter(required);
  }

  @Override
  protected void initializeEditor() {
    super.initializeEditor();

    setSourceViewerConfiguration(new DartEditorSourceViewerConfiguration());
  }
}

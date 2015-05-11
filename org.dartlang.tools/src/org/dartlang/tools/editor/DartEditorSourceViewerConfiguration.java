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

import org.dartlang.tools.editor.dartdoc.DartCommentIndentStrategy;
import org.dartlang.tools.editor.dartdoc.DartDocScanner;
import org.dartlang.tools.editor.util.DartEditorColorProvider;
import org.eclipse.jface.text.DefaultIndentLineAutoEditStrategy;
import org.eclipse.jface.text.IAutoEditStrategy;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextDoubleClickStrategy;
import org.eclipse.jface.text.ITextHover;
import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.contentassist.ContentAssistant;
import org.eclipse.jface.text.contentassist.IContentAssistant;
import org.eclipse.jface.text.presentation.IPresentationReconciler;
import org.eclipse.jface.text.presentation.PresentationReconciler;
import org.eclipse.jface.text.rules.BufferedRuleBasedScanner;
import org.eclipse.jface.text.rules.DefaultDamagerRepairer;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.jface.text.source.IAnnotationHover;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.SourceViewerConfiguration;
import org.eclipse.swt.graphics.RGB;

public class DartEditorSourceViewerConfiguration extends SourceViewerConfiguration {
  private static class SingleTokenScanner extends BufferedRuleBasedScanner {
    public SingleTokenScanner(TextAttribute attribute) {
      setDefaultReturnToken(new Token(attribute));
    }
  }

  @Override
  public IAnnotationHover getAnnotationHover(ISourceViewer sourceViewer) {
    return new DartAnnotationHover();
  }

  @Override
  public IAutoEditStrategy[] getAutoEditStrategies(ISourceViewer sourceViewer, String contentType) {
    if (IDocument.DEFAULT_CONTENT_TYPE.equals(contentType)) {
      return new IAutoEditStrategy[] {new DartAutoIndentStrategy()};
    } else if (DartPartitionScanner.DART_DOC.equals(contentType)) {
      return new IAutoEditStrategy[] {new DartCommentIndentStrategy()};
    } else if (DartPartitionScanner.DART_COMMENT.equals(contentType)) {
      return new IAutoEditStrategy[] {new DartCommentIndentStrategy()};
    } else {
      return new IAutoEditStrategy[] {new DefaultIndentLineAutoEditStrategy()};
    }
  }

  @Override
  public String[] getConfiguredContentTypes(ISourceViewer sourceViewer) {
    return new String[] {
        IDocument.DEFAULT_CONTENT_TYPE, DartPartitionScanner.DART_DOC,
        DartPartitionScanner.DART_COMMENT};
  }

  @Override
  public String getConfiguredDocumentPartitioning(ISourceViewer sourceViewer) {
    return DartPartitionScanner.DART_PARTITIONING;
  }

  @Override
  public IContentAssistant getContentAssistant(ISourceViewer sourceViewer) {
    ContentAssistant assistant = new ContentAssistant();
    assistant.setDocumentPartitioning(getConfiguredDocumentPartitioning(sourceViewer));
    //assistant.setContentAssistProcessor(
    //    new DartCompletionProcessor(),
    //    IDocument.DEFAULT_CONTENT_TYPE);
    //assistant.setContentAssistProcessor(
    //    new DartDocCompletionProcessor(),
    //    DartPartitionScanner.DART_DOC);

    assistant.enableAutoActivation(true);
    assistant.setAutoActivationDelay(500);
    assistant.setProposalPopupOrientation(IContentAssistant.PROPOSAL_OVERLAY);
    assistant.setContextInformationPopupOrientation(IContentAssistant.CONTEXT_INFO_ABOVE);
    assistant.setContextInformationPopupBackground(DartEditorColorProvider.getColorProvider().getColor(
        new RGB(150, 150, 0)));

    return assistant;
  }

  @Override
  public ITextDoubleClickStrategy getDoubleClickStrategy(ISourceViewer sourceViewer,
      String contentType) {
    return new DartDoubleClickSelector();
  }

  @Override
  public String[] getIndentPrefixes(ISourceViewer sourceViewer, String contentType) {
    return new String[] {"  "};
  }

  @Override
  public IPresentationReconciler getPresentationReconciler(ISourceViewer sourceViewer) {
    DartEditorColorProvider colorProvider = DartEditorColorProvider.getColorProvider();
    PresentationReconciler reconciler = new PresentationReconciler();
    reconciler.setDocumentPartitioning(getConfiguredDocumentPartitioning(sourceViewer));

    DefaultDamagerRepairer dr = new DefaultDamagerRepairer(new DartCodeScanner(colorProvider));
    reconciler.setDamager(dr, IDocument.DEFAULT_CONTENT_TYPE);
    reconciler.setRepairer(dr, IDocument.DEFAULT_CONTENT_TYPE);

    dr = new DefaultDamagerRepairer(new DartDocScanner(colorProvider));
    reconciler.setDamager(dr, DartPartitionScanner.DART_DOC);
    reconciler.setRepairer(dr, DartPartitionScanner.DART_DOC);

    dr = new DefaultDamagerRepairer(new SingleTokenScanner(new TextAttribute(
        colorProvider.getColor(DartEditorColorProvider.COMMENT))));
    reconciler.setDamager(dr, DartPartitionScanner.DART_COMMENT);
    reconciler.setRepairer(dr, DartPartitionScanner.DART_COMMENT);

    return reconciler;
  }

  @Override
  public int getTabWidth(ISourceViewer sourceViewer) {
    return 2;
  }

  @Override
  public ITextHover getTextHover(ISourceViewer sourceViewer, String contentType) {
    // TODO:
    //return new DartTextHover();
    return null;
  }
}

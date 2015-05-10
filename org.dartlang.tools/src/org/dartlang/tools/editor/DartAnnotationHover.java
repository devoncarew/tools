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

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.source.IAnnotationHover;
import org.eclipse.jface.text.source.ISourceViewer;

public class DartAnnotationHover implements IAnnotationHover {
  @Override
  public String getHoverInfo(ISourceViewer sourceViewer, int lineNumber) {
    IDocument document = sourceViewer.getDocument();

    try {
      IRegion info = document.getLineInformation(lineNumber);
      return document.get(info.getOffset(), info.getLength());
    } catch (BadLocationException x) {
      return null;
    }
  }
}

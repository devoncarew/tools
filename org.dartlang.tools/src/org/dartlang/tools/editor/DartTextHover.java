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
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextHover;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.Region;
import org.eclipse.swt.graphics.Point;

public class DartTextHover implements ITextHover {
  @Deprecated
  @Override
  public String getHoverInfo(ITextViewer textViewer, IRegion hoverRegion) {
    if (hoverRegion != null) {
      try {
        if (hoverRegion.getLength() > -1) {
          return textViewer.getDocument().get(hoverRegion.getOffset(), hoverRegion.getLength());
        }
      } catch (BadLocationException x) {
      }
    }
    return "empty selection";
  }

  @Override
  public IRegion getHoverRegion(ITextViewer textViewer, int offset) {
    Point selection = textViewer.getSelectedRange();
    if (selection.x <= offset && offset < selection.x + selection.y) {
      return new Region(selection.x, selection.y);
    }
    return new Region(offset, 0);
  }
}

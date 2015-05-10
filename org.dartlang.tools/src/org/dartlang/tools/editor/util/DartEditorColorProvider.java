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
package org.dartlang.tools.editor.util;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;

/**
 * Manager for colors used in the Dart editor.
 */
public class DartEditorColorProvider {
  public static final RGB COMMENT = new RGB(65, 128, 96);
  public static final RGB KEYWORD = new RGB(0, 0, 128);
  public static final RGB TYPE = new RGB(0, 0, 128);
  public static final RGB STRING = new RGB(45, 36, 251);

  public static final RGB DEFAULT = new RGB(0, 0, 0);

  public static final RGB DARTDOC_DEFAULT = new RGB(65, 98, 188);
  public static final RGB DARTDOC_CODE = new RGB(128, 160, 190);

  protected Map<RGB, Color> colorTable = new HashMap<RGB, Color>();

  static private DartEditorColorProvider colorProvider;

  public static DartEditorColorProvider getColorProvider() {
    if (colorProvider == null) {
      colorProvider = new DartEditorColorProvider();
    }
    return colorProvider;
  }

  /**
   * Release all of the color resources.
   */
  public void dispose() {
    Iterator<Color> e = colorTable.values().iterator();
    while (e.hasNext()) {
      e.next().dispose();
    }
  }

  /**
   * Return the color that is stored in the color table under the given RGB value.
   * 
   * @param rgb the RGB value
   * @return the color stored in the color table for the given RGB value
   */
  public Color getColor(RGB rgb) {
    Color color = colorTable.get(rgb);
    if (color == null) {
      color = new Color(Display.getCurrent(), rgb);
      colorTable.put(rgb, color);
    }
    return color;
  }
}

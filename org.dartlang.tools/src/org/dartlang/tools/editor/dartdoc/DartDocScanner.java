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
package org.dartlang.tools.editor.dartdoc;

import java.util.ArrayList;
import java.util.List;

import org.dartlang.tools.editor.util.DartEditorColorProvider;
import org.dartlang.tools.editor.util.DartWhitespaceDetector;
import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.rules.EndOfLineRule;
import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.RuleBasedScanner;
import org.eclipse.jface.text.rules.SingleLineRule;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.jface.text.rules.WhitespaceRule;

public class DartDocScanner extends RuleBasedScanner {
  public DartDocScanner(DartEditorColorProvider provider) {
    setDefaultReturnToken(new Token(new TextAttribute(
        provider.getColor(DartEditorColorProvider.DARTDOC_DEFAULT))));

    IToken code = new Token(new TextAttribute(
        provider.getColor(DartEditorColorProvider.DARTDOC_CODE)));

    List<IRule> list = new ArrayList<IRule>();

    // Add rules for code.
    list.add(new SingleLineRule("`", "`", code));
    list.add(new SingleLineRule("[", "]", code));
    list.add(new EndOfLineRule("     ", code));

    // Add generic whitespace rule.
    list.add(new WhitespaceRule(new DartWhitespaceDetector()));

    IRule[] result = new IRule[list.size()];
    list.toArray(result);
    setRules(result);
  }
}

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

import java.util.ArrayList;
import java.util.List;

import org.dartlang.tools.editor.util.DartEditorColorProvider;
import org.dartlang.tools.editor.util.DartWhitespaceDetector;
import org.dartlang.tools.editor.util.DartWordDetector;
import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.rules.EndOfLineRule;
import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.MultiLineRule;
import org.eclipse.jface.text.rules.RuleBasedScanner;
import org.eclipse.jface.text.rules.SingleLineRule;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.jface.text.rules.WhitespaceRule;
import org.eclipse.jface.text.rules.WordRule;

public class DartCodeScanner extends RuleBasedScanner {
  private static String[] keywords = {
      "abstract", "assert", "async", "await", "break", "case", "catch", "class", "const",
      "continue", "default", "deferred", "do", "else", "enum", "export", "extends", "external",
      "factory", "final", "finally", "for", "get", "hide", "if", "implements", "import", "in",
      "is", "library", "native", "new", "of", "operator", "part", "rethrow", "return", "set",
      "show", "static", "super", "switch", "this", "throw", "try", "typedef", "while", "with"};

  private static String[] types = {
      "void", "bool", "num", "int", "double", "dynamic", "var", "String"};

  private static String[] constants = {"true", "false", "null"};

  public DartCodeScanner(DartEditorColorProvider provider) {
    IToken keyword = new Token(
        new TextAttribute(provider.getColor(DartEditorColorProvider.KEYWORD)));
    IToken type = new Token(new TextAttribute(provider.getColor(DartEditorColorProvider.TYPE)));
    IToken string = new Token(new TextAttribute(provider.getColor(DartEditorColorProvider.STRING)));
    IToken comment = new Token(
        new TextAttribute(provider.getColor(DartEditorColorProvider.COMMENT)));
    IToken other = new Token(new TextAttribute(provider.getColor(DartEditorColorProvider.DEFAULT)));

    List<IRule> rules = new ArrayList<IRule>();

    // TODO: annotations

    // Add rule for single line comments.
    rules.add(new EndOfLineRule("//", comment));

    // Add rule for strings and character constants.
    rules.add(new MultiLineRule("r'''", "'''", string, (char) 0, true));
    rules.add(new MultiLineRule("'''", "'''", string, '\\', true));
    rules.add(new MultiLineRule("r\"\"\"", "\"\"\"", string, (char) 0, true));
    rules.add(new MultiLineRule("\"\"\"", "\"\"\"", string, '\\', true));
    rules.add(new SingleLineRule("r\"", "\"", string));
    rules.add(new SingleLineRule("\"", "\"", string, '\\'));
    rules.add(new SingleLineRule("r'", "'", string));
    rules.add(new SingleLineRule("'", "'", string, '\\'));

    // Add generic whitespace rule.
    rules.add(new WhitespaceRule(new DartWhitespaceDetector()));

    // Add word rule for keywords, types, and constants.
    WordRule wordRule = new WordRule(new DartWordDetector(), other);
    for (int i = 0; i < keywords.length; i++) {
      wordRule.addWord(keywords[i], keyword);
    }
    for (int i = 0; i < types.length; i++) {
      wordRule.addWord(types[i], type);
    }
    for (int i = 0; i < constants.length; i++) {
      wordRule.addWord(constants[i], type);
    }
    rules.add(wordRule);

    IRule[] result = new IRule[rules.size()];
    rules.toArray(result);
    setRules(result);
  }
}

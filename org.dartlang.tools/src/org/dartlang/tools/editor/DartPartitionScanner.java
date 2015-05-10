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

import org.eclipse.jface.text.rules.EndOfLineRule;
import org.eclipse.jface.text.rules.ICharacterScanner;
import org.eclipse.jface.text.rules.IPredicateRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.IWordDetector;
import org.eclipse.jface.text.rules.MultiLineRule;
import org.eclipse.jface.text.rules.RuleBasedPartitionScanner;
import org.eclipse.jface.text.rules.SingleLineRule;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.jface.text.rules.WordRule;

/** This scanner recognizes DartDoc comments and Dart multi line comments. */
public class DartPartitionScanner extends RuleBasedPartitionScanner {

  /** Detector for empty comments. */
  static class EmptyCommentDetector implements IWordDetector {
    @Override
    public boolean isWordPart(char c) {
      return (c == '*' || c == '/');
    }

    @Override
    public boolean isWordStart(char c) {
      return (c == '/');
    }
  }

  static class WordPredicateRule extends WordRule implements IPredicateRule {
    private IToken successToken;

    public WordPredicateRule(IToken successToken) {
      super(new EmptyCommentDetector());
      this.successToken = successToken;
      addWord("/**/", successToken);
    }

    @Override
    public IToken evaluate(ICharacterScanner scanner, boolean resume) {
      return super.evaluate(scanner);
    }

    @Override
    public IToken getSuccessToken() {
      return successToken;
    }
  }

  private static DartPartitionScanner partitionScanner;

  public final static String DART_PARTITIONING = "__dart_partitioning";
  public final static String DART_COMMENT = "__dart_comment";
  public final static String DART_DOC = "__dart_dartdoc";

  public final static String[] DART_PARTITION_TYPES = new String[] {DART_COMMENT, DART_DOC};

  public static DartPartitionScanner getPartitionScanner() {
    if (partitionScanner == null) {
      partitionScanner = new DartPartitionScanner();
    }
    return partitionScanner;
  }

  /** Creates the partitioner and sets up the appropriate rules. */
  public DartPartitionScanner() {
    IToken dartDoc = new Token(DART_DOC);
    IToken comment = new Token(DART_COMMENT);

    List<IPredicateRule> rules = new ArrayList<IPredicateRule>();

    // Add rules for dartdoc.
    rules.add(new MultiLineRule("/**", "*/", dartDoc, (char) 0, true));
    rules.add(new EndOfLineRule("///", dartDoc));

    // Add rules for comments.
    rules.add(new MultiLineRule("/*", "*/", comment, (char) 0, true));
    rules.add(new EndOfLineRule("//", comment));

    // Add rule for strings.
    rules.add(new MultiLineRule("r'''", "'''", Token.UNDEFINED, (char) 0, true));
    rules.add(new MultiLineRule("'''", "'''", Token.UNDEFINED, '\\', true));
    rules.add(new MultiLineRule("r\"\"\"", "\"\"\"", Token.UNDEFINED, (char) 0, true));
    rules.add(new MultiLineRule("\"\"\"", "\"\"\"", Token.UNDEFINED, '\\', true));
    rules.add(new SingleLineRule("r\"", "\"", Token.UNDEFINED));
    rules.add(new SingleLineRule("\"", "\"", Token.UNDEFINED, '\\'));
    rules.add(new SingleLineRule("r'", "'", Token.UNDEFINED));
    rules.add(new SingleLineRule("'", "'", Token.UNDEFINED, '\\'));

    // Add special case word rule.
    rules.add(new WordPredicateRule(comment));

    IPredicateRule[] result = new IPredicateRule[rules.size()];
    rules.toArray(result);
    setPredicateRules(result);
  }
}

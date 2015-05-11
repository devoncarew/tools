// Foo bar.
// Foo bar foo bar foo bar foo bar foo bar foo bar.
// Foo bar.

import 'dart:async';
import 'dart:html';

/**
 * One two three. Here's `some code`.
 * Here's a [reference].
 *
 *     print('foo 1');
 *     print('foo 2');
 *
 * One two three.
 * One two three.
 */
void main(List<String> args) {
  /*print('some code here');
  print('some code here');*/

  print('some code here');

  //print('some code here');
  print('some code here');
}

/// Some more comments.
/// And a [reference] to something.
///
///     print('foo 1');
///     print('foo 2');
@AnAnnotation
void bar() {
  print('one two three');
  print("one two three");
  print(r'one two three\');
  print(r"one two three\");
  print('''
one two
three.
''';
  print(r'''
one two
three.
\''';
  print("""
one two
three.
""";
  print(r"""
one two
three.
\""";
}

// Copyright (c) 2025, the Dart project authors.  Please see the AUTHORS file
// for details. All rights reserved. Use of this source code is governed by a
// BSD-style license that can be found in the LICENSE file.

import 'dart:io';

import 'package:http/http.dart' as http;

void main() async {
  // A mime type to file extension map.
  final registry = <String, List<String>>{};

  print('Updating mime registry...');
  print('');

  const source =
      'https://svn.apache.org/repos/asf/httpd/httpd/trunk/docs/conf/mime.types';

  print('  $source');
  await downloadEntries(registry, source);

  // Post-process.
  registry['text/x-dart'] = ['dart'];
  registry['text/markdown'] = ['md'];

  // Sort and write out.
  final keys = registry.keys.toSet().toList();
  keys.sort();

  final registrySorted = {
    for (String key in keys) key: registry[key]!,
  };

  print('');
  print('${registrySorted.length} entries.');

  const file = 'lib/src/registry.g.dart';

  writeFile(file, registrySorted);

  print('Wrote to $file.');
}

void writeFile(String file, Map<String, List<String>> registry) {
  final buffer = StringBuffer();

  buffer.writeln('''
// Copyright (c) 2025, the Dart project authors.  Please see the AUTHORS file
// for details. All rights reserved. Use of this source code is governed by a
// BSD-style license that can be found in the LICENSE file.

// Do not edit - this is a generated file.

part of 'default_extension_map.dart';

const Map<String, List<String>> registryDefaults = {''');

  for (final key in registry.keys) {
    final value = registry[key]!;
    final valueStr = value.map((item) => "'$item'").join(', ');
    buffer.writeln("  '$key': [$valueStr],");
  }

  buffer.writeln('};');

  final out = File(file);
  out.writeAsStringSync('${buffer.toString().trim()}\n');

  // todo: dartfmt
  Process.runSync(Platform.resolvedExecutable, ['format', file]);
}

Future<void> downloadEntries(
    Map<String, List<String>> registery, String url) async {
  final response = await http.get(Uri.parse(url));

  // # application/odx
  // application/oebps-package+xml	opf
  // application/ogg					      ogx
  // application/omdoc+xml				  omdoc
  // application/onenote				    onetoc onetoc2 onetmp onepkg

  // Download the data and process it a bit.
  var lines = response.body.split('\n').map((line) => line.trim());
  lines = lines.where((line) => !line.startsWith('#') && line.isNotEmpty);
  final ws = RegExp('  +');
  lines = lines.map((line) => line.replaceAll('\t', ' ').replaceAll(ws, ' '));

  for (final line in lines) {
    final parts = line.split(' ');

    final mime = parts[0];
    final extensions = parts.sublist(1);

    registery[mime] = extensions;
  }
}

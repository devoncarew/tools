import 'package:mime/src/default_extension_map.dart';

void main() {
  const older = defaultExtensionMap;
  const newer = registryDefaults;

  final olderMimes = defaultExtensionMap.values.toSet();

  print('added:');

  for (final mime in newer.keys) {
    if (!olderMimes.contains(mime)) {
      print('  $mime');
    }
  }

  print('removed:');

  for (final mime in olderMimes) {
    if (!newer.containsKey(mime)) {
      print('  $mime');
    }
  }

  print('changed:');

  for (final ext in older.keys) {
    if (newExtToMime.containsKey(ext)) {
      final newValue = newExtToMime[ext];
      final oldValue = older[ext];

      if (newValue != oldValue) {
        print('  $ext: $oldValue => $newValue');
      }
    }
  }
}

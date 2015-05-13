
import 'package:grinder/grinder.dart';

// http://www.vogella.com/tutorials/EclipseTycho/article.html
// mvn org.eclipse.tycho:tycho-pomgenerator-plugin:generate-poms -DgroupId=org.dartlang.tools_feature

main(List<String> args) => grind(args);

@Task()
updateSite() {
	// Assume the site is built.

	// Copy the site to a temp dir.

	// Switch branches.

	// Copy the site to the root.

	// Delete the temp dir.

	// Switch back to the branch.

}

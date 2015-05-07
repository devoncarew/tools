# Building Dart Tools for Eclipse

We're using maven to build the Dart Tools for Eclipse. 

To generate the pom.xml files for the project, do:

```shell
$ mvn org.eclipse.tycho:tycho-pomgenerator-plugin:generate-poms -DgroupId=org.dartlang.tools_aggregator
```

Note, this command does not need to be run after initial pom creation..

## update pom files
mvn org.eclipse.tycho:tycho-versions-plugin:update-pom -Dtycho.mode=maven

## More info

For more info about building Eclipse plugins with Maven (and Tycho), see
[EclipseTycho/article.html](http://www.vogella.com/tutorials/EclipseTycho/article.html).

#Meta Data Compatibility Analysis

Analysis to detect potenially breaking changes in project dependency declarations and licenses.


### Prerequisites

This project has a dependency on the `commons` module that must be build first, and installed into the local repository. To do this, simply run `mvn install` in the root folder of `commons`.

### Change Issues Detected

I then look for the following change patterns:

LICENSE_ADDED  // note that there can be multiple licenses
LICENSE_REMOVED
DEPENDENCY_VERSION_CHANGED
DEPENDENCY_ADDED 
DEPENDENCY_REMOVED

### Type of Metadata Parsed

At the moment, only `pom.xml` is supported and parsed. In particular, *gradle*, *sbt* etc are not supported. To add support, the interface
`semverstudy.meta.MetaDataExtractor` must be implemented, and an instance added to `semverstudy.meta.CompatibilityAnalysis::EXTRACTORS`.

### Example

```~json
{
  "name" : "apache-commons-math",
  "version1" : "3.2",
  "version2" : "3.3",
  "issues" : [ {
    "key" : "DEPENDENCY_VERSION_CHANGED",
    "file" : "commons-math3-3.3-src/pom.xml",
    "line" : null,
    "method" : null,
    "direction" : "+1",
    "tool" : "metadata-analyser",
    "details" : "junit/junit-4.10 -> junit/junit-4.11"
  }
```

### Notes of Detecting Changed Dependencies

1. dependency scope is ignored
2. any change to version is detected as version change -- note here that version is actually a constraint, i.e. it is possible
that the constraint is changed, but still resolved to the same version 
3. if there are variables in version tags (like `${project.version}`), then this is always counted as change


### Building and Using

The tool can be build with `mvn package`, this will create a fat jar with all dependencies`target/projectmetadata-<version>-jar-with-dependencies.jar`.
This can then be executed as follows:

`java -jar target/projectmetadata-<version>-jar-with-dependencies.jar <projects.json> <output.json>`

The tool expects a log config `log4j.properties` file in the working dir, there is such a file in the project root folder.
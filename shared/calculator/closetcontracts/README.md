#Closet Contract Compatibility Analysis

This folder contains compatibility analysis between adjacent versions of projects using the Maven standard layout, using the AST-based analysis to extract contracts in code as discussed in [Dietrich, Jens, David J. Pearce, Kamil Jezek, and Premek Brada. "Contracts in the wild: A study of java programs" In Proc. ECOOP 2017](https://drops.dagstuhl.de/opus/volltexte/2017/7259/pdf/LIPIcs-ECOOP-2017-9.pdf). 


### Prerequisites

This project has a dependency on the `commons` module that must be build first, and installed into the local repository. To do this, simply run `mvn install` in the root folder of `commons`.

### Building and Using

The tool can be build with `mvn package`, this will create a fat jar with all dependencies`target/closetcontracts-<version>-jar-with-dependencies.jar`.
This can then be executed as follows:

`java -jar target/closetcontracts-<version>-jar-with-dependencies.jar <projects.json> <output.json>`

The tool expects a log config `log4j.properties` file in the working dir, there is such a file in the project root folder.
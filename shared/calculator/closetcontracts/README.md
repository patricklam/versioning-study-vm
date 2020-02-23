#Closet Contract Compatibility Analysis

This will run a compatibility analysis between adjacenmty versions of projects using the Maven standard layout, using the AST-based analysis to extract contracts in code as discussed in [Dietrich, Jens, David J. Pearce, Kamil Jezek, and Premek Brada. "Contracts in the wild: A study of java programs" In Proc. ECOOP 2017](https://drops.dagstuhl.de/opus/volltexte/2017/7259/pdf/LIPIcs-ECOOP-2017-9.pdf). 
The project can be built with Maven (`mvn package`),
the class to execute is `semverstudy.closetcontracts.CompatibilityAnalysis`, it expects a single parameter: the location of the json file containing the specs of the projects and project versions to be analysed.

This will produce .. TODO
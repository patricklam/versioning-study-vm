#Commons Module

The project contains some useful utilities that can be used by other diff analyses. To use it, do the following:

1. install this artifact into the local repo by running `mvn install`
2. add the following dependency to the pom of your module (project):
    ```$xml
    <dependency>
        <groupId>semverstudy</groupId>
        <artifactId>commons</artifactId>
        <version>1.1.0</version>
    </dependency>
    ```
3. note the version, if changes are made to this project, the version number in this pom should be changes, and the dependencies 
in the other projects must be updated 
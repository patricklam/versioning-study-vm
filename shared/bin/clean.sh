#!/bin/bash

if [ -z $1 ]; then
  echo Usage: $0 projects.json
  exit 1
fi

if [ "benchmarks" != $(basename $(pwd)) ]; then
  echo Must run from 'benchmarks' directory!
  exit 1
fi

INPUT=$1
echo Running 'gradlew clean' on gradlew projects...
for c in $(jq '.[] | select(.build == "gradlew").versions[].dir' < "$INPUT"); do
  ( cc=$(sed -e 's/^"//' -e 's/"$//' <<<"$c"); cd $cc; ./gradlew clean )
done

echo Running 'mvn clean' on mvn projects...
for c in $(jq '.[] | select(.build == "mvn").versions[].dir' < "$INPUT"); do
  ( cc=$(sed -e 's/^"//' -e 's/"$//' <<<"$c"); cd $cc; mvn clean )
done


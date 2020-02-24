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
for c in $(jq '.[] | select(.build == "gradlew").versions[].dir' < "$INPUT"); do
  ( cd $c; ./gradlew clean )
done

for c in $(jq '.[] | select(.build == "mvn").versions[].dir' < "$INPUT"); do
  ( cd $c; mvn clean )
done


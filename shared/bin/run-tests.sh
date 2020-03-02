#!/bin/bash

if [ -z $1 ]; then
  echo Usage: $0 projects.json
  exit 1
fi

if [ "benchmarks" != $(basename $(pwd)) ]; then
  echo Must run from 'benchmarks' directory!
  exit 1
fi

mkdir output

INPUT=$1
for c in $(jq '.[] | select(.build == "gradlew").versions[].dir' < "$INPUT"); do
  ( cc=$(sed -e 's/^"//' -e 's/"$//' <<<"$c"); cd $cc; ./gradlew test; echo $? > ../$(basename $(pwd))-test-result.out )
done

for c in $(jq '.[] | select(.build == "mvn").versions[].dir' < "$INPUT"); do
  ( cc=$(sed -e 's/^"//' -e 's/"$//' <<<"$c"); cd $cc; mvn test >& ../$(basename $(pwd))-test-output; echo $? > ../$(basename $(pwd))-test-error-code )
done


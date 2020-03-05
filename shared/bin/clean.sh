#!/bin/bash

if [ "benchmarks" != $(basename $(pwd)) ]; then
  echo "Must run from 'benchmarks' directory (inside the VM)!"
  exit 1
fi

if [ -z $1 ]; then
  echo Usage: $0 ../shared/projects.json
  exit 1
fi

if [ -n $2 ]; then
	TARGET=$2
fi

INPUT=$1
echo Running 'gradlew clean' on gradlew projects...
for c in $(jq '.[] | select(.build == "gradlew").versions[].dir' < "$INPUT"); do
	cc=$(sed -e 's/^"//' -e 's/"$//' <<<"$c")
	if [ -d $cc ] && [ -z $TARGET ] || [ $TARGET == $cc ]; then
    ( cd $cc; ./gradlew clean )
	fi
done

echo Running 'mvn clean' on mvn projects...
for c in $(jq '.[] | select(.build == "mvn").versions[].dir' < "$INPUT"); do
	cc=$(sed -e 's/^"//' -e 's/"$//' <<<"$c")
	if [ -d $cc ] && [ -z $TARGET ] || [ $TARGET == $cc ]; then
    ( cd $cc; mvn clean )
	fi
done


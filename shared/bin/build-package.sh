#!/bin/bash

if [ "benchmarks" != $(basename $(pwd)) ]; then
  echo "Must run from 'benchmarks' directory (inside the VM)!"
  exit 1
fi

if [[ -z $1 ]] || [[ $(basename $1) != "projects.json" ]]; then
  echo Usage: $0 ../shared/projects.json [benchmark]
  exit 1
fi

if [[ ! -z "$2" ]]; then
  TARGET=$2
fi

INPUT=$1
echo Running 'gradlew jar' on gradlew projects...
for c in $(jq '.[] | select(.build == "gradlew").versions[].dir' < "$INPUT"); do
  cc=$(sed -e 's/^"//' -e 's/"$//' <<<"$c")
  if [ -d $cc ]; then
     if [ -z $TARGET ] || [[ $cc == $TARGET* ]]; then
       ( cd $cc; ./gradlew jar )
     fi
  fi
done

echo Running 'mvn package' on mvn projects...
for c in $(jq '.[] | select(.build == "mvn").versions[].dir' < "$INPUT"); do
  cc=$(sed -e 's/^"//' -e 's/"$//' <<<"$c")
  if [ -d $cc ]; then
     if [ -z $TARGET ] || [[ $cc == $TARGET* ]]; then
       ( cd $cc; mvn package -Drat.skip=true )
     fi
  fi
done


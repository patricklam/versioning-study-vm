#!/bin/bash

if [ -z $1 ]; then
  echo "Usage: $0 <../shared/projects.json>"
  exit 1
fi

if [ "benchmarks" != $(basename $(pwd)) ]; then
  echo Must run from 'benchmarks' directory!
  exit 1
fi

INPUT=$1

process() {
  for c in $(jq '.[] | select(.build == "gradlew").versions[].dir' < "$INPUT"); do
    ( cc=$(sed -e 's/^"//' -e 's/"$//' <<<"$c"); cd $cc; ./gradlew clean; infer run -- ./gradlew build )
  done

  for c in $(jq '.[] | select(.build == "mvn").versions[].dir' < "$INPUT"); do
    ( cc=$(sed -e 's/^"//' -e 's/"$//' <<<"$c"); cd $cc; mvn clean; infer run -- mvn compile )
  done
}

process

mkdir -p output/fbinfer
for c in $(jq '.[].versions[].dir' < "$INPUT"); do
  cc=$(sed -e 's/^"//' -e 's/"$//' <<<"$c")
  cp -a $cc/infer-out output/fbinfer-$cc
done
# copy all output to a subdir of "output"

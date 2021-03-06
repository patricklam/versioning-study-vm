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

process() {
  mkdir -p output/fbinfer
  for c in $(jq '.[] | select((.build == "gradlew") and .skip != ["fbinfer"]).versions[].dir' < "$INPUT"); do
    cc=$(sed -e 's/^"//' -e 's/"$//' <<<"$c")
    if [ -d $cc ]; then
       if [ -z $TARGET ] || [[ $cc == $TARGET* ]]; then
           ( cd $cc;
           rm -rf infer-out;
	   ./gradlew clean;
	   infer run -- ./gradlew build;
           cp -a infer-out ../output/fbinfer-$cc )
       fi
    fi
  done

  for c in $(jq '.[] | select((.build == "mvn") and .skip != ["fbinfer"]).versions[].dir' < "$INPUT"); do
    cc=$(sed -e 's/^"//' -e 's/"$//' <<<"$c")
    if [ -d $cc ]; then
       if [ -z $TARGET ] || [[ $cc == $TARGET* ]]; then
           ( cd $cc;
           rm -rf infer-out;
           mvn clean;
           infer run -- mvn compile -Drat.skip=true;
           cp -a infer-out ../output/fbinfer-$cc
           );
       fi
    fi
  done
}

process

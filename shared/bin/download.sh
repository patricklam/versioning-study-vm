#!/bin/bash

if [ -z $1 ]; then
  echo Usage: $0 projects.json
  exit 1
fi

if [ "benchmarks" != $(basename $(pwd)) ]; then
  echo Must run from 'benchmarks' directory!
  exit 1
fi

# TODO: parameter 2 can be a specific benchmark to download
# TODO: delete the old version, only untar the files we download
INPUT=$1
for c in $(jq '.[].versions[].source' < "$INPUT"); do
  /usr/bin/wget -nc `sed -e 's/^"//' -e 's/"$//' <<<"$c"`
done

for tar in *.tgz *.tar.gz; do
  tar xzf $tar
done

for zip in *.zip; do
  unzip $zip
done


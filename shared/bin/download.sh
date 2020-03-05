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
for c in $(jq '.[].versions[].source' < "$INPUT"); do
  cc=$(sed -e 's/^"//' -e 's/"$//' <<<"$c")
  if [ -z $TARGET ] || [[ $cc == $TARGET* ]]; then
    echo downloading $cc...    
    rm $cc  
    /usr/bin/wget -nc $cc
    if [[ $cc == *.tar.gz ]] -o [[ $cc == *.tgz ]]; then
        tar xzf $cc
    fi
    if [[ $cc == *.zip ]]; then
        unzip $cc
    fi
  fi
done

# TODO apply patches as per json
# for p in ../shared/patches/*.patch; do
#  patch -p 1 < ~/shared/patches/slf4j-v_1.7.29.patch
# done

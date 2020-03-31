#! /bin/bash

REPO_PATH1=$1
REPO_PATH2=$2

VERSION_PATH=$3

CURRENT_DIR=`pwd`

for line in `cat $VERSION_PATH`;
do
    ver1=`echo $line | sed 's@:.*@@'`
    cd $REPO_PATH1
    git clean -d -f
    git checkout -f $ver1
    cd $CURRENT_DIR


    ver2=`echo $line | sed 's@.*:@@'`
    cd $REPO_PATH2
    git clean -d -f
    git checkout -f $ver2
    cd $CURRENT_DIR

    java -Xmx8G -jar feca4j.jar $REPO_PATH1 $REPO_PATH2 > output/$line.log 2>&1

done

#!/usr/bin/env bash

export WORKING_DIRECTORY=`pwd`
export HOME_DIRECTORY=`echo ~`
export VERSION="latest"

while getopts v: flag
do
    case "${flag}" in
        v) VERSION=${OPTARG};;
    esac
done

export IMAGE=`echo gebish/ci:$VERSION`

docker run -v ${WORKING_DIRECTORY}:${WORKING_DIRECTORY} -v ${HOME_DIRECTORY}/.gradle:/gradle-home -w ${WORKING_DIRECTORY} ${IMAGE} /bin/bash -c "Xvfb :99 -screen 1 1280x1024x16 -nolisten tcp -fbdir /var/run > /dev/null 2>&1 & export DISPLAY=:99 ; GRADLE_USER_HOME=\"/gradle-home\" ./gradlew --no-daemon --max-workers 4 --parallel $*"
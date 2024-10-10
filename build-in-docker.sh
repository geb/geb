#!/usr/bin/env bash

export WORKING_DIRECTORY=`pwd`
export HOME_DIRECTORY=`echo ~`
export IMAGE_REPOSITORY="registry.gitlab.com/poundex/geb-ci-docker-image"
export IMAGE_TAG="20241009185844"

while getopts v: flag
do
    case "${flag}" in
        v) VERSION=${OPTARG};;
    esac
done

export IMAGE="${IMAGE_REPOSITORY}:${IMAGE_TAG}"

docker run -v ${WORKING_DIRECTORY}:${WORKING_DIRECTORY} -v ${HOME_DIRECTORY}/.gradle:/gradle-home -w ${WORKING_DIRECTORY} ${IMAGE} /bin/bash -c "Xvfb :99 -screen 1 1280x1024x16 -nolisten tcp > /dev/null 2>&1 & export DISPLAY=:99 ; ./gradlew --no-daemon --max-workers 4 --parallel $*"
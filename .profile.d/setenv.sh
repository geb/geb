#!/bin/bash
echo "Setting environment varialbes..."
export JAVA_OPTS="-Dfile.encoding=UTF-8 -server -Xmx512m -XX:+UseCompressedOops -Dratpack.port=${PORT} -Dratpack.publicAddress=http://gebish.herokuapp.com"
export JAVA_HOME="${HOME}/.jdk7"
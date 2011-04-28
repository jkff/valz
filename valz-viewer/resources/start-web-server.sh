#!/bin/sh

CLASSPATH=$(find $(dirname $0) -name '*.jar' -printf '%p:')

java -cp "$CLASSPATH" org.valz.viewer.ValzWebServer

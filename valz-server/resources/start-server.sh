#!/bin/bash

CLASSPATH=$(find ../ -name '*.jar')
java -cp "$CLASSPATH" org.valz.server.ValzServer

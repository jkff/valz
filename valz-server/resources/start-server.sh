#!/bin/bash

CLASSPATH=$(find ../ -name '*-with-dependencies.jar')
java -cp "$CLASSPATH" org.valz.server.ValzServer

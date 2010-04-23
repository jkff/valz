#!/bin/bash

ps -e -o pid,cmd | grep java | while read line; do
    if [[ ${#line} -gt 500 ]] ; then
        pid=$(echo "$line" | cut -f1 -d' ')
        kill -s SIGKILL $pid
    fi
done

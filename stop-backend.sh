#!/bin/bash

SPRING_PID=$(ps -ef | grep "hack-assembler" | awk '{print $2}')
echo {SPRING_PID}
kill -9 ${SPRING_PID}

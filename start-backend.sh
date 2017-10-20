#!/bin/bash

#curl -I http://www.devopskb.net:8090/hi | grep 200
HTTP_STATUS=$(curl -s -o /dev/null -w "%{http_code}" http://www.devopskb.net:8090/hi)

if [[ "HTTP_STATUS" -ne "200" ]]; then
  echo "spring backend is not running"
  pkill -f hack-assembler
  echo "----------- STARTING SPRINGBOOT JAR APP ------------" >> spring.log
  echo "starting spring ..."
  java -jar target/hack-assembler-1.0.1.jar >> spring.log 2>&1 &
else
  echo "spring is ok"
fi

#!/bin/bash

FILE=$1
function usage {
  echo "usage:"
  echo "$ ./jackompile.sh {file|folder}"
  echo ""
  exit 99
}

# user must provide the file/dir to compile
if [[ -z "$FILE" ]]; then usage; fi

# run the compiler
java -cp target/jackompile.jar \
     net.kukinet.jack.compiler.JackCompiler $1



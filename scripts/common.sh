#!/bin/bash

# Logging
function me {
    basename $0
}

function timestamp {
  date +"%F %T"
}

function fail {
    echo -e "\033[0;31m[$(me) - $(timestamp)][FAIL] $*\033[0m"
    exit 1
}

function logInfo {
    echo -e "\033[0;32m[$(me) - $(timestamp)][INFO] $*\033[0m"
}

function logWarn {
    echo -e "\033[1;33m[$(me) - $(timestamp)][WARN] $*\033[0m"
}

function randomPort {
    echo -n $(( ( RANDOM % 60000 ) + 1024 ))
}
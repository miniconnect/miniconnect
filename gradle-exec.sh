#!/bin/sh

DIRECTORY="$( dirname "$0" )"

"$DIRECTORY/gradlew" "$1" -q --console=plain

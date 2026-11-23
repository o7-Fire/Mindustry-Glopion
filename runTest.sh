#!/usr/bin/env bash
set -euo pipefail

SERVER_JAR=$(cat mindustry-server-jar.path)

set +e
timeout 180s java -Dtest=1 -jar "$SERVER_JAR"
STATUS=$?
set -e

if [ "$STATUS" -eq 124 ]; then
  exit 0
fi

exit "$STATUS"

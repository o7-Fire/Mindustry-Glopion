#!/usr/bin/env bash
set -euo pipefail

VERSION=$(grep '^mindustryHash=' gradle.properties | cut -d= -f2)
BASE="https://github.com/o7-Fire/Static-Mindustry-Package/releases/download/$VERSION"
OUT="$PWD/static-$VERSION-server"
ZIP="$PWD/static-$VERSION-server.zip"

if [ ! -f "$OUT/server.jar" ]; then
  curl -fL --retry 3 -o "$ZIP" "$BASE/mindustry-server.zip"
  rm -rf "$OUT"
  mkdir -p "$OUT"
  unzip -q "$ZIP" -d "$OUT"
fi

echo "$OUT/server.jar" > mindustry-server-jar.path
echo "$OUT/server.jar"

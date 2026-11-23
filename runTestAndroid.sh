#!/usr/bin/env bash
set -euo pipefail

VERSION=$(grep '^mindustryHash=' gradle.properties | cut -d= -f2)
APK="mindustry-$VERSION.apk"
MOD_PATH="/storage/emulated/0/Android/data/io.anuken.mindustry/files/mods"
MOD_PATH_LEGACY="/sdcard/Android/data/io.anuken.mindustry/files/mods"

curl -fL --retry 3 -o "$APK" "https://github.com/o7-Fire/Static-Mindustry-Package/releases/download/$VERSION/mindustry-android.apk"
adb shell ls /system/bin
adb install -r "$APK"
adb shell mkdir -p "$MOD_PATH"
adb shell mkdir -p "$MOD_PATH_LEGACY"
adb push ./build/libs/Mindustry-Glopion-Dexed.jar "$MOD_PATH/Mindustry-Glopion-Dexed.jar"
adb push ./build/libs/Mindustry-Glopion-Dexed.jar "$MOD_PATH_LEGACY/Mindustry-Glopion-Dexed.jar"
adb shell ls "$MOD_PATH"
adb shell am start -n io.anuken.mindustry/mindustry.android.AndroidLauncher

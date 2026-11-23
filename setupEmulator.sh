#!/usr/bin/env bash
set -euo pipefail

API="$1"
FLAVOR="system-images;android-$API;default;x86"

echo "API: $API"
sudo chown -R "$USER:$USER" /usr/local/lib/android/sdk
yes | sdkmanager --licenses
sdkmanager --install "build-tools;30.0.3" platform-tools "platforms;android-$API"
sdkmanager --install emulator
sdkmanager --install "$FLAVOR"
echo "no" | avdmanager --verbose create avd --force --name generic --package "$FLAVOR" --tag default --abi x86
emulator @generic &

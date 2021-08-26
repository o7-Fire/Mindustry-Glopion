echo "API: $1"
yes | sdkmanager --licenses
flavor = "system-images;android-$1;default;x86_64"
echo $flavor
sdkmanager --install $flavor
echo "no" | avdmanager --verbose create avd --force --name "generic" --package $flavor --tag "default" --abi "x86_64"
emulator @generic &

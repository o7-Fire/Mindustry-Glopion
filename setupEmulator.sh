echo "API: $1"
sudo chown $USER:$USER /usr/local/lib/android/sdk -R
yes | sdkmanager --licenses
sdkmanager --install 'build-tools;30.0.3' platform-tools "platforms;android-$1"
flavor = "system-images;android-$1;default;x86"
echo $flavor
sdkmanager --install emulator
sdkmanager --install $flavor
echo "no" | avdmanager --verbose create avd --force --name "generic" --package $flavor --tag "default" --abi "x86"
emulator @generic &

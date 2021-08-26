echo "h"
wget https://github.com/o7-Fire/Static-Mindustry-Package/releases/download/v130.1/mindustry.apk
adb shell ls /system/bin
adb install mindustry.apk
adb push ./build/libs/Mindustry-Glopion-Dexed.jar /storage/emulated/0/Android/data/io.anuken.mindustry/files/mods/Mindustry-Glopion-Dexed.jar
adb shell ls /storage/emulated/0/Android/data/io.anuken.mindustry/files/mods
adb shell am start -n io.anuke.mindustry/mindustry.android.AndroidLauncher

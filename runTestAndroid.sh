echo "h"
wget https://github.com/o7-Fire/Static-Mindustry-Package/releases/download/v130.1/mindustry.apk
adb shell ls /system/bin
adb mindustry.apk
adb shell tree
adb shell ls /storage/emulated/0/Android/data


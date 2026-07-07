#!/data/data/com.termux/files/usr/bin/bash
set -euo pipefail
cd "$(dirname "$0")/.."
chmod +x ./gradlew || true
./gradlew clean assembleDebug assembleRelease --stacktrace --no-configuration-cache
mkdir -p output-apk
find app/build/outputs/apk -name "*.apk" -type f -print -exec cp {} output-apk/ \;
ls -lh output-apk

#!/usr/bin/env bash
set -euo pipefail

ROOT="$(cd "$(dirname "$0")" && pwd)"
SDK="$ROOT/android-sdk"
CMD_ZIP="$ROOT/.cache/commandlinetools-win.zip"
CMD_URL="https://dl.google.com/android/repository/commandlinetools-win-11076708_latest.zip"
export ANDROID_SDK_ROOT="$SDK"
export ANDROID_HOME="$SDK"
mkdir -p "$ROOT/.cache" "$SDK" "$ROOT/build" "$ROOT/dist"

if [ ! -f "$SDK/cmdline-tools/latest/bin/sdkmanager.bat" ]; then
  echo "Downloading Android command line tools..."
  curl -L --fail -o "$CMD_ZIP" "$CMD_URL"
  rm -rf "$SDK/cmdline-tools"
  mkdir -p "$SDK/cmdline-tools/latest"
  unzip -q "$CMD_ZIP" -d "$ROOT/.cache/cmdtools"
  mv "$ROOT/.cache/cmdtools/cmdline-tools"/* "$SDK/cmdline-tools/latest/"
fi

SDKMANAGER="$SDK/cmdline-tools/latest/bin/sdkmanager.bat"
echo "Installing/verifying Android SDK packages..."
yes | "$SDKMANAGER" --sdk_root="$SDK" --licenses >/dev/null || true
"$SDKMANAGER" --sdk_root="$SDK" "platforms;android-35" "build-tools;35.0.0" "platform-tools"

AAPT2="$SDK/build-tools/35.0.0/aapt2.exe"
D8="$SDK/build-tools/35.0.0/d8.bat"
ZIPALIGN="$SDK/build-tools/35.0.0/zipalign.exe"
APKSIGNER="$SDK/build-tools/35.0.0/apksigner.bat"
ANDROID_JAR="$SDK/platforms/android-35/android.jar"

rm -rf "$ROOT/build/classes" "$ROOT/build/dex" "$ROOT/build/compiled" "$ROOT/build/gen" "$ROOT/build/unsigned.apk" "$ROOT/build/aligned.apk"
mkdir -p "$ROOT/build/classes" "$ROOT/build/dex" "$ROOT/build/compiled" "$ROOT/build/gen" "$ROOT/dist"

echo "Compiling Android resources..."
"$AAPT2" compile --dir "$ROOT/app/src/main/res" -o "$ROOT/build/compiled"
"$AAPT2" link -I "$ANDROID_JAR" --manifest "$ROOT/app/src/main/AndroidManifest.xml" --java "$ROOT/build/gen" --min-sdk-version 26 --target-sdk-version 35 --version-code 1 --version-name 1.0 -o "$ROOT/build/unsigned.apk" "$ROOT"/build/compiled/*.flat

echo "Compiling Java sources..."
SRC_FILES=$(find "$ROOT/app/src/main/java" "$ROOT/build/gen" -name '*.java' -print)
javac -encoding UTF-8 --release 8 -cp "$ANDROID_JAR" -d "$ROOT/build/classes" $SRC_FILES

echo "Converting bytecode to DEX..."
CLASS_FILES=$(find "$ROOT/build/classes" -name '*.class' -print)
"$D8" --min-api 26 --lib "$ANDROID_JAR" --output "$ROOT/build/dex" $CLASS_FILES
APK_WIN=$(cygpath -w "$ROOT/build/unsigned.apk")
DEX_WIN=$(cygpath -w "$ROOT/build/dex/classes.dex")
python - <<PY
import zipfile
apk = r"$APK_WIN"
cls = r"$DEX_WIN"
with zipfile.ZipFile(apk, 'a', compression=zipfile.ZIP_STORED) as z:
    z.write(cls, 'classes.dex')
PY

echo "Aligning APK..."
"$ZIPALIGN" -p -f 4 "$ROOT/build/unsigned.apk" "$ROOT/build/aligned.apk"

KEYSTORE="$ROOT/debug.keystore"
if [ ! -f "$KEYSTORE" ]; then
  echo "Creating local debug signing key..."
  keytool -genkeypair -v -keystore "$KEYSTORE" -storepass android -keypass android -alias androiddebugkey -keyalg RSA -keysize 2048 -validity 10000 -dname "CN=Android Debug,O=AptitudeTrainer,C=IN"
fi

echo "Signing APK..."
"$APKSIGNER" sign --ks "$KEYSTORE" --ks-pass pass:android --key-pass pass:android --out "$ROOT/dist/AptitudeTrainer-debug.apk" "$ROOT/build/aligned.apk"
"$APKSIGNER" verify --verbose "$ROOT/dist/AptitudeTrainer-debug.apk"

echo "SUCCESS: $ROOT/dist/AptitudeTrainer-debug.apk"

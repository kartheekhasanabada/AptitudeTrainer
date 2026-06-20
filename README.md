# Aptitude Trainer

A native Android aptitude-practice app for students preparing for company interview aptitude rounds.

## Download for Android users

After this project is uploaded to GitHub, Android users can download the app from the **Releases** page:

1. Open the GitHub repository.
2. Click **Releases** on the right side of the repo page.
3. Open the latest version, for example `v1.0.0`.
4. Download **`AptitudeTrainer.apk`** from **Assets**.
5. On Android, open the downloaded APK.
6. If Android asks, enable **Install unknown apps** for your browser or file manager.
7. Tap **Install**.

> This APK is for direct sideload installation. For Google Play Store publishing, use a release-signed Android App Bundle (`.aab`) with a private release keystore.

## Features

- Apple-glass/Hermes-inspired UI with soft gradients, translucent cards, rounded controls, and a polished dashboard.
- Student dashboard showing completed tests, average score, best score, day streak, last test result, solved question count, and interrupted attempts.
- Daily fixed-time aptitude test scheduling.
- Scheduled test opens using exact alarms and a full-screen notification.
- Easy, Medium, and Hard question levels.
- Student can choose 5 or 10 questions.
- Multiple-choice questions only.
- Hints for every question.
- Final score with correct answers and explanations.
- Questions shuffle every attempt.
- Back button disabled during the test.
- Screenshot/screen-recording protection during test.
- Best-effort screen pinning / lock-task mode to discourage closing the test.
- If the student leaves/closes the app before finishing, the attempt is stopped.
- Boot receiver re-schedules the daily test after phone restart.

## Security-conscious defaults

- No `INTERNET` permission.
- `android:usesCleartextTraffic="false"`.
- `android:allowBackup="false"`.
- Private app preferences for schedule/settings.
- `FLAG_SECURE` during tests to block screenshots and screen recordings.

## Important Android limitation

A normal Android app cannot fully force itself to stay open or prevent closing on every Android device. Absolute kiosk/exam lockdown requires the app to be installed as a **device-owner / kiosk app** by an administrator. This app uses the strongest normal-app approach: exact alarms, full-screen notification, wake lock, screen pinning request, screenshot blocking, and interruption tracking.

## Build locally on Windows

This repo includes a local builder script that downloads Android command-line tools if needed and creates a debug APK.

```bash
bash build-apk.sh
```

Output:

```text
dist/AptitudeTrainer-debug.apk
```

Install with ADB:

```bash
android-sdk/platform-tools/adb.exe install -r dist/AptitudeTrainer-debug.apk
```

## Automatic GitHub APK build

This repo includes a GitHub Actions workflow at:

```text
.github/workflows/android-build.yml
```

It builds the APK automatically on every push and pull request. It also publishes `AptitudeTrainer.apk` as a GitHub Release asset when you push a version tag like `v1.0.0`.

### Create a public downloadable release

After pushing the repo to GitHub, run:

```bash
git tag v1.0.0
git push origin v1.0.0
```

GitHub Actions will build the app and attach `AptitudeTrainer.apk` to the release. Then any Android user can download it from the repo's **Releases** page.

## Project structure

```text
app/src/main/AndroidManifest.xml
app/src/main/java/com/karth/aptitudetrainer/
  MainActivity.java
  TestActivity.java
  Scheduler.java
  AlarmReceiver.java
  BootReceiver.java
  Question.java
  QuestionBank.java
.github/workflows/android-build.yml
build-apk.sh
```

# GitHub Upload and Public APK Release Guide

Follow these steps to make the app downloadable by any Android user.

## Option A: Create repo from GitHub website

1. Go to https://github.com/new
2. Repository name suggestion: `AptitudeTrainer`
3. Choose **Public**.
4. Do **not** add README/gitignore/license on GitHub because this project already has them.
5. Create the repository.
6. On your PC, open Git Bash in this folder:

```bash
cd /c/Users/karth/OneDrive/Desktop/Aptitude
git init
git add .
git commit -m "Initial Android aptitude trainer app"
git branch -M main
git remote add origin https://github.com/YOUR_USERNAME/AptitudeTrainer.git
git push -u origin main
```

Replace `YOUR_USERNAME` with your GitHub username.

## Option B: Create repo using GitHub CLI

If `gh` is installed and logged in:

```bash
cd /c/Users/karth/OneDrive/Desktop/Aptitude
git init
git add .
git commit -m "Initial Android aptitude trainer app"
gh repo create AptitudeTrainer --public --source . --remote origin --push
```

## Create the APK download release

After pushing the repo, create a version tag:

```bash
git tag v1.0.0
git push origin v1.0.0
```

Then open your GitHub repo → **Actions**. Wait for **Build Android APK** to finish.

After it finishes, open your GitHub repo → **Releases**. You will see a release named `Aptitude Trainer v1.0.0` with this file:

```text
AptitudeTrainer.apk
```

Share the Releases page link with Android users. They can download and install the APK.

## If users get an Android warning

Because this is not installed from Play Store, Android may show an unknown-app warning. Tell users:

1. Tap the downloaded APK.
2. If blocked, tap **Settings**.
3. Enable **Allow from this source** for the browser/file manager.
4. Go back and tap **Install**.

## Update app later

After changes, create a new tag:

```bash
git add .
git commit -m "Update app"
git push
git tag v1.0.1
git push origin v1.0.1
```

GitHub will automatically build and publish the new APK release.

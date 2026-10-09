<div align="center">
<img width="1200" height="475" alt="GHBanner" src="https://ai.google.dev/static/site-assets/images/share-ais-513315318.png" />
</div>

# Run and deploy your AI Studio app

This contains everything you need to run your app locally.

View your app in AI Studio: https://ai.studio/apps/7e1a19ab-8bda-44fa-b478-7fe37384c6fc

## Run Locally

**Prerequisites:**  [Android Studio](https://developer.android.com/studio)


1. Open Android Studio
2. Select **Open** and choose the directory containing this project
3. Allow Android Studio to fix any incompatibilities as it imports the project.
4. Create a file named `.env` in the project directory and set `GEMINI_API_KEY` in that file to your Gemini API key (see `.env.example` for an example)
5. Remove this line from the app's `build.gradle.kts` file: `signingConfig = signingConfigs.getByName("debugConfig")`
6. Run the app on an emulator or physical device
7. If you have already published your app in AI Studio, please [request upload key reset](https://support.google.com/googleplay/android-developer/answer/9842756#zippy=%2Crequest-an-upload-key-reset) in Google Play Console.

## Google Sign-In Setup

For local debug builds, add this SHA-1 certificate to Firebase Console under Project settings > Your apps > Android app > Add fingerprint:

`39:28:B2:37:A2:0A:E2:C9:80:CA:A5:74:83:26:12:30:9A:17:B3:38`

Also enable Google in Authentication > Sign-in method. Download the updated `google-services.json` after changing fingerprints and place it in `app/`.

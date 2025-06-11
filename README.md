# Road Signs Recognition

This repository contains a minimal Android application written in Kotlin that uses CameraX and TensorFlow Lite to perform real time road sign recognition. The sample project currently depends on TensorFlow Lite 2.17 because the `2.18.0` artifacts are not yet published to Maven Central. When those artifacts become available you can update the dependency version in `app/build.gradle`.

## Building

The project uses Gradle. To build or run the application you need the Android SDK installed. Typical build command:

```bash
gradle assembleDebug
```

## Notes

The TensorFlow Lite model must accept 416x416 pixel images. The app loads labels from `labels.txt` in the assets folder and displays the top prediction with its confidence percentage.

# Road Signs Recognition

This repository contains a minimal Android application written in Kotlin that uses CameraX and TensorFlow Lite 2.18 to perform real time road sign recognition. The application opens the camera, analyzes preview frames and runs them through a TensorFlow Lite model. A mock `model.tflite` file is provided in the assets folder and should be replaced with a proper trained model.

## Building

The project uses Gradle. To build or run the application you need the Android SDK installed. Typical build command:

```bash
gradle assembleDebug
```

## Notes

The TensorFlow Lite model must accept 416x416 pixel images. The current implementation simply displays the output score as a percentage.

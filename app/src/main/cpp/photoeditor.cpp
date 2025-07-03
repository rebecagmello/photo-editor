#include <jni.h>
#include <android/bitmap.h>
#include <cstdint>

#define CLAMP(x) (x < 0 ? 0 : (x > 255 ? 255 : x))

extern "C" {

// Filtro 1: NEGATIVO
JNIEXPORT void JNICALL
Java_com_example_photoeditor_FilterFragment_applyNegative(
        JNIEnv *env, jobject, jobject bitmap) {

    AndroidBitmapInfo info;
    void *pixels;

    if (AndroidBitmap_getInfo(env, bitmap, &info) != ANDROID_BITMAP_RESULT_SUCCESS) return;
    if (AndroidBitmap_lockPixels(env, bitmap, &pixels) != ANDROID_BITMAP_RESULT_SUCCESS) return;

    for (int y = 0; y < info.height; y++) {
        uint32_t *line = (uint32_t *) ((char *) pixels + y * info.stride);
        for (int x = 0; x < info.width; x++) {
            uint32_t pixel = line[x];
            uint8_t a = (pixel >> 24) & 0xff;
            uint8_t r = 255 - ((pixel >> 16) & 0xff);
            uint8_t g = 255 - ((pixel >> 8) & 0xff);
            uint8_t b = 255 - (pixel & 0xff);
            line[x] = (a << 24) | (r << 16) | (g << 8) | b;
        }
    }

    AndroidBitmap_unlockPixels(env, bitmap);
}

// Filtro 2: SEPIA
JNIEXPORT void JNICALL
Java_com_example_photoeditor_FilterFragment_applySepia(
        JNIEnv *env, jobject, jobject bitmap) {

    AndroidBitmapInfo info;
    void *pixels;

    if (AndroidBitmap_getInfo(env, bitmap, &info) != ANDROID_BITMAP_RESULT_SUCCESS) return;
    if (AndroidBitmap_lockPixels(env, bitmap, &pixels) != ANDROID_BITMAP_RESULT_SUCCESS) return;

    for (int y = 0; y < info.height; y++) {
        uint32_t *line = (uint32_t *) ((char *) pixels + y * info.stride);
        for (int x = 0; x < info.width; x++) {
            uint32_t pixel = line[x];
            uint8_t a = (pixel >> 24) & 0xff;
            uint8_t r = (pixel >> 16) & 0xff;
            uint8_t g = (pixel >> 8) & 0xff;
            uint8_t b = pixel & 0xff;

            int tr = CLAMP((int) (0.393 * r + 0.769 * g + 0.189 * b));
            int tg = CLAMP((int) (0.349 * r + 0.686 * g + 0.168 * b));
            int tb = CLAMP((int) (0.272 * r + 0.534 * g + 0.131 * b));

            line[x] = (a << 24) | (tr << 16) | (tg << 8) | tb;
        }
    }

    AndroidBitmap_unlockPixels(env, bitmap);
}

// Filtro 3: ESCALA DE CINZA
JNIEXPORT void JNICALL
Java_com_example_photoeditor_FilterFragment_applyGrayscale(
        JNIEnv *env, jobject, jobject bitmap) {

    AndroidBitmapInfo info;
    void *pixels;

    if (AndroidBitmap_getInfo(env, bitmap, &info) != ANDROID_BITMAP_RESULT_SUCCESS) return;
    if (AndroidBitmap_lockPixels(env, bitmap, &pixels) != ANDROID_BITMAP_RESULT_SUCCESS) return;

    for (int y = 0; y < info.height; y++) {
        uint32_t *line = (uint32_t *) ((char *) pixels + y * info.stride);
        for (int x = 0; x < info.width; x++) {
            uint32_t pixel = line[x];
            uint8_t a = (pixel >> 24) & 0xff;
            uint8_t r = (pixel >> 16) & 0xff;
            uint8_t g = (pixel >> 8) & 0xff;
            uint8_t b = pixel & 0xff;

            uint8_t gray = (r + g + b) / 3;

            line[x] = (a << 24) | (gray << 16) | (gray << 8) | gray;
        }
    }

    AndroidBitmap_unlockPixels(env, bitmap);
}

// Filtro 4: AUMENTO DE BRILHO
JNIEXPORT void JNICALL
Java_com_example_photoeditor_NativeImageFilters_applyBrightness(
        JNIEnv *env, jobject, jobject bitmap, jint value) {

    AndroidBitmapInfo info;
    void *pixels;

    if (AndroidBitmap_getInfo(env, bitmap, &info) != ANDROID_BITMAP_RESULT_SUCCESS) return;
    if (AndroidBitmap_lockPixels(env, bitmap, &pixels) != ANDROID_BITMAP_RESULT_SUCCESS) return;

    for (int y = 0; y < info.height; y++) {
        uint32_t *line = (uint32_t *) ((char *) pixels + y * info.stride);
        for (int x = 0; x < info.width; x++) {
            uint32_t pixel = line[x];
            uint8_t a = (pixel >> 24) & 0xff;
            uint8_t r = CLAMP(((pixel >> 16) & 0xff) + value);
            uint8_t g = CLAMP(((pixel >> 8) & 0xff) + value);
            uint8_t b = CLAMP((pixel & 0xff) + value);
            line[x] = (a << 24) | (r << 16) | (g << 8) | b;
        }
    }

    AndroidBitmap_unlockPixels(env, bitmap);
}
}

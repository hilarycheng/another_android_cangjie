#include <string.h>
#include <jni.h>
#include <android/log.h>

#define  LOG_TAG    "Cangjie"
#define  LOGE(...)  __android_log_print(ANDROID_LOG_ERROR,LOG_TAG,__VA_ARGS__)

char value[] = "你";

jchar Java_com_diycircuits_cangjie_TableLoader_getChar(JNIEnv* env, jobject thiz)
{
  LOGE("Char Length : %d", sizeof(jchar));
  LOGE("Char Value : %d %02X %02X %02X\n", strlen(value), value[0] & 0x00FF, value[1] & 0x00FF, value[2] & 0x00FF);
  return 0;
}

jchar Java_com_diycircuits_cangjie_TableLoader_passCharArray(JNIEnv* env, jobject thiz, jcharArray arr)
{
  jchar *buf = (*env)->GetCharArrayElements(env, arr, NULL);
  jsize len = (*env)->GetArrayLength(env, arr);

  LOGE("Array Length : %d", len);

  (*env)->ReleaseCharArrayElements(env, arr, buf, 0);

  return 0;
}

jint Java_com_diycircuits_cangjie_TableLoader_updateFrequencyQuick(JNIEnv* env, jobject thiz, jobjectArray arr, jchar ch)
{
  jsize len = (*env)->GetArrayLength(env, arr);
  int count = 0;

  for (count = 0; count < len; count++) {
    jcharArray jc = (jcharArray) (*env)->GetObjectArrayElement(env, arr, count);
    if (jc == NULL) continue;
    jchar* item = (*env)->GetCharArrayElements(env, jc, NULL);
    if (item[2] == ch) {
      item[3]++;
      (*env)->ReleaseCharArrayElements(env, jc, item, JNI_COMMIT);
      (*env)->DeleteLocalRef(env, jc);
      return count;
    }
    (*env)->ReleaseCharArrayElements(env, jc, item, JNI_ABORT);
    (*env)->DeleteLocalRef(env, jc);
  }

  return -1;
}

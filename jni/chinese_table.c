#include <string.h>
#include <stdio.h>
#include <jni.h>
#include <android/log.h>

#define  LOG_TAG    "Cangjie"
#define  LOGE(...)  __android_log_print(ANDROID_LOG_ERROR,LOG_TAG,__VA_ARGS__)

#include "input_method.h"

typedef enum {
  QUICK      = 0,
  CANGJIE    = 1,
  CANGJIE_HK = 2,
} INPUT_METHOD;

int mTotalMatch = 0;
int mSaved = 0;
char data_path[1024] = "0";
char quick_data[1024] = "0";
char cangjie_data[1024] = "0";
char cangjie_hk_data[1024] = "0";
INPUT_METHOD mInputMethod = CANGJIE;

void Java_com_diycircuits_cangjie_TableLoader_setInputMethod(JNIEnv *env, jobject thiz, jint inputMethod)
{
  mInputMethod = inputMethod;
}

void Java_com_diycircuits_cangjie_TableLoader_setPath(JNIEnv *env, jobject thiz, jbyteArray path)
{
  jbyte *buf = (*env)->GetByteArrayElements(env, path, NULL);
  jsize len = (*env)->GetArrayLength(env, path);

  strncpy(data_path, buf, len);
  quick_data[0] = 0;
  strncat(quick_data, data_path, sizeof(quick_data));

  (*env)->ReleaseByteArrayElements(env, path, buf, 0);
}

void Java_com_diycircuits_cangjie_TableLoader_initialize(JNIEnv* env, jobject thiz)
{
  input_method[QUICK]->init(quick_data);  
}

void Java_com_diycircuits_cangjie_TableLoader_reset(JNIEnv* env, jobject thiz)
{
  input_method[QUICK]->reset();
}
 
jchar Java_com_diycircuits_cangjie_TableLoader_getChar(JNIEnv* env, jobject thiz)
{
  /* int count = 0; */

  /* for (count = 0; count < sizeof(quick_index) / sizeof(jint); count++) { */
  /*   quick_index[count] = -1; */
  /* } */

  /* LOGE("Char Quick : %d", sizeof(quick) / (sizeof(jchar) * 4)); */
  /* LOGE("Char Length : %d", sizeof(jchar)); */
  /* LOGE("Char Value : %d %02X %02X %02X\n", strlen(value), value[0] & 0x00FF, value[1] & 0x00FF, value[2] & 0x00FF); */
  
  return 0;
}

jchar Java_com_diycircuits_cangjie_TableLoader_passCharArray(JNIEnv* env, jobject thiz, jcharArray arr)
{
  jchar *buf = (*env)->GetCharArrayElements(env, arr, NULL);
  jsize len = (*env)->GetArrayLength(env, arr);

  /* LOGE("Array Length : %d", len); */

  (*env)->ReleaseCharArrayElements(env, arr, buf, 0);

  return 0;
}

void Java_com_diycircuits_cangjie_TableLoader_searchQuick(JNIEnv* env, jobject thiz, jchar key0, jchar key1)
{
  input_method[QUICK]->searchWord(key0, key1, 0, 0, 0);
}
 
jint Java_com_diycircuits_cangjie_TableLoader_totalMatch(JNIEnv* env, jobject thiz)
{
  return input_method[QUICK]->totalMatch();
}
 
jchar Java_com_diycircuits_cangjie_TableLoader_getMatchChar(JNIEnv* env, jobject thiz, jint index)
{
  return input_method[QUICK]->getMatchChar(index);
}
 
jint Java_com_diycircuits_cangjie_TableLoader_updateFrequencyQuick(JNIEnv* env, jobject thiz, jchar ch)
{
  return input_method[QUICK]->updateFrequency(ch);
}
			
void Java_com_diycircuits_cangjie_TableLoader_saveMatch(JNIEnv* env, jobject thiz)
{
  input_method[QUICK]->saveMatch();
}

void Java_com_diycircuits_cangjie_TableLoader_clearAllFrequency(JNIEnv *env, jobject thiz)
{
  input_method[QUICK]->clearFrequency();
}


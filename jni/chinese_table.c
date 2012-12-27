#include <string.h>
#include <stdio.h>
#include <jni.h>
#include <android/log.h>

#define  LOG_TAG    "Cangjie"
#define  LOGE(...)  __android_log_print(ANDROID_LOG_ERROR,LOG_TAG,__VA_ARGS__)

#include "input_method.h"

int mTotalMatch = 0;
int mSaved = 0;
int mCurrentIm = CANGJIE;
jboolean mEnableHK = 0;
char data_path[1024] = "0";
char quick_data[1024] = "0";
char cangjie_data[1024] = "0";
char cangjie_hk_data[1024] = "0";

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
  input_method[CANGJIE]->init(quick_data);  
}

void Java_com_diycircuits_cangjie_TableLoader_reset(JNIEnv* env, jobject thiz)
{
  input_method[QUICK]->reset();
  input_method[CANGJIE]->reset();
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

void Java_com_diycircuits_cangjie_TableLoader_setInputMethod(JNIEnv* env, jobject thiz, jint im)
{
  mCurrentIm = im;
}
 
void Java_com_diycircuits_cangjie_TableLoader_searchCangjie(JNIEnv* env, jobject thiz, jchar key0, jchar key1, jchar key2, jchar key3, jchar key4)
{
  input_method[mCurrentIm]->searchWord(key0, key1, key2, key3, key4);
}

void Java_com_diycircuits_cangjie_TableLoader_enableHongKongChar(JNIEnv* env, jobject thiz, jboolean hk)
{
  mEnableHK = hk;
  input_method[mCurrentIm]->enableHongKongChar(mEnableHK);
}

jboolean Java_com_diycircuits_cangjie_TableLoader_tryMatchCangjie(JNIEnv* env, jobject thiz, jchar key0, jchar key1, jchar key2, jchar key3, jchar key4)
{
  return input_method[mCurrentIm]->tryMatchWord(key0, key1, key2, key3, key4);
}
 
jint Java_com_diycircuits_cangjie_TableLoader_totalMatch(JNIEnv* env, jobject thiz)
{
  return input_method[mCurrentIm]->totalMatch();
}
 
jchar Java_com_diycircuits_cangjie_TableLoader_getMatchChar(JNIEnv* env, jobject thiz, jint index)
{
  return input_method[mCurrentIm]->getMatchChar(index);
}
 
jint Java_com_diycircuits_cangjie_TableLoader_updateFrequencyQuick(JNIEnv* env, jobject thiz, jchar ch)
{
  return input_method[mCurrentIm]->updateFrequency(ch);
}
			
void Java_com_diycircuits_cangjie_TableLoader_saveMatch(JNIEnv* env, jobject thiz)
{
  input_method[QUICK]->saveMatch();
  input_method[CANGJIE]->saveMatch();
}

void Java_com_diycircuits_cangjie_TableLoader_clearAllFrequency(JNIEnv *env, jobject thiz)
{
  input_method[QUICK]->clearFrequency();
  input_method[CANGJIE]->clearFrequency();
}


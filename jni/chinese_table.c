#include <string.h>
#include <stdio.h>
#include <jni.h>
#include <android/log.h>

#define  LOG_TAG    "Cangjie"
#define  LOGE(...)  __android_log_print(ANDROID_LOG_ERROR,LOG_TAG,__VA_ARGS__)

#include "quick.h"
int mTotalMatch = 0;
int mSaved = 0;
char data_path[1024] = "0";
char quick_data[1024] = "0";
char value[] = "你";

void Java_com_diycircuits_cangjie_TableLoader_setPath(JNIEnv *env, jobject thiz, jbyteArray path)
{
  jbyte *buf = (*env)->GetByteArrayElements(env, path, NULL);
  jsize len = (*env)->GetArrayLength(env, path);

  strncpy(data_path, buf, len);
  quick_data[0] = 0;
  strncat(quick_data, data_path, sizeof(quick_data));
  strncat(quick_data, "/quick.dat", sizeof(quick_data));

  (*env)->ReleaseByteArrayElements(env, path, buf, 0);
}

void Java_com_diycircuits_cangjie_TableLoader_initialize(JNIEnv* env, jobject thiz)
{
  int clear = 0;
  int count = 0;
 
  for (count = 0; count < sizeof(quick_index) / sizeof(jint); count++) {
    quick_index[count] = -1;
  }

  FILE *file = fopen(quick_data, "r");
  if (file != NULL) {
    int read = fread(quick_frequency, 1, sizeof(quick_frequency), file);
    fclose(file);
    if (read != sizeof(quick_frequency))
      clear = 1;
  } else {
    clear = 1;
  }
   
  /* if (clear != 0) { */
    for (count = 0; count < sizeof(quick_frequency) / sizeof(jint); count++) {
      quick_frequency[count] = 0;
    }
  /* } */
}

jchar Java_com_diycircuits_cangjie_TableLoader_getChar(JNIEnv* env, jobject thiz)
{
  int count = 0;

  for (count = 0; count < sizeof(quick_index) / sizeof(jint); count++) {
    quick_index[count] = -1;
  }

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
  int total = sizeof(quick) / (sizeof(jchar) * 3);
  int count = 0;
  int loop  = 0;
  int i = 0;
  int j = 0;
  int found = 0;

  for (count = 0; count < sizeof(quick_index) / sizeof(jint); count++) {
    quick_index[count] = 0;
  }

  LOGE("Match0 %d %d, %d %d, %d %d ", quick[count][0], quick[count][1], key0, key1, count, total);
  for (count = 0; count < total; count++) {
    if (key1 == 0) {
      if (quick[count][0] == key0) {
	quick_index[loop] = count;
	loop++;
	found = 1;
      } else if (found) {
      	break;
      }
    } else {
      if (quick[count][0] == key0 && quick[count][1] == key1) {
	quick_index[loop] = count;
	loop++;
	found = 1;
      } else if (found) {
      	break;
      }
    }
  }

  if (loop > 1) {
    int swap = 1;
    while (swap) {
      swap = 0;
      for (i = 0; i < loop - 1; i++) {
	if (quick_frequency[quick_index[i]] < quick_frequency[quick_index[i + 1]]) {
	  int temp = quick_index[i];
	  quick_index[i] = quick_index[i + 1];
	  quick_index[i + 1] = temp;
	  swap = 1;
	}
      }
    }
  }

  mTotalMatch = loop;
}
 
jint Java_com_diycircuits_cangjie_TableLoader_totalMatch(JNIEnv* env, jobject thiz)
{
  return mTotalMatch;
}
 
jchar Java_com_diycircuits_cangjie_TableLoader_getMatchChar(JNIEnv* env, jobject thiz, jint index)
{
  int total = sizeof(quick) / (sizeof(jchar) * 3);

  if (index >= total) return 0;
  if (quick_index[index] < 0) return 0;

  return quick[quick_index[index]][2];
}
 
jint Java_com_diycircuits_cangjie_TableLoader_updateFrequencyQuick(JNIEnv* env, jobject thiz, jchar ch)
{
  /* jsize len = (*env)->GetArrayLength(env, arr); */
  /* int count = 0; */

  /* for (count = 0; count < len; count++) { */
  /*   jcharArray jc = (jcharArray) (*env)->GetObjectArrayElement(env, arr, count); */
  /*   if (jc == NULL) continue; */
  /*   jchar* item = (*env)->GetCharArrayElements(env, jc, NULL); */
  /*   if (item[2] == ch) { */
  /*     item[3]++; */
  /*     (*env)->ReleaseCharArrayElements(env, jc, item, JNI_COMMIT); */
  /*     (*env)->DeleteLocalRef(env, jc); */
  /*     return count; */
  /*   } */
  /*   (*env)->ReleaseCharArrayElements(env, jc, item, JNI_ABORT); */
  /*   (*env)->DeleteLocalRef(env, jc); */
  /* } */

  int total = sizeof(quick) / (sizeof(jchar) * 3);
  int count = 0;

  for (count = 0; count < total; count++) {
    if (quick[count][2] == ch) {
      quick_frequency[count]++;
      mSaved = 1;
      return quick_frequency[count];
    }
  }

  return -1;
}
			
void Java_com_diycircuits_cangjie_TableLoader_saveMatch(JNIEnv* env, jobject thiz)
{
  FILE *file = fopen(quick_data, "w");
  if (file != NULL) {
    fwrite(quick_frequency, 1, sizeof(quick_frequency), file);
    fclose(file);
  }
}

void Java_com_diycircuits_cangjie_TableLoader_clearAllFrequency(JNIEnv *env, jobject thiz)
{
  int count = 0;
  
  for (count = 0; count < sizeof(quick_frequency) / sizeof(jint); count++) {
    quick_frequency[count] = 0;
  }

  remove(quick_data);
}


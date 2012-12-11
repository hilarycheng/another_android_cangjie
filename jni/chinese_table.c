#include <string.h>
#include <jni.h>
#include <android/log.h>

#define  LOG_TAG    "Cangjie"
#define  LOGE(...)  __android_log_print(ANDROID_LOG_ERROR,LOG_TAG,__VA_ARGS__)

#include "quick.h"
int mTotalMatch = 0;
int mSaved = 0;
char value[] = "你";

jchar Java_com_diycircuits_cangjie_TableLoader_getChar(JNIEnv* env, jobject thiz)
{
  int count = 0;

  for (count = 0; count < sizeof(quick_index) / sizeof(jint); count++) {
    quick_index[count] = -1;
  }

  LOGE("Char Quick : %d", sizeof(quick) / (sizeof(jchar) * 4));
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

void Java_com_diycircuits_cangjie_TableLoader_searchQuick(JNIEnv* env, jobject thiz, jchar key0, jchar key1)
{
  int total = sizeof(quick) / (sizeof(jchar) * 4);
  int count = 0;
  int loop  = 0;
  int i = 0;
  int j = 0;
  int found = 0;

  for (count = 0; count < sizeof(quick_index) / sizeof(jint); count++) {
    quick_index[count] = -1;
  }

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
    for (i = 0; i < loop - 1; i++) {
      for (j = i + 1; j < loop; j++) {
	if (quick[quick_index[i]][3] == 0 && quick[quick_index[j]][3] == 0)
	  break;
	if (quick[quick_index[i]][3] < quick[quick_index[j]][3]) {
	  int temp = quick_index[i];
	  quick_index[i] = quick_index[j];
	  quick_index[j] = temp;
	}
      }
    }
  }

  /* for (i = 0; i < loop; i++) { */
  /*   LOGE("Loop : %d %d %d %d %d", loop, i, j, quick_index[i], quick[quick_index[i]][3]); */
  /* } */

  mTotalMatch = loop;
}
 
jint Java_com_diycircuits_cangjie_TableLoader_totalMatch(JNIEnv* env, jobject thiz)
{
  return mTotalMatch;
}
 
jchar Java_com_diycircuits_cangjie_TableLoader_getMatchChar(JNIEnv* env, jobject thiz, jint index)
{
  int total = sizeof(quick) / (sizeof(jchar) * 4);

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

  int total = sizeof(quick) / (sizeof(jchar) * 4);
  int count = 0;

  for (count = 0; count < total; count++) {
    if (quick[count][2] == ch) {
      quick[count][3]++;
      mSaved = 1;
      return quick[count][3];
    }
  }

  return -1;
}
			
void Java_com_diycircuits_cangjie_TableLoader_saveMatch(JNIEnv* env, jobject thiz)
{
  LOGE("Save Match");
}

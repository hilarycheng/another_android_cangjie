#include <stdio.h>
#include <string.h>
#include "quick_method.h"
#include "quick.h"
#ifndef X86
#include <android/log.h>
#define  LOG_TAG    "Cangjie"
#define  LOGE(...)  __android_log_print(ANDROID_LOG_ERROR,LOG_TAG,__VA_ARGS__)
#else
#define  LOGE(...)
#endif

void quick_init(char *path)
{
  int clear = 1;
  int count = 0;
  char key[8];
  
  quick_func.mSaved = 0;
  strncpy(quick_func.mPath,         path, sizeof(quick_func.mPath));
  strncat(quick_func.mPath, "/quick.dat", sizeof(quick_func.mPath));

  for (count = 0; count < sizeof(quick_index) / sizeof(jint); count++) {
    quick_index[count] = -1;
  }

  memset(key, 0, 8);
  strcpy(key, "QUICK0");

  FILE *file = fopen(quick_func.mPath, "r");
  if (file != 0) {
    int read = fread(quick_func.mBuffer, 1, sizeof(quick_func.mBuffer), file);
    if (memcmp(quick_func.mBuffer, key, 8) == 0) {
      int read = fread(quick_frequency, 1, sizeof(quick_frequency), file);
      fclose(file);
      if (read == sizeof(quick_frequency)) clear = 0;
    }
  }
   
  if (clear != 0) {
    for (count = 0; count < sizeof(quick_frequency) / sizeof(jint); count++) {
      quick_frequency[count] = 0;
    }
  }
}

int quick_maxKey(void)
{
  return 2;
}

void quick_searchWord(jchar key0, jchar key1, jchar key2, jchar key3, jchar key4)
{
  int total = sizeof(quick) / (sizeof(jchar) * 3);
  int count = 0;
  int loop  = 0;
  int i = 0;
  int j = 0;
  int found = 0;
  int offset = 0;

  for (count = 0; count < sizeof(quick_index) / sizeof(jint); count++) {
    quick_index[count] = 0;
  }

  for (count = 0; count < total; count++) {
    if (key1 == 0) {
      if (quick[count][0] == key0) {
	if (key1 == 0 && quick[count][1] == 0) offset++;
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
      for (i = offset; i < loop - 1; i++) {
	if (quick_frequency[quick_index[i]] < quick_frequency[quick_index[i + 1]]) {
	  int temp = quick_index[i];
	  quick_index[i] = quick_index[i + 1];
	  quick_index[i + 1] = temp;
	  swap = 1;
	}
      }
    }
  }

  quick_func.mTotalMatch = loop;
}

int quick_totalMatch(void)
{
  return quick_func.mTotalMatch;
}

int quick_updateFrequency(jchar ch)
{
  int total = sizeof(quick) / (sizeof(jchar) * 3);
  int count = 0;
  int max = 0;
  int index = 0;

  for (count = 0; count < total; count++) {
    if (quick_frequency[count] > max) max = quick_frequency[count];
  }
 
  for (count = 0; count < total; count++) {
    if (quick[count][2] == ch) {
      if (quick_frequency[count] < max || max == 0) {
	quick_frequency[count]++;
	quick_func.mSaved = 1;
	return quick_frequency[count];
      }
    }
  }

  for (count = 0; count < total; count++) {
    if (quick_frequency[count] == max && count != index) {
      quick_frequency[index]++;
      quick_func.mSaved = 1;
      return quick_frequency[index];
    }
  }
  
  return quick_frequency[index];
}

void quick_clearFrequency(void)
{
  int count = 0;
  
  for (count = 0; count < sizeof(quick_frequency) / sizeof(jint); count++) {
    quick_frequency[count] = 0;
  }

  remove(quick_func.mPath);
}

jchar quick_getMatchChar(int index)
{
  int total = sizeof(quick) / (sizeof(jchar) * 3);

  if (index >= total) return 0;
  if (quick_index[index] < 0) return 0;

  return quick[quick_index[index]][2];
}

void quick_reset(void)
{
  quick_func.mTotalMatch = 0;
}

void quick_saveMatch(void)
{
  char key[8];

  if (quick_func.mSaved == 0) return;
  quick_func.mSaved = 0;

  memset(key, 0, 8);
  strcpy(key, "QUICK0");
  FILE *file = fopen(quick_func.mPath, "w");
  if (file != NULL) {
    fwrite(key, 1, sizeof(key), file);
    fwrite(quick_frequency, 1, sizeof(quick_frequency), file);
    fclose(file);
  }
}

jboolean quick_tryMatchWord(jchar c0, jchar c1, jchar c2, jchar c3, jchar c4)
{
  return 1;
}

void quick_enableHongKongChar(jboolean hk)
{
  quick_func.mEnableHK = (hk != 0);
}

struct _input_method quick_func =
{
  .init            = quick_init,
  .maxKey          = quick_maxKey,
  .searchWord      = quick_searchWord,
  .tryMatchWord    = quick_tryMatchWord,
  .enableHongKongChar = quick_enableHongKongChar,
  .totalMatch      = quick_totalMatch,
  .updateFrequency = quick_updateFrequency,
  .clearFrequency  = quick_clearFrequency,
  .getMatchChar    = quick_getMatchChar,
  .reset           = quick_reset,
  .saveMatch       = quick_saveMatch
};

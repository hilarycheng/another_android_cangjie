#include <stdio.h>
#include <string.h>
#include "cangjie_method.h"
#include "cangjie.h"
#ifndef X86
#include <android/log.h>
#define  LOG_TAG    "Cangjie"
#define  LOGE(...)  __android_log_print(ANDROID_LOG_ERROR,LOG_TAG,__VA_ARGS__)
#else
#define  LOGE(...)
#endif

void cangjie_init(char *path)
{
  int clear = 0;
  int count = 0;

  cangjie_func.mSaved = 0;
  strncpy(cangjie_func.mPath,           path, sizeof(cangjie_func.mPath));
  strncat(cangjie_func.mPath, "/cangjie.dat", sizeof(cangjie_func.mPath));

  for (count = 0; count < sizeof(cangjie_index) / sizeof(jint); count++) {
    cangjie_index[count] = -1;
  }

  FILE *file = fopen(cangjie_func.mPath, "r");
  if (file != 0) {
    int read = fread(cangjie_frequency, 1, sizeof(cangjie_frequency), file);
    fclose(file);
    if (read != sizeof(cangjie_frequency))
      clear = 1;
  } else {
    clear = 1;
  }
   
  if (clear != 0) {
    for (count = 0; count < sizeof(cangjie_frequency) / sizeof(jint); count++) {
      cangjie_frequency[count] = 0;
    }
  }
}

int cangjie_maxKey(void)
{
  return 5;
}

void cangjie_searchWord(jchar key0, jchar key1, jchar key2, jchar key3, jchar key4)
{
  jchar src[5];
  int total = sizeof(cangjie) / (sizeof(jchar) * CANGJIE_COLUMN);
  int count = 0;
  int loop  = 0;
  int i = 0;
  int j = 0;
  int found = 0;
  int offset = 0;
  int match = 0;
  int count0 = 0, count1 = 0;

  src[0] = key0;
  src[1] = key1;
  src[2] = key2;
  src[3] = key3;
  src[4] = key4;
  
  for (count = 0; count < sizeof(cangjie_index) / sizeof(jint); count++) {
    cangjie_index[count] = 0;
  }

  for (count0 = 0; count0 < total; count0++) {
    if (cangjie[count0][0] != src[0]) { // First code does not matched, skip it
      if (found == 1)
	break;
      continue;
    }

    match = 1;
    for (count1 = 1; count1 < 5; count1++) {
      if (src[count1] == 0)
	break;
      if (cangjie[count0][count1] == src[count1] && (cangjie_func.mEnableHK != 0 || cangjie[count0][6] == 0))
	match = 1;
      else {
	match = 0;
	break;
      }
    }
    /* LOGE("Cangjie : %02x %02x %02x %02x %02x, %02x %02x %02x %02x %02x, Match %d\n", */
    /* 	 cangjie[count0][0], */
    /* 	 cangjie[count0][1], */
    /* 	 cangjie[count0][2], */
    /* 	 cangjie[count0][3], */
    /* 	 cangjie[count0][4], */
    /* 	 key0, */
    /* 	 key1, */
    /* 	 key2, */
    /* 	 key3, */
    /* 	 key4, */
    /* 	 match); */
    if (match != 0) {
      cangjie_index[loop] = count0;
      loop++;
    }

    found = 1;
  }

  cangjie_func.mTotalMatch = loop;
}

jboolean cangjie_tryMatchWord(jchar key0, jchar key1, jchar key2, jchar key3, jchar key4)
{
  jchar src[5];
  int total = sizeof(cangjie) / (sizeof(jchar) * CANGJIE_COLUMN);
  int count = 0;
  int loop  = 0;
  int i = 0;
  int j = 0;
  int found = 0;
  int offset = 0;
  int match = 0;
  int count0 = 0, count1 = 0;

  src[0] = key0;
  src[1] = key1;
  src[2] = key2;
  src[3] = key3;
  src[4] = key4;
  
  for (count0 = 0; count0 < total; count0++) {
    if (cangjie[count0][0] != src[0]) { // First code does not matched, skip it
      if (found == 1)
	break;
      continue;
    }

    match = 1;
    for (count1 = 1; count1 < 5; count1++) {
      if (src[count1] == 0)
	break;
      if (cangjie[count0][count1] == src[count1] && (cangjie_func.mEnableHK != 0 || cangjie[count0][6] == 0))
	match = 1;
      else {
	match = 0;
	break;
      }
    }
    if (match != 0) {
      loop++;
    }

    found = 1;
  }

  return (loop > 0) ? 1 : 0;
}

int cangjie_totalMatch(void)
{
  return cangjie_func.mTotalMatch;
}

int cangjie_updateFrequency(jchar ch)
{
  int index = 0;

  return cangjie_frequency[index];
}

void cangjie_clearFrequency(void)
{
  int count = 0;
  
  for (count = 0; count < sizeof(cangjie_frequency) / sizeof(jint); count++) {
    cangjie_frequency[count] = 0;
  }

  remove(cangjie_func.mPath);
}

jchar cangjie_getMatchChar(int index)
{
  int total = sizeof(cangjie) / (sizeof(jchar) * CANGJIE_COLUMN);

  if (index >= total) return 0;
  if (cangjie_index[index] < 0) return 0;

  return cangjie[cangjie_index[index]][5];
}

void cangjie_reset(void)
{
  cangjie_func.mTotalMatch = 0;
}

void cangjie_saveMatch(void)
{
  if (cangjie_func.mSaved == 0) return;
  cangjie_func.mSaved = 0;
  FILE *file = fopen(cangjie_func.mPath, "w");
  if (file != NULL) {
    fwrite(cangjie_frequency, 1, sizeof(cangjie_frequency), file);
    fclose(file);
  }
}

void cangjie_enableHongKongChar(jboolean hk)
{
  cangjie_func.mEnableHK = (hk != 0);
}

struct _input_method cangjie_func =
{
  .init            = cangjie_init,
  .maxKey          = cangjie_maxKey,
  .searchWord      = cangjie_searchWord,
  .tryMatchWord    = cangjie_tryMatchWord,
  .enableHongKongChar = cangjie_enableHongKongChar,
  .totalMatch      = cangjie_totalMatch,
  .updateFrequency = cangjie_updateFrequency,
  .clearFrequency  = cangjie_clearFrequency,
  .getMatchChar    = cangjie_getMatchChar,
  .reset           = cangjie_reset,
  .saveMatch       = cangjie_saveMatch
};

#include <stdio.h>
#include <string.h>
#include "cangjie_method.h"
#include "cangjie.h"

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
  int total = sizeof(cangjie) / (sizeof(jchar) * CANGJIE_COLUMN);
  int count = 0;
  int loop  = 0;
  int i = 0;
  int j = 0;
  int found = 0;

  for (count = 0; count < sizeof(cangjie_index) / sizeof(jint); count++) {
    cangjie_index[count] = 0;
  }

  for (count = 0; count < total; count++) {
    if (key1 == 0) {
      if (cangjie[count][0] == key0) {
	cangjie_index[loop] = count;
	loop++;
	found = 1;
      } else if (found) {
      	break;
      }
    } else {
      if (cangjie[count][0] == key0 && cangjie[count][1] == key1) {
	cangjie_index[loop] = count;
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
	if (cangjie_frequency[cangjie_index[i]] < cangjie_frequency[cangjie_index[i + 1]]) {
	  int temp = cangjie_index[i];
	  cangjie_index[i] = cangjie_index[i + 1];
	  cangjie_index[i + 1] = temp;
	  swap = 1;
	}
      }
    }
  }

  cangjie_func.mTotalMatch = loop;
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

  return cangjie[cangjie_index[index]][2];
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

struct _input_method cangjie_func =
{
  .init            = cangjie_init,
  .maxKey          = cangjie_maxKey,
  .searchWord      = cangjie_searchWord,
  .totalMatch      = cangjie_totalMatch,
  .updateFrequency = cangjie_updateFrequency,
  .clearFrequency  = cangjie_clearFrequency,
  .getMatchChar    = cangjie_getMatchChar,
  .reset           = cangjie_reset,
  .saveMatch       = cangjie_saveMatch
};

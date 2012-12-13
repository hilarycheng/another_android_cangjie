#include <stdio.h>
#include <string.h>
#include "quick_method.h"
#include "quick.h"

void init_quick_method(char *path)
{
  int clear = 0;
  int count = 0;

  quick_functions.mSaved = 0;
  strncpy(quick_functions.mPath,         path, sizeof(quick_functions.mPath));
  strncat(quick_functions.mPath, "/quick.dat", sizeof(quick_functions.mPath));

  for (count = 0; count < sizeof(quick_index) / sizeof(jint); count++) {
    quick_index[count] = -1;
  }

  FILE *file = fopen(quick_functions.mPath, "r");
  if (file != 0) {
    int read = fread(quick_frequency, 1, sizeof(quick_frequency), file);
    fclose(file);
    if (read != sizeof(quick_frequency))
      clear = 1;
  } else {
    clear = 1;
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

void quick_searchWord(jchar key0, jchar key1)
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

  quick_functions.mTotalMatch = loop;
}

int quick_totalMatch(void)
{
  return quick_functions.mTotalMatch;
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
	quick_functions.mSaved = 1;
	return quick_frequency[count];
      }
    }
  }

  for (count = 0; count < total; count++) {
    if (quick_frequency[count] == max && count != index) {
      quick_frequency[index]++;
      quick_functions.mSaved = 1;
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

  remove(quick_functions.mPath);
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
  quick_functions.mTotalMatch = 0;
}

void quick_saveMatch(void)
{
  if (quick_functions.mSaved == 0) return;
  quick_functions.mSaved = 0;
  FILE *file = fopen(quick_functions.mPath, "w");
  if (file != NULL) {
    fwrite(quick_frequency, 1, sizeof(quick_frequency), file);
    fclose(file);
  }
}

struct _input_method quick_functions =
{
  .init            = init_quick_method,
  .maxKey          = quick_maxKey,
  .searchWord      = quick_searchWord,
  .totalMatch      = quick_totalMatch,
  .updateFrequency = quick_updateFrequency,
  .clearFrequency  = quick_clearFrequency,
  .getMatchChar    = quick_getMatchChar,
  .reset           = quick_reset,
  .saveMatch       = quick_saveMatch
};

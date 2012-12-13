#include "quick_method.h"
// #include "quick.h"

void init_quick_method(char *path)
{
}

int quick_maxKey(void)
{
}

void quick_searchWord(jchar c0, jchar c1)
{
}

int quick_totalMatch(void)
{
}

int quick_updateFrequency(jchar c0)
{
}

int quick_clearFrequency(void)
{
}

jchar quick_getMatchChar(int index)
{
}

void quick_reset(void)
{
}

struct input_method quick_functions =
{
  .init            = init_quick_method,
  .maxKey          = quick_maxKey,
  .searchWord      = quick_searchWord,
  .totalMatch      = quick_totalMatch,
  .updateFrequency = quick_updateFrequency,
  .clearFrequency  = quick_clearFrequency,
  .getMatchChar    = quick_getMatchChar,
  .reset           = quick_reset
};

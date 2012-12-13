#ifndef X86
#include <jni.h>
#endif

struct input_method {
  void  (*init)(void);
  int   (*maxKey)(void);
  void  (*searchWord)(jchar c0, jchar c1);
  int   (*totalMatch)(void);
  int   (*updateFrequency)(jchar c0);
  int   (*clearFrequency)(void);
  void  (*reset)(void);
  jchar (*getMatchChar)(int index);
  char  mPath[1024];
  int   mTotalMatch;
};

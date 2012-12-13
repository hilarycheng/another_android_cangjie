#ifndef X86
#include <jni.h>
#endif

struct _input_method {
  void  (*init)(char *path);
  int   (*maxKey)(void);
  void  (*searchWord)(jchar c0, jchar c1);
  int   (*totalMatch)(void);
  int   (*updateFrequency)(jchar c0);
  void  (*clearFrequency)(void);
  void  (*reset)(void);
  jchar (*getMatchChar)(int index);
  void  (*saveMatch)(void);
  char  mPath[1024];
  int   mTotalMatch;
  int   mSaved;
};

struct _input_method *input_method[3];

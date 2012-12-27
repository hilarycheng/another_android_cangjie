#ifndef INPUT_METHOD_H
#define INPUT_METHOD_H

#ifndef X86
#include <jni.h>
#else
#include <common.h>
#endif

typedef enum {
  QUICK = 0,
  CANGJIE = 1,
} INPUT_METHOD;

struct _input_method {
  void  (*init)(char *path);
  int   (*maxKey)(void);
  void  (*searchWord)(jchar c0, jchar c1, jchar c2, jchar c3, jchar c4);
  jboolean (*tryMatchWord)(jchar c0, jchar c1, jchar c2, jchar c3, jchar c4);
  void  (*enableHongKongChar)(jboolean hk);
  int   (*totalMatch)(void);
  int   (*updateFrequency)(jchar c0);
  void  (*clearFrequency)(void);
  void  (*reset)(void);
  jchar (*getMatchChar)(int index);
  void  (*saveMatch)(void);
  char  mPath[1024];
  char  mBuffer[8];
  int   mTotalMatch;
  int   mSaved;
  jboolean mEnableHK;
};

extern struct _input_method *input_method[3];

#endif

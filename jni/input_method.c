#include "quick_method.h"
#include "cangjie_method.h"

struct _input_method *input_method[3] = {
  &quick_func,
  &cangjie_func,
  0
};


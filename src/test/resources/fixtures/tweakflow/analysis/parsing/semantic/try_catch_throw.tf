library lib {
  try_catch_e:      try 0 catch e false;      # try evaluating 0, catch as exception e and return false
  try_catch:        try 0 catch false;        # try evaluation 0, ignore exception e and return false
  try_catch_e_t:    try 0 catch e, trace nil; # try evaluating 0, catch exception and trace and return nil
  throw_nil:        throw nil;                # throw an exception

}
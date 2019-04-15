library lib {
  reference:        import_name.lib.x;        # reference string
  lib_ref:          library::e0;              # library reference
  mod_short_ref:    ::lib.e0;                 # module reference
  mod_ref:          module::lib.e0;           # module reference
  global_short_ref: $global_var;              # global reference
  global_ref:       global::global_var;       # global reference
  local_ref:        e0;                       # local reference
}
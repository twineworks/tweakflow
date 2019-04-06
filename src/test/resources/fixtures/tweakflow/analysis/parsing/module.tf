
doc "Module documentation string"

meta {
  :version "0.0.0"
}

module;

import * as x from "other/module";
import lib_x as x from "other/module";
import * as m, lib_x as x, lib_y from "other/module";

alias m as q;
export m;
export m as my_mod;


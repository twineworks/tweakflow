
import * as zoobar from "./lib/zoobar.tf";
alias zoobar.foo as foo;
alias zoobar.bar as bar;

# import foo, bar from "./lib/zoobar.tf"

library main_spec {
#  foo_a: foo.a == "a"
#  foo_b: foo.b == "b"
#  bar_c: bar.c == "c"
#  bar_d: bar.d == "d"
}

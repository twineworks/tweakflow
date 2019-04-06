
import b from "./module_b.tf";

export library a {
  n: "a";
  f: (c) ->
    (x) -> [x, c, b.n];
}

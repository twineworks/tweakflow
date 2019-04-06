import a from "./module_a.tf";

export library b {
  n: "b";
  f: (c) ->
    (x) -> [x, c, a.n];
}

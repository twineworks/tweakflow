import lib_a from "./imports/module_a.tf";

doc "lib doc string"
export library lib
{
  e0: 1;
  e1: let {
        string a: "foo";
      }
      a;
  e2: (string a = "foo") -> a;
}

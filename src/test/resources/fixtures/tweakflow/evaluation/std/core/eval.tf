import core from "std"

library eval_spec {
  native_code_restricted:
    (
    try
      core.eval("let {f: () -> boolean via {:class 'com.twineworks.tweakflow.lang.values.NativeConstantTrue'}} f()")
    catch error
      error[:code]
    )
    ==
    "NATIVE_CODE_RESTRICTED"

  evaluates_constant:
    core.eval("'hello world'") == "hello world"

  evaluates_plus_operator:
    core.eval("1+2") == 3

  evaluates_references:
    core.eval("let {a: 1; b: 2} [a, b]") == [1, 2]

  evaluates_functions:
    core.eval("let {f: (x) -> x+1} f(4)") == 5
}
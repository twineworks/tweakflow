import core from "std";
import assert, expect, expect_error, to, describe, it, subject, before, after from "std/spec";

library spec {
  spec:
    describe("core.eval", [

      it("native_code_restricted", () ->
        expect_error(
          () -> core.eval("let {f: () -> boolean via {:class 'com.twineworks.tweakflow.lang.values.NativeConstantTrue'};} f()"),
          to.have_code("NATIVE_CODE_RESTRICTED")
        )
      ),

      it("evaluates_constant", () ->
        core.eval("'hello world'") == "hello world"
      ),

      it("evaluates_plus_operator", () ->
        core.eval("1+2") == 3
      ),

      it("evaluates_references", () ->
        expect(
          core.eval("let {a: 1; b: 2;} [a, b]"),
          to.be([1, 2])
        )
      ),

      it("evaluates_functions", () ->
        expect(
          core.eval("let {f: (x) -> x+1;} f(4)"),
          to.be(5)
        )
      ),

      it("produces_errors", () ->
        expect_error(
          () -> core.eval("x"),
          to.have_code("UNRESOLVED_REFERENCE")
        )
      ),
    ]);
}
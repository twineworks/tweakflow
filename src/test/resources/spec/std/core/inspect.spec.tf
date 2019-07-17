import core from "std";
import assert, expect, expect_error, to, describe, it, subject, before, after from "std/spec";

alias core.inspect as inspect;

library spec {
  spec:
    describe("core.inspect", [

      it("is_function", () ->
        inspect is function
      ),

      it("inspects_long", () ->
        expect(inspect(1), to.be("1"))
      ),

      it("inspects_double", () ->
        expect(inspect(1.0), to.be("1.0"))
      ),

      it("inspects_list", () ->
        expect(inspect([1, 2, 3]), to.be("[1, 2, 3]"))
      ),

      it("inspects_dict", () ->
        expect(
          inspect({:a 1, :b 2}),
          to.be(
~~~
{
  :a 1,
  :b 2
}
~~~
          )
        )
      ),

      it("inspects_string", () ->
        expect(inspect("foo"), to.be('"foo"'))
      ),

      it("inspects_boolean", () ->
        expect(inspect(true), to.be('true'))
      ),

      it("inspects_binary", () ->
        expect(inspect(0b01020304), to.be('0b01020304'))
      ),

      it("inspects_function", () ->
        expect(inspect(() -> 1), to.be("function"))
      ),

      it("inspects_datetime", () ->
        expect(inspect(1970-01-01T00:00:00Z@UTC), to.be("1970-01-01T00:00:00Z@UTC"))
      ),

      it("inspects_datetime_unescaped_timzone_name", () ->
        expect(inspect(1970-01-01T00:00:00+01:00@Europe/Berlin), to.be("1970-01-01T00:00:00+01:00@Europe/Berlin"))
      ),

      it("inspects_datetime_escaped_timzone_name", () ->
        expect(inspect(1970-01-01T00:00:00+01:00@`UTC+01:00`), to.be("1970-01-01T00:00:00+01:00@`UTC+01:00`"))
      ),

      it("inspects_default_nil", () ->
        expect(inspect(), to.be("nil"))
      ),

    ]);
}
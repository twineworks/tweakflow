import core from "std";
import expect, expect_error, to from "std/assert.tf";

alias core.inspect as inspect;

library eval_spec {

  is_function:
    inspect is function;

  inspects_long:
    expect(inspect(1), to.be("1"));

  inspects_double:
    expect(inspect(1.0), to.be("1.0"));

  inspects_list:
    expect(inspect([1, 2, 3]), to.be("[1, 2, 3]"));

  inspects_dict:
    inspect({:a 1, :b 2})
    ===
~~~
{
  :a 1,
  :b 2
}
~~~;

  inspects_string:
    expect(inspect("foo"), to.be('"foo"'));

  inspects_boolean:
    expect(inspect(true), to.be('true'));

  inspects_function:
    expect(inspect(() -> 1), to.be("function"));

  inspects_datetime:
    expect(inspect(1970-01-01T00:00:00Z@UTC), to.be("1970-01-01T00:00:00Z@UTC"));

  inspects_datetime_unescaped_timzone_name:
    expect(inspect(1970-01-01T00:00:00+01:00@Europe/Berlin), to.be("1970-01-01T00:00:00+01:00@Europe/Berlin"));

  inspects_datetime_escaped_timzone_name:
    expect(inspect(1970-01-01T00:00:00+01:00@`UTC+01:00`), to.be("1970-01-01T00:00:00+01:00@`UTC+01:00`"));

  inspects_default_nil:
    expect(inspect(), to.be("nil"));


}
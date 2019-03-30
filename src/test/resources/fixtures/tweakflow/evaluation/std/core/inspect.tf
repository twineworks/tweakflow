import core from "std"

alias core.inspect as inspect;

library eval_spec {

  is_function:
    inspect is function;

  inspects_long:
    inspect(1) === "1";

  inspects_double:
    inspect(1.0) === "1.0";

  inspects_list:
    inspect([1, 2, 3]) === "[1, 2, 3]";

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
    inspect("foo") === '"foo"';

  inspects_boolean:
    inspect(true) === 'true';

  inspects_function:
    inspect(() -> 1) === "function";

  inspects_datetime:
    inspect(1970-01-01T00:00:00Z@UTC) === "1970-01-01T00:00:00Z@UTC"

  inspects_default_nil:
    inspect() === "nil";


}
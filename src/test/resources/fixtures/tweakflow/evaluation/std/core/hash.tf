import core from "std"

alias core.hash as hash;

library eval_spec {

  is_function:
    hash is function;

  of_nil:
    hash(nil) === 0;

  of_default_nil:
    hash() === 0;

  of_true:
    hash(true) === 1;

  of_false:
    hash(false) === 0;

  of_zero:
    hash(0) === 0;

  of_one:
    hash(1) === 1072693248;

  of_a:
    hash("a") === 97;

  of_empty_list:
    hash([]) === 1;

  of_empty_dict:
    debug hash({}) === 0;

  of_equal_nums:
    hash(1) === hash(1.0);

  of_equal_lists:
    hash([1, 2, 3]) === hash([1.0, 2.0, 3.0]);

  of_equal_dicts:
    hash({:a 1, :b 2, :c 3}) === hash({:a 1.0, :b 2.0, :c 3.0});

}
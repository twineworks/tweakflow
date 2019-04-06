import core from "std";
alias core.id as id;

library eval_spec {

  is_function:
    id is function;

  evaluates_to_input_str:
    id("foo") === "foo";

  evaluates_to_input_list:
    id([]) === [];

  evaluates_to_input_nil:
    id(nil) === nil;

  evaluates_to_default_nil:
    id() === nil;

}
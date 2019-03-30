import core from "std"

alias core.nil? as nil?;

library eval_spec {

  is_function:
    nil? is function

  non_nil_false:
    nil?("") === false

  nil_true:
    nil?(nil) === true

  default_true:
    nil?() === true

}
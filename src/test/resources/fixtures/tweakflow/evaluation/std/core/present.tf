import core from "std";
alias core.present? as present?;

library eval_spec {

  is_function:
    present? is function;

  non_nil_true:
    present?("") === true;

  nil_false:
    present?(nil) === false;

  default_false:
    present?() === false;

}
import data from "std.tf"
alias data.map as map

library lib {
  throw_error: () -> throw {:code "test", :message "error"}
}

library throw_spec {

  basic_catch_trace:
    let {
      caught:
        try
          lib.throw_error()
        catch error, trace
          {
            :error error,
            :trace trace
          }

    }
    caught[:error] ==
      {:code "test", :message "error"}
    &&
    caught[:trace] == {
      :message      "CUSTOM_ERROR"
      :code         "CUSTOM_ERROR"
      :value        {:code "test", :message "error"}
      :source       'throw {:code "test", :message "error"}'
      :at           "fixtures/tweakflow/evaluation/throwing/try_catch_trace.tf:5:22"
      :stack       ["fixtures/tweakflow/evaluation/throwing/try_catch_trace.tf:5:22",  # throw
                    "fixtures/tweakflow/evaluation/throwing/try_catch_trace.tf:14:11", # lib.throw_error()
                    "fixtures/tweakflow/evaluation/throwing/try_catch_trace.tf:11:5",  # let
                    "fixtures/tweakflow/evaluation/throwing/try_catch_trace.tf:10:3",  # basic_catch_trace
                    "fixtures/tweakflow/evaluation/throwing/try_catch_trace.tf:8:1",   # library throw_spec
                    "fixtures/tweakflow/evaluation/throwing/try_catch_trace.tf:1:1"]   # module

    }

}

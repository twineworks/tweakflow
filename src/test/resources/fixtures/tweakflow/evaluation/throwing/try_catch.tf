
library lib
{
  catch_error:
               try
                 throw "error"
               catch e
                 "caught: " .. e

  catch_let:
                try
                  throw {:message "another error"}
                catch e
                  let {
                    message: e[:message]
                  }
                  "caught: #{message}"

  let_catch:    let {
                  message: "yet another error"
                }
                try
                  throw message
                catch e
                  "caught: #{e}"

  catch_trace:  try
                  throw "error"
                catch e, trace
                  let {
                    t0: trace[:stack, 0]
                  }
                  "caught: #{e} trace: #{t0}"

}

export helpers.to_match as to_match;
export helpers.to_not_match as to_not_match;

export library helpers {

  # helper validating that a matcher is successful
  to_match: (expected) -> (x) ->
    {
      :semantic "to match",
      :expected expected,
      :success x(expected)[:success]
    }
  ;

  # helper validating that a matcher is not successful
  to_not_match: (expected) -> (x) ->
    {
      :semantic "to not match",
      :expected expected,
      :success !(x(expected)[:success])
    }
  ;

}
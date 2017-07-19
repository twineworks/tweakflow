import core, data, math from "std"

alias data.range as range
alias math.sqrt as sqrt

library lib {
  is_list: (x) -> typeof x == "list"
  is_long: (x) -> typeof x == "long"
}

library match_expression_spec {

  no_match: (
      match "something"
        1 -> "foo"
        2 -> "bar"
    )
    == nil

  match_default: (
      match 3
        1 -> "one"
        2 -> "two"
        default -> "unknown"
    )
    == "unknown"

  match_long: (
      match 2
        1 -> "one"
        2 -> "two"
        default -> "unknown"
    )
    == "two"

  match_empty_list: (
      match []
        [] -> "empty"
        [2,3,4] -> "two"
        default -> "unknown"
    )
    == "empty"

  match_predicate: (
      match [1,2,3]
        lib.is_list -> "list"
        lib.is_long -> "long"
        default -> "unknown"
    )
    == "list"

}

library list_pattern_spec {

  match_list: (
      match [1,2,3]
        [1,@,@] -> "one"
        [2,3,4] -> "two"
        default -> "unknown"
    )
    == "one"

  match_list_nested_capture: (
      match [1,2,3]
         [@f,2,@g] -> f+g
         default -> "unknown"
    )
    == 4

  match_list_deeply_nested_capture: (
      match [1,2, [1,2,3]]
         [@f,2, [@,2,@g]] -> f+g
         default -> "unknown"
    )
    == 4

  match_sublist_whole: (
      match [1,2, [1,2,3]]
         [1,2, [@,@,@] @s] -> s[0]+s[2]
         default -> "unknown"
    )
    == 4

  match_wrong_length_list: (
      match [1,2,3]
        [@,@] -> "two"
        [@,@,@,@] -> "four"
        default -> "none"
    )
    == "none"

  # head tail variant

  match_head_tail_list: (
     match [1,2,3]
       [@head, @...tail] -> head + tail[1]
       default -> "no match"
     )
     == 4

  match_head_tail_whole: (
     match [1,2,3]
       [@head, @...tail] @whole -> whole[0] + whole[2]
       default -> "no match"
     )
     == 4

  match_head_ignore_tail: (
     match [1,2,3]
       [@head, @...] -> head
       default -> "no match"
     )
     == 1

  match_tail_ignore_head: (
     match [1,2,3]
       [@, @...tail] -> tail
       default -> "no match"
     )
     == [2, 3]

  match_tail_ignore_many_head: (
     match [1,2,3,4,5,6]
       [@,@,@,@...tail] -> tail
       default -> "no match"
     )
     == [4, 5, 6]

  match_everything_as_tail: (
     match [1,2,3]
       [@...tail] -> tail
       default -> "no match"
     )
     == [1, 2, 3]

  match_empty_as_tail: (
     match [1]
       [@,@...tail] -> tail
       default -> "no match"
     )
     == []

  match_ignore_head_and_tail: (
     match [1,2,3]
       [@,@...] -> "non-empty"
       default -> "empty"
     )
     == "non-empty"

  match_fail_empty_on_head_and_tail: (
     match []
       [@,@...] -> "non-empty"
       default -> "empty"
     )
     == "empty"

  # init last variant

  match_init_last_list: (
     match [1,2,3]
       [@...init, @last] -> init[0] + last
       default -> "no match"
     )
     == 4

  match_init_last_list_whole: (
     match [1,2,3]
       [@...init, @last] @whole -> whole[0] + whole[2]
       default -> "no match"
     )
     == 4

  match_last_ignore_init: (
     match [1,2,3]
       [@..., @last] -> last
       default -> "no match"
     )
     == 3

  match_init_ignore_last: (
     match [1,2,3]
       [@...init, @] -> init
       default -> "no match"
     )
     == [1, 2]

  match_init_ignore_many_last: (
     match [1,2,3,4,5,6]
       [@...init,@,@,@] -> init
       default -> "no match"
     )
     == [1,2,3]

  match_empty_as_init: (
     match [1]
       [@...init,@] -> init
       default -> "no match"
     )
     == []

  match_ignore_init_and_last: (
     match [1,2,3]
       [@..., @] -> "non-empty"
       default -> "empty"
     )
     == "non-empty"

  match_fail_empty_on_init_and_last: (
     match []
       [@..., @] -> "non-empty"
       default -> "empty"
     )
     == "empty"

  # middle list variant

  match_mid_list: (
     match [1,2,3]
       [@head, @...mid, @last] -> head + mid[0] + last
       default -> "no match"
     )
     == 6

  match_mid_list_whole: (
     match [1,2,3]
       [@, @..., @] @whole -> whole[0] + whole[1] + whole[2]
       default -> "no match"
     )
     == 6

  match_head_last_ignore_mid: (
     match [1,2,3]
       [@head, @..., @last] -> head+last
       default -> "no match"
     )
     == 4

  match_mid_ignore_head_and_last: (
     match [1,2,3]
       [@, @...mid, @] -> mid
       default -> "no match"
     )
     == [2]

  match_mid_ignore_many_head_and_last: (
     match [1,2,3,4,5,6]
       [@,@,@...mid,@,@] -> mid
       default -> "no match"
     )
     == [3, 4]


  match_empty_as_mid: (
     match [1,2]
       [@,@...mid,@] -> mid
       default -> "no match"
     )
     == []

  match_ignore_head_mid_and_last: (
     match [1,2]
       [@,@...,@] -> "two_or_more"
       default -> "empty"
     )
     == "two_or_more"

  match_fail_empty_on_mid: (
     match []
       [@,@...,@] -> "two_or_more"
       default -> "one_or_empty"
     )
     == "one_or_empty"

  match_fail_single_on_mid: (
     match [1]
       [@,@...,@] -> "two_or_more"
       default -> "one_or_empty"
     )
     == "one_or_empty"

}


library dict_pattern_spec {

  no_match_with_extra_keys: (
      match {:a 1, :b 2, :c 3}
        {:a @, :b @} -> "matched"
        default -> "unknown"
    )
    == "unknown"

  match_rest_with_extra_keys: (
      match {:a 1, :b 2, :c 3}
        {:a @, :b @, @...} -> "matched"
        default -> "unknown"
    )
    == "matched"

  match_rest: (
      match {:a 1, :b 2, :c 3, :d 4}
        {:a @, :b @, @...rest} -> rest
        default -> "unknown"
    )
    == {:c 3, :d 4}

  match_rest_and_capture: (
      match {:a 1, :b 2, :c 3, :d 4}
        {:a @, @...rest} @all -> all
        default -> "unknown"
    )
    == {:a 1, :b 2, :c 3, :d 4}


  match_everything_as_rest: (
      match {:a 1, :b 2, :c 3, :d 4}
        {@...rest} -> rest
        default -> "unknown"
    )
    == {:a 1, :b 2, :c 3, :d 4}

  match_dict_with_capture: (
      match {:a 1, :b 2, :c 3}
        {:a @a, :b @, :c @c} -> a+c
        default -> "unknown"
    )
    == 4

  match_dict_capture_all: (
      match {:a 1, :b 2, :c 3}
        {:a @, :b @, :c @} @m -> m[:a] + m[:c]
        default -> "unknown"
    )
    == 4

  match_nested_dict_with_capture: (
      match {:a {:a 1, :b 2, :c 3}}
        {:a {:a @a, :b @, :c @c}} -> a+c
        default -> "unknown"
    )
    == 4

}

library operator_spec {

  match_capture_and_first_guard: (
      match 1
         0 -> "zero"
        @m, m > 0 -> "positive: "..m
        @m, m < 0 -> "negative: "..m
    )
    == "positive: 1"

  match_capture_and_second_guard: (
      match -1
         0 -> "zero"
        @m, m > 0 -> "positive: "..m
        @m, m < 0 -> "negative: "..m
    )
    == "negative: -1"

  match_type: (
      match 7
        long -> "long"
        double -> "double"
        default -> "unknown"
    )
    == "long"

  match_type_capture: (
      match 7
        long    @a -> "long "..a
        double  @a -> "double "..a
        default -> "unknown"
    )
    == "long 7"

  match_expression_capture: (
      match 3
        1 @a -> "one "..a
        2 @a -> "two "..a
        3 @a -> "three "..a
        default -> "unknown"
    )
    == "three 3"

  match_named_capture: (
      match 100
         1 -> "one"
         2 -> "two"
        @m -> "item: "..m
    )
    == "item: 100"

  match_anonymous_capture: (
      match 100
         1 -> "one"
         2 -> "two"
         @ -> "other"
    )
    == "other"

}
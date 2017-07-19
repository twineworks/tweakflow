import regex from "std"

library p {
  world_greeting:   regex.replacing('hello (\w+)', "hello world")
  greetings:        regex.replacing('hello (\w+)', "greetings $1")
}

library matching_spec {
  hello_world:  p.world_greeting("hello you")            == "hello world"
  hello_twice:  p.world_greeting("hello a, hello b")     == "hello world, hello world"
  greetings:    p.greetings("hello joe")                 == "greetings joe"
  greetings_twice: p.greetings("hello joe, hello sue")   == "greetings joe, greetings sue"
}
import regex from "std"

library p {
  hello?: regex.matching("hello .*")
  empty?: regex.matching("")
  digits?: regex.matching('\d+')
}

library matching_spec {
  hello_world:  p.hello?("hello world")
  helloween:    p.hello?("helloween")      == false
  empty:        p.empty?("")
  a:            p.empty?("a")              == false
  digits:       p.digits?("12345")
  non_digits:   p.digits?("123-45")        == false

  nil_pattern:  try regex.matching(nil) catch "error" == "error"
}
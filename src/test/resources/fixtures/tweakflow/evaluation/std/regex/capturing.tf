import regex from "std"

library p {
  greeting: regex.capturing("hello (.*)")
  digits: regex.capturing('(?:(\d+)-?)*')
}

library matching_spec {
  hello_world:  p.greeting("hello world")   == ["hello world", "world"]
  helloween:    p.greeting("helloween")     == []
  digits:       p.digits("12345")           == ["12345", "12345"]
  multi_digits: p.digits("12345-54321-999") == ["12345-54321-999", "999"] # capture only last ocurrence of group
  non_digits:   p.digits("a12345")          == []
}
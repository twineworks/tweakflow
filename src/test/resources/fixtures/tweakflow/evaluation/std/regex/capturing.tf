import regex from "std";
import expect, expect_error, to from "std/assert.tf";

alias regex.capturing as capturing;

library p {
  greeting: capturing("hello (.*)");
  digits: capturing('(?:(\d+)-?)*');
  date_time: capturing('(\d{4})-(\d{2})-(\d{2})(?:T(\d{2}:\d{2}))?');
}

library capturing_spec {
  hello_world:  expect(p.greeting("hello world"),       to.be(["hello world", "world"]));
  helloween:    expect(p.greeting("helloween"),         to.be([]));
  of_nil:       expect(p.greeting(nil),                 to.be(nil));

  digits:       expect(p.digits("12345"),               to.be(["12345", "12345"]));
  multi_digits: expect(p.digits("12345-54321-999"),     to.be(["12345-54321-999", "999"])); # capture only last ocurrence of group
  non_digits:   expect(p.digits("a12345"),              to.be([]));

  date:         expect(p.date_time("2017-05-24"),       to.be(["2017-05-24", "2017", "05", "24", nil]));
  date_time:    expect(p.date_time("2017-05-24T08:45"), to.be(["2017-05-24T08:45", "2017", "05", "24", "08:45"]));
  bad_time:     expect(p.date_time("yo 2017-05-24"),    to.be([]));

  nil_pattern:
    expect_error(
      () -> capturing(nil),
      to.have_code("NIL_ERROR")
    );

  invalid_pattern:
    expect_error(
      () -> capturing("[a"),
      to.have_code("ILLEGAL_ARGUMENT")
    );
}
import strings as s from 'std.tf';
import assert, expect, expect_error, to, describe, it, subject, before, after from "std/spec";

alias s.index_of as index_of;

library spec {
  spec:
    describe("index_of", [


  of_default:
    expect(index_of(), to.be_nil());

  of_nil:
    expect(index_of(nil, nil, nil), to.be_nil());

  of_x_nil:
    expect(index_of(nil, "o"), to.be_nil());

  of_sub_nil:
    expect(index_of("foo", nil), to.be_nil());

  of_index_nil:
    expect(index_of("foo", "o", nil), to.be_nil());

  simple_found:
    expect(index_of("foo", "o"), to.be(1));

  simple_found_start:
    expect(index_of("foo", "fo"), to.be(0));

  simple_not_found:
    expect(index_of("foo", "b"), to.be(-1));

  empty_found:
    expect(index_of("foo", ""), to.be(0));

  empty_found_in_empty:
    expect(index_of("", ""), to.be(0));

  found_at_index:
    expect(index_of("for", "o", 1), to.be(1));

  found_at_start_index:
    expect(index_of("for", "f", 0), to.be(0));

  found_after_index:
    expect(index_of("hello world", "o", 5), to.be(7));

  not_found_after_index:
    expect(index_of("hello world", "e", 5), to.be(-1));

  found_at_sub_zero_index:
    expect(index_of("for", "o", -99), to.be(1));

  not_found_at_past_end_index:
    expect(index_of("for", "o", 99), to.be(-1));

  found_at_last_index:
    expect(index_of("for", "r", 2), to.be(2));

  ]);
}
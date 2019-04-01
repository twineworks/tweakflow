import strings as s from 'std.tf';
import expect, expect_error, to from "std/assert.tf";

alias s.code_points as code_points;

library code_points_spec {

  of_default:
    expect(code_points(), to.be_nil());

  of_nil:
    expect(code_points(nil), to.be_nil());

  simple:
    expect(code_points("foo"), to.be([102, 111, 111]));

  of_empty:
    expect(code_points(""), to.be([]));

  of_code_points:
    expect(code_points("你好"), to.be([0x4F60, 0x597D]));

  of_code_points_beyond_bmp:
    expect(code_points("𝄞你好𝄞"), to.be([0x01D11E, 0x4F60, 0x597D, 0x01D11E]));

}
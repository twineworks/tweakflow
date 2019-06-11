import strings as s from 'std.tf';
import expect, expect_error, to from "std/assert.tf";

alias s.charsets as charsets;

library charsets_spec {

  of_default:
    expect(charsets(), to.contain_all(["UTF-8", "ISO-8859-1", "windows-1252"]));

}
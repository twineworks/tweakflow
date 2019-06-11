import strings as s from 'std.tf';
import expect, expect_error, to from "std/assert.tf";

alias s.from_bytes as from_bytes;

library from_bytes_spec {

  of_default:
    expect(from_bytes(), to.be_nil());

  of_nil:
    expect(from_bytes(nil), to.be_nil());

  empty:
    expect(from_bytes(0b), to.be(""));

  simple:
    expect(from_bytes(0b616263), to.be("abc"));

  diacritic_utf8:
    expect(from_bytes(0b62C3A4636B6572), to.be("bäcker"));

  diacritic_iso_8859_1:
    expect(from_bytes(0b62E4636B6572, "ISO-8859-1"), to.be("bäcker"));

  bmp:
    expect(from_bytes(0bE4BDA0E5A5BD), to.be("你好"));

  # unspecified endianness expects a BOM and defaults to big endian
  bmp_utf16:
    expect(from_bytes(0bFEFF4F60597D, "UTF-16"), to.be("你好")) &&
    expect(from_bytes(0bFFFE604F7D59, "UTF-16"), to.be("你好")) &&
    expect(from_bytes(0b4F60597D, "UTF-16"), to.be("你好"));

  bmp_utf16le:
    expect(from_bytes(0b604F7D59, "UTF-16LE"), to.be("你好"));

  bmp_utf16be:
    expect(from_bytes(0b4F60597D, "UTF-16BE"), to.be("你好"));

  beyond_bmp:
    expect(from_bytes(0bE4BDA0E5A5BDF09D849E), to.be("你好𝄞"));

  unknown_charset:
    expect_error(
      () -> from_bytes(0b, "fluffy"),
      to.have_code("ILLEGAL_ARGUMENT")
    );

}
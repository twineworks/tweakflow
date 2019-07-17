import strings as s from 'std.tf';
import assert, expect, expect_error, to, describe, it, subject, before, after from "std/spec";

alias s.to_bytes as to_bytes;

library spec {
  spec:
    describe("to_bytes", [


  of_default:
    expect(to_bytes(), to.be_nil());

  of_nil:
    expect(to_bytes(nil), to.be_nil());

  empty:
    expect(to_bytes(""), to.be(0b));

  simple:
    expect(to_bytes("abc"), to.be(0b616263));

  diacritic_utf8:
    expect(to_bytes("bäcker"), to.be(0b62C3A4636B6572));

  diacritic_iso_8859_1:
    expect(to_bytes("bäcker", "ISO-8859-1"), to.be(0b62E4636B6572));

  bmp:
    expect(to_bytes("你好"), to.be(0bE4BDA0E5A5BD));

  # unspecified endianness inserts a BOM and uses big-endian
  bmp_utf16:
    expect(to_bytes("你好", "UTF-16"), to.be(0bFEFF4F60597D));

  bmp_utf16le:
    expect(to_bytes("你好", "UTF-16LE"), to.be(0b604F7D59));

  bmp_utf16be:
    expect(to_bytes("你好", "UTF-16BE"), to.be(0b4F60597D));

  beyond_bmp:
    expect(to_bytes("你好𝄞"), to.be(0bE4BDA0E5A5BDF09D849E));

  unknown_charset:
    expect_error(
      () -> to_bytes("abc", "fluffy"),
      to.have_code("ILLEGAL_ARGUMENT")
    );

  ]);
}
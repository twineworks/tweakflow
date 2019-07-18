import strings as s from 'std.tf';
import assert, expect, expect_error, to, describe, it, subject, before, after from "std/spec";

alias s.from_bytes as from_bytes;

library spec {
  spec:
    describe("strings.from_bytes", [

      it("of_default", () ->
        expect(from_bytes(), to.be_nil())
      ),

      it("of_nil", () ->
        expect(from_bytes(nil), to.be_nil())
      ),

      it("empty", () ->
        expect(from_bytes(0b), to.be(""))
      ),

      it("simple", () ->
        expect(from_bytes(0b616263), to.be("abc"))
      ),

      it("diacritic_utf8", () ->
        expect(from_bytes(0b62C3A4636B6572), to.be("bäcker"))
      ),

      it("diacritic_iso_8859_1", () ->
        expect(from_bytes(0b62E4636B6572, "ISO-8859-1"), to.be("bäcker"))
      ),

      it("bmp", () ->
        expect(from_bytes(0bE4BDA0E5A5BD), to.be("你好"))
      ),

      # unspecified endianness expects a BOM and defaults to big endian
      it("bmp_utf16", () ->
        expect(from_bytes(0bFEFF4F60597D, "UTF-16"), to.be("你好")) &&
        expect(from_bytes(0bFFFE604F7D59, "UTF-16"), to.be("你好")) &&
        expect(from_bytes(0b4F60597D, "UTF-16"), to.be("你好"))
      ),

      it("bmp_utf16le", () ->
        expect(from_bytes(0b604F7D59, "UTF-16LE"), to.be("你好"))
      ),

      it("bmp_utf16be", () ->
        expect(from_bytes(0b4F60597D, "UTF-16BE"), to.be("你好"))
      ),

      it("beyond_bmp", () ->
        expect(from_bytes(0bE4BDA0E5A5BDF09D849E), to.be("你好𝄞"))
      ),

      it("unknown_charset", () ->
        expect_error(
          () -> from_bytes(0b, "fluffy"),
          to.have_code("ILLEGAL_ARGUMENT")
        )
      ),

  ]);
}
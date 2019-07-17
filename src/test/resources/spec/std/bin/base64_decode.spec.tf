
import core, data, bin, strings from "std";
import assert, expect, expect_error, to, describe, it, subject, before, after from "std/spec";

alias bin.base64_decode as base64_decode;

library spec {

  spec:
    describe("bin.base64_decode", [

      it("decodes nil", () ->
        expect(base64_decode(nil), to.be_nil())
      ),
      
      it("of_empty", () ->
        expect(base64_decode(""), to.be(0b))
      ),

      it("of_0b00", () ->
        expect(base64_decode("AA=="), to.be(0b00))
      ),

      it("of_hello_world", () ->
        expect(base64_decode("SGVsbG8gV29ybGQh"), to.be(strings.to_bytes("Hello World!")))
      ),

      it("of_basic", () ->
        expect(base64_decode("z9vs/d7/"), to.be(0bcfdbecfddeff))
      ),

      it("of_url_safe", () ->
        expect(base64_decode("z9vs_d7_", 'url'), to.be(0bcfdbecfddeff))
      ),

      it ("of_mime", () ->
        let {
          text:
~~~
Man is distinguished, not only by his reason, but by this singular passion from other animals,
which is a lust of the mind, that by a perseverance of delight in the continued and indefatigable
generation of knowledge, exceeds the short vehemence of any carnal pleasure.
~~~
;
          bytes: strings.to_bytes(text);
          encoded:
            "TWFuIGlzIGRpc3Rpbmd1aXNoZWQsIG5vdCBvbmx5IGJ5IGhpcyByZWFzb24sIGJ1dCBieSB0aGlz\r\n" ..
            "IHNpbmd1bGFyIHBhc3Npb24gZnJvbSBvdGhlciBhbmltYWxzLAp3aGljaCBpcyBhIGx1c3Qgb2Yg\r\n" ..
            "dGhlIG1pbmQsIHRoYXQgYnkgYSBwZXJzZXZlcmFuY2Ugb2YgZGVsaWdodCBpbiB0aGUgY29udGlu\r\n" ..
            "dWVkIGFuZCBpbmRlZmF0aWdhYmxlCmdlbmVyYXRpb24gb2Yga25vd2xlZGdlLCBleGNlZWRzIHRo\r\n" ..
            "ZSBzaG9ydCB2ZWhlbWVuY2Ugb2YgYW55IGNhcm5hbCBwbGVhc3VyZS4=";
        }
        expect(base64_decode(encoded, 'mime'), to.be(bytes))
      ),

      it("of_invalid_input_length", () ->
        expect_error(
          () -> base64_decode("z9vs/d7/1"),
          to.have_code("ILLEGAL_ARGUMENT")
        )
      ),

      it("of_invalid_input_char", () ->
        expect_error(
          () -> base64_decode("ÄÄ=="),
          to.have_code("ILLEGAL_ARGUMENT")
        )
      ),

      it("of_invalid_variant", () ->
        expect_error(
          () -> base64_decode("AA==", "fluffy"),
          to.have_code("ILLEGAL_ARGUMENT")
        )
      ),

    ]);

}
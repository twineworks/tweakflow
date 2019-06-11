import bin, strings from 'std.tf';
import expect, expect_error, to from "std/assert.tf";

alias bin.base64_encode as base64_encode;

library base64_encode_spec {

  of_nil:
    expect(base64_encode(nil), to.be_nil());

  of_empty:
    expect(base64_encode(0b), to.be(""));

  of_0b00:
    expect(base64_encode(0b00), to.be("AA=="));

  of_hello_world:
    expect(base64_encode(strings.to_bytes("Hello World!")), to.be("SGVsbG8gV29ybGQh"));

  of_basic:
    expect(base64_encode(0bcfdbecfddeff), to.be("z9vs/d7/"));

  of_url_safe:
    expect(base64_encode(0bcfdbecfddeff, 'url'), to.be("z9vs_d7_"));

  of_mime:
    let {
      text:
~~~
Man is distinguished, not only by his reason, but by this singular passion from other animals,
which is a lust of the mind, that by a perseverance of delight in the continued and indefatigable
generation of knowledge, exceeds the short vehemence of any carnal pleasure.
~~~
;
      bytes: strings.to_bytes(text);
      expected:
"TWFuIGlzIGRpc3Rpbmd1aXNoZWQsIG5vdCBvbmx5IGJ5IGhpcyByZWFzb24sIGJ1dCBieSB0aGlz\r\n" ..
"IHNpbmd1bGFyIHBhc3Npb24gZnJvbSBvdGhlciBhbmltYWxzLAp3aGljaCBpcyBhIGx1c3Qgb2Yg\r\n" ..
"dGhlIG1pbmQsIHRoYXQgYnkgYSBwZXJzZXZlcmFuY2Ugb2YgZGVsaWdodCBpbiB0aGUgY29udGlu\r\n" ..
"dWVkIGFuZCBpbmRlZmF0aWdhYmxlCmdlbmVyYXRpb24gb2Yga25vd2xlZGdlLCBleGNlZWRzIHRo\r\n" ..
"ZSBzaG9ydCB2ZWhlbWVuY2Ugb2YgYW55IGNhcm5hbCBwbGVhc3VyZS4=";
    }
    expect(base64_encode(bytes, 'mime'), to.be(expected));

  of_invalid_variant:
    expect_error(
      () -> base64_encode(0b00010203, "fluffy"),
      to.have_code("ILLEGAL_ARGUMENT")
    );

}
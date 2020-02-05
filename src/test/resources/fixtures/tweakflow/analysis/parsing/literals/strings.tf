library lib
{
  single_quoted_empty: '';
  double_quoted_empty: "";
  single_quoted: 'single quoted';
  double_quoted: "double quoted";
  escape_sequence_newline: "-\n-";
  escape_sequence_backslash: "string with \\ backslash";
  escape_sequence_double_quote: "string with \" double quote";
  escape_sequence_tab: "string with \t tab";
  escape_sequence_cr: "string with \r carriage return";
  escape_sequence_bmp: "string with \u2287 superset";
  escape_sequence_unicode: "string with \U0001d11e clef";
  escape_sequence_mixed: "\\\t\r\n\u2287\"\U0001d11e";
  with_hash: "string with # hash";
  with_serial_interpolation: "### #{name}{##{id}}";
  with_serial_interpolation_expected: "### "..(name as string).."{#"..(id as string).."}";
  with_hash_at_end: "string with #";
  with_escaped_interpolation: "string with \#{hash}";
  with_interpolation: "string with #{hash}";
  with_interpolation_expected: "string with "..(hash as string);
  with_nested_interpolation: "string with #{"name: #{name}"}";
  with_nested_interpolation_expected: "string with "..("name: "..(name as string));
  single_escaped: 'single quoted ''string''';
  single_multi_line: 'single quoted
multi
line
string';
  double_multi_line: "double quoted
multi
line
string";
  here_doc:
~~~
Here ~~~ String
~~~;
  key: :key;
  quoted_key: :`quoted key`;
  digit_key: :123;
  dash_key: :content-type;
  plus_key: :+and+;
  slash_key: :/slash/;
  dot_key: :user.home;
}

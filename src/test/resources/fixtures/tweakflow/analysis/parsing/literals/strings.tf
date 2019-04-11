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
  with_escaped_interpolation: "string with \#{hash}";
  with_interpolation: "string with #{hash}";
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

}
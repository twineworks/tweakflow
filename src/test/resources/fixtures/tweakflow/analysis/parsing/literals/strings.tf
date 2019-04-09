library lib
{
  single_quoted_empty: '';
  double_quoted_empty: "";
  single_quoted: 'single quoted';
  double_quoted: "double quoted";
  escape_sequence_1: "-\n-";
  escape_sequence_2: "string with\nescape sequence";
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
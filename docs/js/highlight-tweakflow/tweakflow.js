// MIT License
//
// Copyright (c) 2019 Twineworks GmbH
//
// Permission is hereby granted, free of charge, to any person obtaining a copy
// of this software and associated documentation files (the "Software"), to deal
// in the Software without restriction, including without limitation the rights
// to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
// copies of the Software, and to permit persons to whom the Software is
// furnished to do so, subject to the following conditions:
//
// The above copyright notice and this permission notice shall be included in all
// copies or substantial portions of the Software.
//
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
// AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
// OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
// SOFTWARE.

var module = module ? module : {};

function hljsDefineTweakflow(hljs) {

  const CORE_KEYWORDS = [

    "global",
    "library",
    "module",

    "interactive",
    "in_scope",

    "import",
    "export",
    "as",
    "from",
    "alias",

    "meta",
    "doc",
    "via",

    "not",
    "is",
    "if",
    "then",
    "else",
    "for",
    "try",
    "catch",
    "throw",
    "let",
    "debug",
    "typeof",
    "default",
    "match",

    "provided"
  ];

  const DATA_TYPES = [    // data types
    "function",
    "string",
    "boolean",
    "long",
    "dict",
    "list",
    "double",
    "datetime",
    "any",
    "void"
  ];

  const LITERALS = ["true", "false", "nil", "NaN", "Infinity"];

  const KEYWORDS = {
    literal: LITERALS.join(" "),
    keyword: CORE_KEYWORDS.join(" "),
    type: DATA_TYPES.join(" ")
  };

  const BOUNDARY_RE = "(^|\\b|[ ])";

  const LONG_RE = "[+\\-]?[0-9]+";
  const HEX_RE = "0x[0-9a-fA-F]+";
  const DOUBLE_RE = "[+\\-]?((NaN)|(Infinity)|([0-9]+(\\.[0-9]+)?([eE][-+]?[0-9]+)?)|\\.[0-9]+([eE][-+]?[0-9]+)?)";
  const NUMBER_RE = `${BOUNDARY_RE}((${HEX_RE})|(${DOUBLE_RE}|(${LONG_RE})))`;

  const ID_RE = "[a-zA-Z_][a-zA-Z0-9?_]*";
  const ID_ESCAPED_RE = "`.+?`";
  const IDENTIFIER_RE = `((${ID_RE})|(${ID_ESCAPED_RE}))`;

  // const DATETIME_RE_ = /(^|\b|[ ])([+\-])?\d+-\d+-\d+T((\d+:\d+:\d+(\.\d+)?)(Z|([+\-]\d+:\d+))?(@([a-zA-Z_]+))?)?/

  const OPTIONAL_SIGN_RE = "([+\\-])?";
  const DATE_RE = "\\d+-\\d+-\\d+T";
  const TIME_RE = "\\d+:\\d+:\\d+(\\.\\d+)?";
  const OFFSET_RE = "Z|([+\\-]\\d+:\\d+)";
  const TIMEZONE_ID_RE = "[a-zA-Z_]+(/[a-zA-Z_0-9?]+)+";

  const TZ_RE = `@((${TIMEZONE_ID_RE})|(${ID_RE})|(${ID_ESCAPED_RE}))`;

  const DATETIME_RE = `${BOUNDARY_RE}${OPTIONAL_SIGN_RE}${DATE_RE}(${TIME_RE}(${OFFSET_RE})?(${TZ_RE})?)?`;

  const COMMENT_MODES = [
    hljs.HASH_COMMENT_MODE,
    hljs.C_BLOCK_COMMENT_MODE
  ];

  const PROMPT_MODE = {
    className: 'meta',
    begin: "^(.*\\.tf)?>"
  };

  const PROMPT_COMMANDS_MODE = {
    begin: "\\\\((load)|(meta)|(doc))"
  };

  const REPL_MULTILINE_ESCAPE_MODE = {
    className: 'meta',
    begin: "\\\\e"
  };

  const NUMBER_MODE = {
    className: 'number',
    begin: NUMBER_RE
  };

  const DATETIME_MODE = {
    className: 'number',
    begin: DATETIME_RE
  };

  const ERROR_MODE = {
    className: 'meta',
    begin: "^ERROR:",
    end: "\n\n"
  };

  const DEBUG_MODE = {
    className: 'meta',
    begin: "^DEBUG:",
    end: "$"
  };

  const SYMBOL_MODE = {
    className: "symbol",
    begin: ":" + IDENTIFIER_RE
  };

  const SINGLE_QUOTED_STRING_MODE = {
    className: "string",
    begin: "'",
    end: "'",
    contains: [
      {begin: "''"}
    ]
  };

  const HEREDOC_STRING_MODE = {
    className: "string",
    begin: "~~~\n",
    end: "\n~~~",
  };

  const CAPTURE_PATTERN = {
    className: "regexp",
    begin: `@(\\.\\.\\.)?${IDENTIFIER_RE}?`
  };

  const LIBRARY_MODE = {
    className: 'class',
    begin: 'library ', end: /[{]/, excludeEnd: true,
    keywords: KEYWORDS,
    contains: [
      hljs.UNDERSCORE_TITLE_MODE
    ]
  };

  const DEF_MODE = {
    // className: 'variable',
    className: 'title',
    begin: IDENTIFIER_RE+":"
  };

  const PREFIXED_REFERENCE_MODE = {
    // className: 'variable',
    // className: 'variable',
    begin: `((\\$)|(global::)|(module::)|(library::)|(::))${IDENTIFIER_RE}`
  };

  const REFERENCE_MODE = {
    // className: 'variable',
    // className: 'variable',
    keywords: KEYWORDS,
    begin: IDENTIFIER_RE
  };

  const REF_FREE_KEYWORDS = {
    className: 'keyword',
    begin: `(global)|(library)|(module)`
  };

  const CODE = {
      case_insensitive: false,
      className: "tweakflow",
      lexemes: IDENTIFIER_RE,
      keywords: KEYWORDS,
      contains: [
        PROMPT_MODE,
        PROMPT_COMMANDS_MODE,
        REPL_MULTILINE_ESCAPE_MODE,
        ERROR_MODE,
        DEBUG_MODE,
        PREFIXED_REFERENCE_MODE,
        LIBRARY_MODE,
        DEF_MODE,
        DATETIME_MODE,
        NUMBER_MODE,
        SYMBOL_MODE,
        SINGLE_QUOTED_STRING_MODE,
        hljs.QUOTE_STRING_MODE,
        HEREDOC_STRING_MODE,
        CAPTURE_PATTERN,
        REFERENCE_MODE,
      ].concat(COMMENT_MODES)
    };

  return CODE;
}

module.exports = function(hljs) {
  hljs.registerLanguage("tweakflow", hljsDefineTweakflow);
};

module.exports.definer = hljsDefineTweakflow;

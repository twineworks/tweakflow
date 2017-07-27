# The MIT License (MIT)
#
# Copyright (c) 2017 Twineworks GmbH
#
# Permission is hereby granted, free of charge, to any person obtaining a copy
# of this software and associated documentation files (the "Software"), to deal
# in the Software without restriction, including without limitation the rights
# to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
# copies of the Software, and to permit persons to whom the Software is
# furnished to do so, subject to the following conditions:
#
# The above copyright notice and this permission notice shall be included in all
# copies or substantial portions of the Software.
#
# THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
# IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
# FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
# AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
# LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
# OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
# SOFTWARE.

import core, data, strings from "std"

alias data.map as map
alias strings.join as join
alias core.inspect as inspect

library transform {

  transform: (dict x) -> string
    match x[:node]
      'module'  -> module_to_markdown(x)
      'library' -> lib_to_markdown(x)
      'var'     -> var_to_markdown(x)
      default   -> core.inspect(x)

  module_to_markdown: (dict x) -> string
    "---\n"
    .."title: "..x[:file].."\n"
    .."---\n\n"

    ..'# module '..x[:file].."\n\n"
    ..doc_fragment(x)
    ..meta_fragment(x)
    ..join(map(x[:components], transform)).."\n"

  lib_to_markdown: (dict x) -> string
    '## library '..x[:name].."\n\n"
    ..doc_fragment(x)
    ..meta_fragment(x)
    ..join(map(x[:vars], transform)).."\n"

  var_to_markdown: (dict x) -> string
    '### '..x[:name].."\n\n"
    ..doc_fragment(x)
    ..meta_fragment(x)
    # ..source_fragment(x)

  doc_fragment: (x) -> string
    (if x[:doc] != nil then x[:doc].."\n\n" else "")

  meta_fragment: (x) -> string
    (if x[:meta] != nil then "`````ruby\n"..inspect(x[:meta]).."\n`````".."\n\n" else "")

  source_fragment: (x) -> string
    "<div class=\"source\">\n"
    .."`````\n"..x[:source_code].."\n`````".."\n"
    .."</div>\n\n"


}
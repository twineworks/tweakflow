# The MIT License (MIT)
#
# Copyright (c) 2019 Twineworks GmbH
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

import core, data, strings from "std";

alias data.map as map;
alias data.flatmap as flatmap;
alias strings.join as join;
alias core.inspect as inspect;
alias strings as s;

library util {

  trim_trailing: (string x, string tail) -> string
  	let {
      lower_x: s.lower_case(x);
      lower_tail: s.lower_case(tail);
  	}
  	if s.ends_with?(lower_x, lower_tail)
  	  s.substring(x, 0, s.last_index_of(lower_x, lower_tail))
    else
      x;
}

library fragments {

  for_node: (dict x, list parents=[]) -> list
    match x[:node]
      'module'  -> module_fragments(x),
      'library' -> library_fragments(x, parents),
      'var'     -> var_fragments(x, parents),
      default   -> [core.inspect(x)];

  children: (dict x) -> list
  	match x[:node]
      'module'  -> x[:components],
      'library' -> x[:vars],
  	  default   -> [];

  child_fragments: (dict x, list parents=[]) -> list
  	let {
      sub_fragments: for_node(parents=[x, ...parents]);
  	}
	flatmap(children(x), sub_fragments);

  module_fragments: (dict x) -> list
  	let {
      prologue: "---\n"
                .."title: "..x[:file].."\n"
                .."---\n\n";
  	}
  	[
      prologue,
      title_fragment(x),
      doc_fragment(x),
      meta_fragment(x),
      ...child_fragments(x)
    ];

  library_fragments: (dict x, list parents) -> list
  	[
      title_fragment(x, parents),
      doc_fragment(x, parents),
      meta_fragment(x, parents),
      ...child_fragments(x, parents)
    ];

  var_fragments: (dict x, list parents) -> list
  	[
      title_fragment(x, parents),
      doc_fragment(x, parents),
      meta_fragment(x, parents)
    ];

  title_fragment: (dict x, list parents=[]) -> string
    let {
      id: '{#'..title_id(x, parents) ..'}';
  	}
  	match x[:node]
  	  'var'      -> '### '        .. x[:name] .. id .. "\n\n",
  	  'library'  -> '## library ' .. x[:name] .. id .. "\n\n",
  	  'module'   -> '# module '   .. x[:file] .. id .. "\n\n",
  	  'default'  -> '';

  title_id: (dict x, list parents=[]) -> string
	match x[:node]
 	  "var"      -> parents[0, :name] .. '-' .. x[:name],
      "module"   -> util.trim_trailing(x[:file], ".tf"),
      "library"  -> x[:name];

  doc_fragment: (dict x, list parents=[]) -> string
    (if x[:doc] != nil then x[:doc].."\n\n" else "");

  meta_fragment: (dict x, list parents=[]) -> string
  	let {

      id: title_id(x, parents);

      type: x[:node];

      name: match x[:node]
              'module' -> x[:file],
              default  -> x[:name];

      tags: if x[:node] == "var"
              parents[0, :name]
            else
              "";
  	}
  	"\n\n<div
      data-meta='true'
      data-meta-id='#{id}'
      data-meta-type='#{type}'
      data-meta-name='#{name}'
	  data-meta-tags='#{tags}'
    ></div>\n\n";

}

library transform {

  transform: (dict x) -> string
	join(fragments.for_node(x));

}
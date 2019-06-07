/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2019 Twineworks GmbH
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.twineworks.tweakflow.lang.parse.builders;

import com.twineworks.tweakflow.lang.ast.expressions.ExpressionNode;
import com.twineworks.tweakflow.lang.ast.expressions.NilNode;
import com.twineworks.tweakflow.lang.ast.expressions.StringNode;
import com.twineworks.tweakflow.lang.load.loadpath.MemoryLocation;
import com.twineworks.tweakflow.lang.parse.SourceInfo;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class ExpressionBuilderTest {

  private ExpressionBuilder makeBuilder(){
    return new ExpressionBuilder(new MemoryLocation.Builder().add( "", "").build().getParseUnit(""));
  }

  @Test
  public void compacts_string_nodes__empty() throws Exception {

    ExpressionBuilder x = makeBuilder();
    List<ExpressionNode> list = new ArrayList<>();
    List<ExpressionNode> out = x.compactStringNodes(list);

    assertThat(out).hasSize(0);
  }

  @Test
  public void compacts_string_nodes__s() throws Exception {

    ExpressionBuilder x = makeBuilder();

    List<ExpressionNode> list = new ArrayList<>();
    StringNode a = new StringNode("a");
    a.setSourceInfo(new SourceInfo(null, 1, 0, 0, 0));
    list.add(a);

    List<ExpressionNode> out = x.compactStringNodes(list);

    assertThat(out).hasSize(1);
    assertThat(out.get(0)).isNotSameAs(a);
    assertThat(a.getStringVal()).isEqualTo("a");
    assertThat(a.getSourceInfo().getLine()).isEqualTo(1);

  }

  @Test
  public void compacts_string_nodes__n() throws Exception {

    ExpressionBuilder x = makeBuilder();

    List<ExpressionNode> list = new ArrayList<>();
    NilNode a = new NilNode();
    a.setSourceInfo(new SourceInfo(null, 1, 0, 0, 0));
    list.add(a);

    List<ExpressionNode> out = x.compactStringNodes(list);

    assertThat(out).hasSize(1);
    assertThat(out.get(0)).isSameAs(a);

  }


  @Test
  public void compacts_string_nodes__ss() throws Exception {

    ExpressionBuilder x = makeBuilder();

    List<ExpressionNode> list = new ArrayList<>();
    ExpressionNode a = new StringNode("a").setSourceInfo(new SourceInfo(null, 1, 0, 0, 0));
    ExpressionNode b = new StringNode("b").setSourceInfo(new SourceInfo(null, 2, 0, 0, 0));
    list.add(a);
    list.add(b);

    List<ExpressionNode> out = x.compactStringNodes(list);

    assertThat(out).hasSize(1);
    StringNode r = (StringNode) out.get(0);
    assertThat(r.getStringVal()).isEqualTo("ab");
    assertThat(r.getSourceInfo().getLine()).isEqualTo(1);

  }

  @Test
  public void compacts_string_nodes__sn() throws Exception {

    ExpressionBuilder x = makeBuilder();

    List<ExpressionNode> list = new ArrayList<>();
    ExpressionNode a = new StringNode("a").setSourceInfo(new SourceInfo(null, 1, 0, 0, 0));
    ExpressionNode b = new NilNode().setSourceInfo(new SourceInfo(null, 2, 0, 0, 0));

    list.add(a);
    list.add(b);

    List<ExpressionNode> out = x.compactStringNodes(list);

    assertThat(out).hasSize(2);

    StringNode r1 = (StringNode) out.get(0);
    assertThat(r1.getStringVal()).isEqualTo("a");
    assertThat(r1.getSourceInfo().getLine()).isEqualTo(1);

    NilNode r2 = (NilNode) out.get(1);
    assertThat(r2.getSourceInfo().getLine()).isEqualTo(2);

  }

  @Test
  public void compacts_string_nodes__ns() throws Exception {

    ExpressionBuilder x = makeBuilder();

    List<ExpressionNode> list = new ArrayList<>();
    ExpressionNode a = new NilNode().setSourceInfo(new SourceInfo(null, 1, 0, 0, 0));
    ExpressionNode b = new StringNode("a").setSourceInfo(new SourceInfo(null, 2, 0, 0, 0));

    list.add(a);
    list.add(b);

    List<ExpressionNode> out = x.compactStringNodes(list);

    assertThat(out).hasSize(2);

    NilNode r1 = (NilNode) out.get(0);
    assertThat(r1.getSourceInfo().getLine()).isEqualTo(1);

    StringNode r2 = (StringNode) out.get(1);
    assertThat(r2.getStringVal()).isEqualTo("a");
    assertThat(r2.getSourceInfo().getLine()).isEqualTo(2);

  }



  @Test
  public void compacts_string_nodes__nn() throws Exception {

    ExpressionBuilder x = makeBuilder();

    List<ExpressionNode> list = new ArrayList<>();
    ExpressionNode a = new NilNode().setSourceInfo(new SourceInfo(null, 1, 0, 0, 0));
    ExpressionNode b = new NilNode().setSourceInfo(new SourceInfo(null, 2, 0, 0, 0));

    list.add(a);
    list.add(b);

    List<ExpressionNode> out = x.compactStringNodes(list);

    assertThat(out).hasSize(2);

    NilNode r1 = (NilNode) out.get(0);
    assertThat(r1.getSourceInfo().getLine()).isEqualTo(1);

    NilNode r2 = (NilNode) out.get(1);
    assertThat(r2.getSourceInfo().getLine()).isEqualTo(2);

  }


  @Test
  public void compacts_string_nodes__sns() throws Exception {

    ExpressionBuilder x = makeBuilder();

    List<ExpressionNode> list = new ArrayList<>();
    ExpressionNode a = new StringNode("a").setSourceInfo(new SourceInfo(null, 1, 0, 0, 0));
    ExpressionNode b = new NilNode().setSourceInfo(new SourceInfo(null, 2, 0, 0, 0));
    ExpressionNode c = new StringNode("c").setSourceInfo(new SourceInfo(null, 3, 0, 0, 0));

    list.add(a);
    list.add(b);
    list.add(c);

    List<ExpressionNode> out = x.compactStringNodes(list);

    assertThat(out).hasSize(3);

    StringNode r1 = (StringNode) out.get(0);
    assertThat(r1.getStringVal()).isEqualTo("a");
    assertThat(r1.getSourceInfo().getLine()).isEqualTo(1);

    NilNode r2 = (NilNode) out.get(1);
    assertThat(r2.getSourceInfo().getLine()).isEqualTo(2);

    StringNode r3 = (StringNode) out.get(2);
    assertThat(r3.getStringVal()).isEqualTo("c");
    assertThat(r3.getSourceInfo().getLine()).isEqualTo(3);

  }

  @Test
  public void compacts_string_nodes__snss() throws Exception {

    ExpressionBuilder x = makeBuilder();

    List<ExpressionNode> list = new ArrayList<>();
    ExpressionNode a = new StringNode("a").setSourceInfo(new SourceInfo(null, 1, 0, 0, 0));
    ExpressionNode b = new NilNode().setSourceInfo(new SourceInfo(null, 2, 0,  0, 0));
    ExpressionNode c = new StringNode("c").setSourceInfo(new SourceInfo(null, 3, 0, 0, 0));
    ExpressionNode d = new StringNode("d").setSourceInfo(new SourceInfo(null, 4, 0, 0, 0));

    list.add(a);
    list.add(b);
    list.add(c);
    list.add(d);

    List<ExpressionNode> out = x.compactStringNodes(list);

    assertThat(out).hasSize(3);

    StringNode r1 = (StringNode) out.get(0);
    assertThat(r1.getStringVal()).isEqualTo("a");
    assertThat(r1.getSourceInfo().getLine()).isEqualTo(1);

    NilNode r2 = (NilNode) out.get(1);
    assertThat(r2.getSourceInfo().getLine()).isEqualTo(2);

    StringNode r3 = (StringNode) out.get(2);
    assertThat(r3.getStringVal()).isEqualTo("cd");
    assertThat(r3.getSourceInfo().getLine()).isEqualTo(3);

  }

  @Test
  public void compacts_string_nodes__ssnss() throws Exception {

    ExpressionBuilder x = makeBuilder();

    List<ExpressionNode> list = new ArrayList<>();
    ExpressionNode a = new StringNode("a").setSourceInfo(new SourceInfo(null, 1, 0, 0, 0));
    ExpressionNode b = new StringNode("b").setSourceInfo(new SourceInfo(null, 2, 0, 0, 0));
    ExpressionNode n = new NilNode().setSourceInfo(new SourceInfo(null, 3, 0, 0, 0));
    ExpressionNode c = new StringNode("c").setSourceInfo(new SourceInfo(null, 4, 0, 0, 0));
    ExpressionNode d = new StringNode("d").setSourceInfo(new SourceInfo(null, 5, 0, 0, 0));

    list.add(a);
    list.add(b);
    list.add(n);
    list.add(c);
    list.add(d);

    List<ExpressionNode> out = x.compactStringNodes(list);

    assertThat(out).hasSize(3);

    StringNode r1 = (StringNode) out.get(0);
    assertThat(r1.getStringVal()).isEqualTo("ab");
    assertThat(r1.getSourceInfo().getLine()).isEqualTo(1);

    NilNode r2 = (NilNode) out.get(1);
    assertThat(r2.getSourceInfo().getLine()).isEqualTo(3);

    StringNode r3 = (StringNode) out.get(2);
    assertThat(r3.getStringVal()).isEqualTo("cd");
    assertThat(r3.getSourceInfo().getLine()).isEqualTo(4);

  }


  @Test
  public void compacts_string_nodes__nssn() throws Exception {

    ExpressionBuilder x = makeBuilder();

    List<ExpressionNode> list = new ArrayList<>();
    ExpressionNode n = new NilNode().setSourceInfo(new SourceInfo(null, 1, 0, 0, 0));
    ExpressionNode a = new StringNode("a").setSourceInfo(new SourceInfo(null, 2, 0, 0, 0));
    ExpressionNode b = new StringNode("b").setSourceInfo(new SourceInfo(null, 3, 0, 0, 0));
    ExpressionNode m = new NilNode().setSourceInfo(new SourceInfo(null, 4, 0, 0, 0));

    list.add(n);
    list.add(a);
    list.add(b);
    list.add(m);

    List<ExpressionNode> out = x.compactStringNodes(list);

    assertThat(out).hasSize(3);

    NilNode r1 = (NilNode) out.get(0);
    assertThat(r1.getSourceInfo().getLine()).isEqualTo(1);

    StringNode r2 = (StringNode) out.get(1);
    assertThat(r2.getStringVal()).isEqualTo("ab");
    assertThat(r2.getSourceInfo().getLine()).isEqualTo(2);

    NilNode r3 = (NilNode) out.get(2);
    assertThat(r3.getSourceInfo().getLine()).isEqualTo(4);

  }


  @Test
  public void supports_newline_escape() throws Exception {

    ExpressionBuilder x = makeBuilder();
    assertThat(x.convertEscapeSequence("\\n")).isEqualTo("\n");

  }


  @Test
  public void supports_return_escape() throws Exception {

    ExpressionBuilder x = makeBuilder();
    assertThat(x.convertEscapeSequence("\\r")).isEqualTo("\r");

  }

  @Test
  public void supports_tab_escape() throws Exception {

    ExpressionBuilder x = makeBuilder();
    assertThat(x.convertEscapeSequence("\\t")).isEqualTo("\t");

  }

  @Test
  public void supports_hash_escape() throws Exception {

    ExpressionBuilder x = makeBuilder();
    assertThat(x.convertEscapeSequence("\\#{")).isEqualTo("#{");

  }

  @Test
  public void supports_2_byte_unicode_escape() throws Exception {

    ExpressionBuilder x = makeBuilder();
    assertThat(x.convertEscapeSequence("\\u0040")).isEqualTo("@");

  }

  @Test
  public void supports_4_byte_unicode_escape() throws Exception {

    ExpressionBuilder x = makeBuilder();
    // treble clef
    // see: http://www.fileformat.info/info/unicode/char/1d11e/index.htm
    assertThat(x.convertEscapeSequence("\\U0001D11E")).isEqualTo("\uD834\uDD1E");

  }

  @Test
  public void supports_hex_literal_1_byte() throws Exception {
    ExpressionBuilder x = makeBuilder();
    assertThat(x.parseHexLiteral("0x00")).isEqualTo(0L);
    assertThat(x.parseHexLiteral("0x01")).isEqualTo(1L);
    assertThat(x.parseHexLiteral("0xFF")).isEqualTo(255L);
  }

  @Test
  public void supports_hex_literal_2_byte() throws Exception {
    ExpressionBuilder x = makeBuilder();
    assertThat(x.parseHexLiteral("0x0000")).isEqualTo(0L);
    assertThat(x.parseHexLiteral("0x0001")).isEqualTo(1L);
    assertThat(x.parseHexLiteral("0xFFFF")).isEqualTo(256*256-1);
  }

  @Test
  public void supports_hex_literal_4_byte() throws Exception {
    ExpressionBuilder x = makeBuilder();
    assertThat(x.parseHexLiteral("0x00000000")).isEqualTo(0L);
    assertThat(x.parseHexLiteral("0x00000001")).isEqualTo(1L);
    assertThat(x.parseHexLiteral("0xFFFFFFFF")).isEqualTo(256L*256L*256L*256L-1L);
  }

  @Test
  public void supports_hex_literal_8_byte() throws Exception {
    ExpressionBuilder x = makeBuilder();
    assertThat(x.parseHexLiteral("0x0000000000000000")).isEqualTo(0L);
    assertThat(x.parseHexLiteral("0x0000000000000001")).isEqualTo(1L);
    assertThat(x.parseHexLiteral("0x7FFFFFFFFFFFFFFF")).isEqualTo(Long.MAX_VALUE);
    assertThat(x.parseHexLiteral("0x8000000000000000")).isEqualTo(Long.MIN_VALUE);
    assertThat(x.parseHexLiteral("0xFFFFFFFFFFFFFFFF")).isEqualTo(-1L);
    assertThat(x.parseHexLiteral("0xFFFFFFFFFFFFFFFE")).isEqualTo(-2L);
  }

  @Test
  public void supports_bin_literal() throws Exception {
    ExpressionBuilder x = makeBuilder();
    assertThat(x.parseBinLiteral("0b")).hasSize(0);
    assertThat(x.parseBinLiteral("0b01")).isEqualTo(new byte[] {1});
    assertThat(x.parseBinLiteral("0b0102030405060708")).isEqualTo(new byte[] {1,2,3,4,5, 6,7,8});
    assertThat(x.parseBinLiteral("0bFF")).isEqualTo(new byte[] {-1});
    assertThat(x.parseBinLiteral("0bfF")).isEqualTo(new byte[] {-1});
    assertThat(x.parseBinLiteral("0bFFFFFFFFFF")).isEqualTo(new byte[] {-1, -1, -1, -1, -1});
  }


}
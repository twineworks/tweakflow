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

package com.twineworks.tweakflow.spec.nodes;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class NodeLocationTest {

  @Test
  void generates_location_from_line_and_char() {

    String at = "/library/steps/decision/decision/decision.spec.tf:12:23";
    NodeLocation loc = NodeLocation.at(at);
    assertThat(loc.file).isEqualTo("/library/steps/decision/decision/decision.spec.tf");
    assertThat(loc.line).isEqualTo(12);
    assertThat(loc.charInLine).isEqualTo(23);

  }

  @Test
  void generates_location_from_line_only() {

    String at = "/library/steps/decision/decision/decision.spec.tf:12";
    NodeLocation loc = NodeLocation.at(at);
    assertThat(loc.file).isEqualTo("/library/steps/decision/decision/decision.spec.tf");
    assertThat(loc.line).isEqualTo(12);
    assertThat(loc.charInLine).isEqualTo(0);

  }

  @Test
  void generates_location_from_file_only() {

    String at = "/library/steps/decision/decision/decision.spec.tf";
    NodeLocation loc = NodeLocation.at(at);
    assertThat(loc.file).isEqualTo("/library/steps/decision/decision/decision.spec.tf");
    assertThat(loc.line).isEqualTo(0);
    assertThat(loc.charInLine).isEqualTo(0);

  }

}
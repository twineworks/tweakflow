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

package com.twineworks.tweakflow.io;

import com.twineworks.tweakflow.lang.TweakFlow;
import com.twineworks.tweakflow.lang.values.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.math.BigDecimal;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;


class OutTest {

  static final Path scratchFile = Paths.get("target/test-scratch/out.bin").toAbsolutePath().normalize();
  FileChannel w;
  FileChannel r;

  @BeforeEach
  void clearScratch() throws Exception {
    File f = scratchFile.toFile();
    Files.createDirectories(scratchFile.getParent());
    if (f.exists()){
      if (!f.delete()) throw new AssertionError("Could not delete scratch file: "+f);
    }

    w = new RandomAccessFile(f, "rw").getChannel();
    w.truncate(0);
    r = new RandomAccessFile(f, "r").getChannel();

  }

  @AfterEach
  void tearDown() {

    if (w != null){
      try {
        w.close();
        w = null;
      } catch (IOException ignored) { }
    }

    if (r != null){
      try {
        r.close();
        r = null;
      } catch (IOException ignored) { }
    }

  }

  @Test
  void writes_nils() throws Exception {

    try (Out out = new Out(w, 17)) {
      for (int i=0;i<100;i++){
        out.write(Values.NIL);
      }
    }
    w.close();

    try (In in = new In(r, 17)){
      for (int i=0;i<100;i++){
        Value v = in.readNext();
        assertThat(v).isSameAs(Values.NIL);
      }
      Value end = in.readNext();
      assertThat(end).isNull();
    }
    r.close();

  }

  @Test
  void writes_longs() throws Exception {

    try (Out out = new Out(w, 17)) {
      for (int i=0;i<100;i++){
        out.write(Values.make(i));
      }
    }
    w.close();

    try (In in = new In(r, 17)){
      for (int i=0;i<100;i++){
        Value v = in.readNext();
        assertThat(v).isEqualTo(Values.make(i));
      }
      Value end = in.readNext();
      assertThat(end).isNull();
    }
    r.close();

  }

  @Test
  void writes_doubles() throws Exception {

    try (Out out = new Out(w, 17)) {
      for (int i=0;i<100;i++){
        out.write(Values.make((double)i));
      }
    }
    w.close();

    try (In in = new In(r, 17)){
      for (int i=0;i<100;i++){
        Value v = in.readNext();
        assertThat(v).isEqualTo(Values.make((double)i));
      }
      Value end = in.readNext();
      assertThat(end).isNull();
    }
    r.close();

  }

  @Test
  void writes_decimals() throws Exception {

    try (Out out = new Out(w, 17)) {
      for (int i=0;i<100;i++){
        out.write(Values.make(new BigDecimal(i+"."+i)));
      }
    }
    w.close();

    try (In in = new In(r, 17)){
      for (int i=0;i<100;i++){
        Value v = in.readNext();
        assertThat(v).isEqualTo(Values.make(new BigDecimal(i+"."+i)));
      }
      Value end = in.readNext();
      assertThat(end).isNull();
    }
    r.close();

  }

  @Test
  void writes_datetimes() throws Exception {

    ArrayList<String> zoneIds = new ArrayList<>(ZoneId.getAvailableZoneIds());
    ArrayList<Value> dts = new ArrayList<>();
    Instant now = Instant.now();

    for (int i=0;i<1000;i++){
      int idx = (i % zoneIds.size() + i%2)%zoneIds.size();
      ZoneId zoneId = ZoneId.of(zoneIds.get(idx));
      ZonedDateTime dt = ZonedDateTime.ofInstant(now.plusSeconds(i), zoneId);
      Value v = Values.make(new DateTimeValue(dt));
      dts.add(v);
    }

    try (Out out = new Out(w, 17)) {
      for (int i=0;i<1000;i++){
        out.write(dts.get(i));
      }
    }
    w.close();

    try (In in = new In(r, 17)){
      for (int i=0;i<1000;i++){
        Value v = in.readNext();
        assertThat(v).isEqualTo(dts.get(i));
      }
      Value end = in.readNext();
      assertThat(end).isNull();
    }
    r.close();

  }

  @Test
  void writes_strings() throws Exception {

    ArrayList<String> zoneIds = new ArrayList<>(ZoneId.getAvailableZoneIds());
    ArrayList<Value> values = new ArrayList<>();

    for (int i=0;i<1000;i++){
      int idx = (i % zoneIds.size() + i%2) % zoneIds.size();
      ZoneId zoneId = ZoneId.of(zoneIds.get(idx));
      values.add(Values.make(zoneId.toString()));
    }

    try (Out out = new Out(w, 17)) {
      for (int i=0;i<1000;i++){
        out.write(values.get(i));
      }
    }
    w.close();

    try (In in = new In(r, 17)){
      for (int i=0;i<1000;i++){
        Value v = in.readNext();
        assertThat(v).isEqualTo(values.get(i));
      }
      Value end = in.readNext();
      assertThat(end).isNull();
    }
    r.close();

  }

  @Test
  void writes_binary() throws Exception {

    ArrayList<String> zoneIds = new ArrayList<>(ZoneId.getAvailableZoneIds());
    ArrayList<Value> values = new ArrayList<>();

    for (int i=0;i<1000;i++){
      int idx = (i % zoneIds.size() + i%2) % zoneIds.size();
      ZoneId zoneId = ZoneId.of(zoneIds.get(idx));
      values.add(Values.make(zoneId.toString().getBytes(StandardCharsets.UTF_8)));
    }

    try (Out out = new Out(w, 17)) {
      for (int i=0;i<1000;i++){
        out.write(values.get(i));
      }
    }
    w.close();

    try (In in = new In(r, 17)){
      for (int i=0;i<1000;i++){
        Value v = in.readNext();
        assertThat(v).isEqualTo(values.get(i));
      }
      Value end = in.readNext();
      assertThat(end).isNull();
    }
    r.close();

  }

  @Test
  void writes_empty_lists() throws Exception {

    try (Out out = new Out(w, 17)) {
      for (int i=0;i<1000;i++){
        out.write(Values.EMPTY_LIST);
      }
    }
    w.close();

    try (In in = new In(r, 17)){
      for (int i=0;i<1000;i++){
        Value v = in.readNext();
        assertThat(v).isEqualTo(Values.EMPTY_LIST);
      }
      Value end = in.readNext();
      assertThat(end).isNull();
    }
    r.close();

  }

  @Test
  void writes_lists() throws Exception {

    ArrayList<Value> values = new ArrayList<>();
    for (int i=0;i<1000;i++){
      ListValue list = new ListValue();
      for (int j=0;j<i%10;j++){
        list = list.append(Values.make(j));
        list = list.append(Values.make((double) j));
        list = list.append(Values.make("j: "+j));
      }
      values.add(Values.make(list));
    }


    try (Out out = new Out(w, 17)) {
      for (int i=0;i<1000;i++){
        out.write(values.get(i % values.size()));
      }
    }
    w.close();

    try (In in = new In(r, 17)){
      for (int i=0;i<1000;i++){
        Value v = in.readNext();
        assertThat(v).isEqualTo(values.get(i % values.size()));
      }
      Value end = in.readNext();
      assertThat(end).isNull();
    }
    r.close();

  }

  @Test
  void writes_nested_lists() throws Exception {

    ArrayList<Value> values = new ArrayList<>();
    for (int i=0;i<1000;i++){
      Value a = Values.EMPTY_LIST;
      Value b = Values.makeList(i, i+1, a);
      Value c = Values.makeList(a, b, i+1, i+2);
      values.add(c);
    }

    try (Out out = new Out(w, 17)) {
      for (int i=0;i<1000;i++){
        out.write(values.get(i % values.size()));
      }
    }
    w.close();

    try (In in = new In(r, 17)){
      for (int i=0;i<1000;i++){
        Value v = in.readNext();
        assertThat(v).isEqualTo(values.get(i % values.size()));
      }
      Value end = in.readNext();
      assertThat(end).isNull();
    }
    r.close();

  }

  @Test
  void writes_empty_dicts() throws Exception {

    try (Out out = new Out(w, 17)) {
      for (int i=0;i<1000;i++){
        out.write(Values.EMPTY_DICT);
      }
    }
    w.close();

    try (In in = new In(r, 17)){
      for (int i=0;i<1000;i++){
        Value v = in.readNext();
        assertThat(v).isEqualTo(Values.EMPTY_DICT);
      }
      Value end = in.readNext();
      assertThat(end).isNull();
    }
    r.close();

  }

  @Test
  void writes_dicts() throws Exception {

    ArrayList<Value> values = new ArrayList<>();
    for (int i=0;i<1000;i++){
      TransientDictValue t = new TransientDictValue();
      for (int j=0;j<i%10;j++){
        t.put("l: "+j, Values.make(j));
        t.put("d: "+j, Values.make((double) j));
        t.put("s: "+j, Values.make("j -> "+ j));
      }
      values.add(Values.make(t.persistent()));
    }


    try (Out out = new Out(w, 17)) {
      for (int i=0;i<1000;i++){
        out.write(values.get(i % values.size()));
      }
    }
    w.close();

    try (In in = new In(r, 17)){
      for (int i=0;i<1000;i++){
        Value v = in.readNext();
        assertThat(v).isEqualTo(values.get(i % values.size()));
      }
      Value end = in.readNext();
      assertThat(end).isNull();
    }
    r.close();

  }

  @Test
  void writes_nested_dicts() throws Exception {

    ArrayList<Value> values = new ArrayList<>();
    for (int i=0;i<1000;i++){
      Value a = Values.makeDict("bytes", Values.make(new byte[] {0,1,2}));
      Value b = Values.makeDict("a", i, "b", i+1, "c", a);
      Value c = Values.makeDict("z", a, "x", b, "y", i+1, "q", i+2);
      values.add(c);
    }

    try (Out out = new Out(w, 17)) {
      for (int i=0;i<1000;i++){
        out.write(values.get(i % values.size()));
      }
    }
    w.close();

    try (In in = new In(r, 17)){
      for (int i=0;i<1000;i++){
        Value v = in.readNext();
        assertThat(v).isEqualTo(values.get(i % values.size()));
      }
      Value end = in.readNext();
      assertThat(end).isNull();
    }
    r.close();

  }

  @Test
  void writes_nested_dicts_and_lists() throws Exception {

    ArrayList<Value> values = new ArrayList<>();
    for (int i=0;i<1000;i++){
      Value a = Values.makeDict("foo", Values.make(new byte[] {0,1,2}), "bar", Values.EMPTY_LIST);
      Value b = Values.makeList(Values.make(new byte[] {}), i, "b", i+1, "c", a);
      Value c = Values.makeDict("z", a, "x", b, "y", new BigDecimal(i+1), "q", i+2);
      values.add(c);
    }

    try (Out out = new Out(w, 17)) {
      for (int i=0;i<1000;i++){
        out.write(values.get(i % values.size()));
      }
    }
    w.close();

    try (In in = new In(r, 17)){
      for (int i=0;i<1000;i++){
        Value v = in.readNext();
        assertThat(v).isEqualTo(values.get(i % values.size()));
      }
      Value end = in.readNext();
      assertThat(end).isNull();
    }
    r.close();

  }

  @Test
  void cannot_serialize_functions() throws Exception {

    Value f = TweakFlow.evaluate("(x) -> x");
    assertThat(f.isFunction()).isTrue();

    try (Out out = new Out(w, 17)) {
      assertThrows(IOException.class, () -> out.write(f));
    }

  }

  @Test
  void cannot_serialize_nested_functions() throws Exception {

    Value lf = TweakFlow.evaluate("[(x) -> x]");

    assertThat(lf.isList()).isTrue();
    try (Out out = new Out(w, 17)) {
      assertThrows(IOException.class, () -> out.write(lf));
    }

  }
}
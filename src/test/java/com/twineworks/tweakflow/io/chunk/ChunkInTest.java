package com.twineworks.tweakflow.io.chunk;

import com.twineworks.tweakflow.lang.values.Value;
import com.twineworks.tweakflow.lang.values.Values;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Iterator;

import static org.assertj.core.api.StrictAssertions.assertThat;

class ChunkInTest {

  static Iterator<byte[]> getChunks(Value v, int maxChunkSize){
    ChunkOut chunkOut = new ChunkOut(v, maxChunkSize);
    ArrayList<byte[]> chunks = new ArrayList<>();
    while (chunkOut.hasMoreChunks()) {
      chunks.add(chunkOut.nextChunk());
    }

    return chunks.iterator();
  }

  @Test
  void reads_nils() throws Exception {

    ChunkIn in = new ChunkIn(getChunks(Values.NIL, 16));
    Value v = in.read();

    assertThat(v.isNil()).isTrue();

  }


  @Test
  void reads_binary() throws Exception {
    Value src = Values.make(new byte[]{1,2,3});
    ChunkIn in = new ChunkIn(getChunks(src, 16));
    Value v = in.read();

    assertThat(v).isEqualTo(src);

  }

  @Test
  void reads_binary_parts() throws Exception {
    Value src = Values.make(new byte[]{1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17});
    ChunkIn in = new ChunkIn(getChunks(src, 16));
    Value v = in.read();

    assertThat(v).isEqualTo(src);

  }

  @Test
  void reads_strings() throws Exception {

    ChunkIn in = new ChunkIn(getChunks(Values.make("Hello"), 16));
    Value v = in.read();

    assertThat(v.string()).isEqualTo("Hello");

  }

  @Test
  void reads_string_parts() throws Exception {

    final String STR = "This is longer than chunk size.";

    ChunkIn in = new ChunkIn(getChunks(Values.make(STR), 16));
    Value v = in.read();

    assertThat(v.string()).isEqualTo(STR);

  }

  @Test
  void reads_dict_key_parts() throws Exception {

    final Value src = Values.makeDict("This is longer than chunk size.", 0);

    ChunkIn in = new ChunkIn(getChunks(src, 16));
    Value v = in.read();

    assertThat(v).isEqualTo(src);

  }

  @Test
  void reads_empty_lists() throws Exception {

    ChunkIn in = new ChunkIn(getChunks(Values.EMPTY_LIST, 16));
    Value v = in.read();

    assertThat(v).isEqualTo(Values.EMPTY_LIST);

  }

  @Test
  void reads_lists() throws Exception {

    Value src = Values.makeList(Values.TRUE, Values.NIL, Values.make("Hello World!"), Values.LONG_NEG_ONE);
    ChunkIn in = new ChunkIn(getChunks(src, 16));
    Value v = in.read();

    assertThat(v).isEqualTo(src);

  }

  @Test
  void reads_nested_lists() throws Exception {

    Value src = Values.makeList(
        Values.TRUE,
        Values.makeList(
            Values.NIL,
            Values.makeList(1, 2, 3)
        ),
        Values.make("Hello World!"),
        Values.LONG_NEG_ONE
    );

    ChunkIn in = new ChunkIn(getChunks(src, 16));
    Value v = in.read();

    assertThat(v).isEqualTo(src);

  }

  @Test
  void reads_empty_dict() throws Exception {

    ChunkIn in = new ChunkIn(getChunks(Values.EMPTY_DICT, 16));
    Value v = in.read();

    assertThat(v).isEqualTo(Values.EMPTY_DICT);

  }

  @Test
  void reads_dict() throws Exception {

    Value src = Values.makeDict(
        "foo", "bar",
        "baz", 1L,
        "str", "short",
        "str_long", "A much longer string that does not fit in a chunk",
        "a long key that does not fit in a chunk", -1L,
        "f", Math.PI,
        "a", Values.TRUE,
        "bin", new byte[]{1,2,3,4,5,6,7,8,9,10},
        "d", Values.make(new BigDecimal("123.45678901234545678967890")),
        "dt", Values.EPOCH
    );

    ChunkIn in = new ChunkIn(getChunks(
        src,
        16)
    );
    Value v = in.read();

    assertThat(v).isEqualTo(src);

  }

  @Test
  void reads_nested_dict() throws Exception {

    Value src = Values.makeDict(
        "foo", "bar",
        "baz", Values.makeDict("key", Values.makeDict("a", Values.EMPTY_LIST)),
        "a", Values.TRUE
    );

    ChunkIn in = new ChunkIn(getChunks(
        src,
        16)
    );
    Value v = in.read();

    assertThat(v).isEqualTo(src);

  }


  @Test
  void reads_doubles() throws Exception {

    Value src = Values.make(Math.PI);

    ChunkIn in = new ChunkIn(getChunks(
        src,
        16)
    );
    Value v = in.read();

    assertThat(v).isEqualTo(src);

  }

  @Test
  void reads_decimals() throws Exception {

    Value src = Values.make(new BigDecimal(Math.PI));

    ChunkIn in = new ChunkIn(getChunks(
        src,
        16)
    );
    Value v = in.read();

    assertThat(v).isEqualTo(src);

  }

  @Test
  void reads_short_decimals() throws Exception {

    Value src = Values.make(Values.DECIMAL_ZERO);

    ChunkIn in = new ChunkIn(getChunks(
        src,
        16)
    );
    Value v = in.read();

    assertThat(v).isEqualTo(src);

  }


  @Test
  void reads_datetimes() throws Exception {

    Value src = Values.make(ZonedDateTime.of(1981, 8, 16, 4, 30, 2, 1233, ZoneId.of("Europe/Berlin")));

    ChunkIn in = new ChunkIn(getChunks(
        src,
        32)
    );
    Value v = in.read();

    assertThat(v).isEqualTo(src);

  }

  @Test
  void reads_datetime_parts() throws Exception {

    Value src = Values.make(ZonedDateTime.of(1981, 8, 16, 4, 30, 2, 1233, ZoneId.of("Europe/Berlin")));

    ChunkIn in = new ChunkIn(getChunks(
        src,
        16)
    );
    Value v = in.read();

    assertThat(v).isEqualTo(src);

  }

}
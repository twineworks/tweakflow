package com.twineworks.tweakflow.io.chunk;

import com.twineworks.tweakflow.io.MagicNumbers;
import com.twineworks.tweakflow.lang.values.Values;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.ArrayList;

import static org.assertj.core.api.StrictAssertions.assertThat;

class ChunkOutTest {

  @Test
  void writes_nils() throws Exception {

    ChunkOut chunkOut = new ChunkOut(Values.NIL, 16);
    ArrayList<byte[]> chunks = new ArrayList<>();
    while (chunkOut.hasMoreChunks()) {
      chunks.add(chunkOut.nextChunk());
    }

    assertThat(chunks).asList().hasSize(1);
    assertThat(chunks.get(0)).containsExactly(MagicNumbers.Format.VOID);

  }

  @Test
  void writes_longs() throws Exception {

    ChunkOut chunkOut = new ChunkOut(Values.LONG_ONE, 16);
    ArrayList<byte[]> chunks = new ArrayList<>();
    while (chunkOut.hasMoreChunks()) {
      chunks.add(chunkOut.nextChunk());
    }

    assertThat(chunks).asList().hasSize(1);
    assertThat(chunks.get(0)).containsExactly(
        MagicNumbers.Format.LONG,
        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x01
    );

  }

  @Test
  void writes_doubles() throws Exception {

    ChunkOut chunkOut = new ChunkOut(Values.make(Math.PI), 16);
    ArrayList<byte[]> chunks = new ArrayList<>();
    while (chunkOut.hasMoreChunks()) {
      chunks.add(chunkOut.nextChunk());
    }

    assertThat(chunks).asList().hasSize(1);
    assertThat(chunks.get(0)).containsExactly(
        MagicNumbers.Format.DOUBLE,
        (byte) 0x40, (byte) 0x09, (byte) 0x21, (byte) 0xFB, (byte) 0x54, (byte) 0x44, (byte) 0x2D, (byte) 0x18
    );

  }

  @Test
  void writes_datetimes() throws Exception {

    ChunkOut chunkOut = new ChunkOut(Values.EPOCH, 32);
    ArrayList<byte[]> chunks = new ArrayList<>();
    while (chunkOut.hasMoreChunks()) {
      chunks.add(chunkOut.nextChunk());
    }

    assertThat(chunks).asList().hasSize(1);
    assertThat(chunks.get(0)).containsExactly(
        MagicNumbers.Format.DATETIME,
        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x0F, // buffer len
        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, // 0 secs
        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, // 0 nanos
        (byte) 0x55, (byte) 0x54, (byte) 0x43 // UTC

    );
  }

  @Test
  void writes_datetimes_chunked() throws Exception {

    ChunkOut chunkOut = new ChunkOut(Values.EPOCH, 16);
    ArrayList<byte[]> chunks = new ArrayList<>();
    while (chunkOut.hasMoreChunks()) {
      chunks.add(chunkOut.nextChunk());
    }

    assertThat(chunks).asList().hasSize(5);
    assertThat(chunks.get(0)).containsExactly(
        MagicNumbers.Format.DATETIME_PART,
        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x0F, // buffer len
        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, // index
        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x03, // local len
        (byte) 0x00, (byte) 0x00, (byte) 0x00 // 3x0 sec
    );

    assertThat(chunks.get(1)).containsExactly(
        MagicNumbers.Format.DATETIME_PART,
        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x0F, // buffer len
        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x03, // index
        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x03, // local len
        (byte) 0x00, (byte) 0x00, (byte) 0x00 // 3x0 sec
    );

    assertThat(chunks.get(2)).containsExactly(
        MagicNumbers.Format.DATETIME_PART,
        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x0F, // buffer len
        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x06, // index
        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x03, // local len
        (byte) 0x00, (byte) 0x00, (byte) 0x00 // 2x0 sec, 1x0 nano
    );

    assertThat(chunks.get(3)).containsExactly(
        MagicNumbers.Format.DATETIME_PART,
        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x0F, // buffer len
        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x09, // index
        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x03, // local len
        (byte) 0x00, (byte) 0x00, (byte) 0x00 // 3x0 nano
    );

    assertThat(chunks.get(4)).containsExactly(
        MagicNumbers.Format.DATETIME_PART,
        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x0F, // buffer len
        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x0C, // index
        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x03, // local len
        (byte) 0x55, (byte) 0x54, (byte) 0x43 // UTC
    );

  }

  @Test
  void writes_decimal_0() throws Exception {
    ChunkOut chunkOut = new ChunkOut(Values.DECIMAL_ZERO, 16);
    ArrayList<byte[]> chunks = new ArrayList<>();
    while (chunkOut.hasMoreChunks()) {
      chunks.add(chunkOut.nextChunk());
    }

    assertThat(chunks).asList().hasSize(1);
    assertThat(chunks.get(0)).containsExactly(
        MagicNumbers.Format.DECIMAL,
        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x01, // len
        (byte) 0x30
    );

  }

  @Test
  void writes_decimal_parts_1_000000000000000001() throws Exception {
    ChunkOut chunkOut = new ChunkOut(Values.make(new BigDecimal("1.000000000000000001")), 24);
    ArrayList<byte[]> chunks = new ArrayList<>();
    while (chunkOut.hasMoreChunks()) {
      chunks.add(chunkOut.nextChunk());
    }

    assertThat(chunks).asList().hasSize(2);
    assertThat(chunks.get(0)).containsExactly(
        MagicNumbers.Format.DECIMAL_PART,
        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x14,  // total len
        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,  // index
        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x0B,  // len
        (byte) 0x31, (byte) 0x2E, (byte) 0x30, (byte) 0x30,  // 1.00
        (byte) 0x30, (byte) 0x30, (byte) 0x30, (byte) 0x30,  // 0000
        (byte) 0x30, (byte) 0x30, (byte) 0x30                // 000
    );

    assertThat(chunks.get(1)).containsExactly(
        MagicNumbers.Format.DECIMAL_PART,
        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x14,  // total len
        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x0B,  // index
        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x09,  // len
        (byte) 0x30, (byte) 0x30, (byte) 0x30, (byte) 0x30,  // 0000
        (byte) 0x30, (byte) 0x30, (byte) 0x30, (byte) 0x30,  // 0000
        (byte) 0x31                                          // 000
    );


  }

  @Test
  void writes_empty_binary() throws Exception {
    ChunkOut chunkOut = new ChunkOut(Values.EMPTY_BINARY, 16);
    ArrayList<byte[]> chunks = new ArrayList<>();
    while (chunkOut.hasMoreChunks()) {
      chunks.add(chunkOut.nextChunk());
    }

    assertThat(chunks).asList().hasSize(1);
    assertThat(chunks.get(0)).containsExactly(
        MagicNumbers.Format.BINARY,
        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00
    );

  }

  @Test
  void writes_binary() throws Exception {
    ChunkOut chunkOut = new ChunkOut(Values.make(new byte[]{1,2,3}), 16);
    ArrayList<byte[]> chunks = new ArrayList<>();
    while (chunkOut.hasMoreChunks()) {
      chunks.add(chunkOut.nextChunk());
    }

    assertThat(chunks).asList().hasSize(1);
    assertThat(chunks.get(0)).containsExactly(
        MagicNumbers.Format.BINARY,
        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x03, // length
        (byte) 0x01, (byte) 0x02, (byte) 0x03 // values
    );

  }

  @Test
  void writes_binary_parts() throws Exception {
    ChunkOut chunkOut = new ChunkOut(Values.make(new byte[]{1,2,3,4,1,2,3,4,1,2,3,4}), 16);
    ArrayList<byte[]> chunks = new ArrayList<>();
    while (chunkOut.hasMoreChunks()) {
      chunks.add(chunkOut.nextChunk());
    }

    assertThat(chunks).asList().hasSize(4);
    assertThat(chunks.get(0)).containsExactly(
        MagicNumbers.Format.BINARY_PART,
        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x0C, // length total
        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, // index
        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x03, // length
        (byte) 0x01, (byte) 0x02, (byte) 0x03 // values
    );

    assertThat(chunks.get(1)).containsExactly(
        MagicNumbers.Format.BINARY_PART,
        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x0C, // length total
        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x03, // index
        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x03, // length
        (byte) 0x04, (byte) 0x01, (byte) 0x02 // values
    );

    assertThat(chunks.get(2)).containsExactly(
        MagicNumbers.Format.BINARY_PART,
        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x0C, // length total
        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x06, // index
        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x03, // length
        (byte) 0x03, (byte) 0x04, (byte) 0x01 // values
    );

    assertThat(chunks.get(3)).containsExactly(
        MagicNumbers.Format.BINARY_PART,
        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x0C, // length total
        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x09, // index
        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x03, // length
        (byte) 0x02, (byte) 0x03, (byte) 0x04 // values
    );

  }

  @Test
  void writes_empty_strings() throws Exception {
    ChunkOut chunkOut = new ChunkOut(Values.EMPTY_STRING, 16);
    ArrayList<byte[]> chunks = new ArrayList<>();
    while (chunkOut.hasMoreChunks()) {
      chunks.add(chunkOut.nextChunk());
    }

    assertThat(chunks).asList().hasSize(1);
    assertThat(chunks.get(0)).containsExactly(
        MagicNumbers.Format.STRING,
        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00
    );

}

  @Test
  void writes_full_strings() throws Exception {
    ChunkOut chunkOut = new ChunkOut(Values.make("HALLO"), 16);
    ArrayList<byte[]> chunks = new ArrayList<>();
    while (chunkOut.hasMoreChunks()) {
      chunks.add(chunkOut.nextChunk());
    }

    assertThat(chunks).asList().hasSize(1);
    assertThat(chunks.get(0)).containsExactly(
        MagicNumbers.Format.STRING,
        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x05, // length
        (byte) 0x48, (byte) 0x41, (byte) 0x4C, (byte) 0x4C, (byte) 0x4F // ascii HALLO
    );

  }


  @Test
  void writes_partial_strings_even_buffer() throws Exception {
    ChunkOut chunkOut = new ChunkOut(Values.make("HALLO--HALLO"), 16);
    ArrayList<byte[]> chunks = new ArrayList<>();
    while (chunkOut.hasMoreChunks()) {
      chunks.add(chunkOut.nextChunk());
    }

    assertThat(chunks).asList().hasSize(4);
    assertThat(chunks.get(0)).containsExactly(
        MagicNumbers.Format.STRING_PART,
        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x0C, // total length
        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, // starting index
        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x03, // partial length
        (byte) 0x48, (byte) 0x41, (byte) 0x4C // ascii HAL
    );

    assertThat(chunks.get(1)).containsExactly(
        MagicNumbers.Format.STRING_PART,
        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x0C, // total length
        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x03, // starting index
        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x03, // partial length
        (byte) 0x4C, (byte) 0x4F, (byte) 0x2D // ascii LO-
    );

    assertThat(chunks.get(2)).containsExactly(
        MagicNumbers.Format.STRING_PART,
        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x0C, // total length
        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x06, // starting index
        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x03, // partial length
        (byte) 0x2D, (byte) 0x48, (byte) 0x41 // ascii -HA
    );

    assertThat(chunks.get(3)).containsExactly(
        MagicNumbers.Format.STRING_PART,
        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x0C, // total length
        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x09, // starting index
        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x03, // partial length
        (byte) 0x4C, (byte) 0x4C, (byte) 0x4F // ascii LLO
    );

  }

  @Test
  void writes_partial_strings_uneven_buffer() throws Exception {
    ChunkOut chunkOut = new ChunkOut(Values.make("FOO-FOO-FOO-FOO-FOO-FOO-FOO-"), 32);
    ArrayList<byte[]> chunks = new ArrayList<>();
    while (chunkOut.hasMoreChunks()) {
      chunks.add(chunkOut.nextChunk());
    }

    assertThat(chunks).asList().hasSize(2);
    assertThat(chunks.get(0)).containsExactly(
        MagicNumbers.Format.STRING_PART,
        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x1C, // total length
        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, // starting index
        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x13, // partial length
        (byte) 0x46, (byte) 0x4F, (byte) 0x4F, (byte) 0x2D, // ascii FOO-
        (byte) 0x46, (byte) 0x4F, (byte) 0x4F, (byte) 0x2D, // ascii FOO-
        (byte) 0x46, (byte) 0x4F, (byte) 0x4F, (byte) 0x2D, // ascii FOO-
        (byte) 0x46, (byte) 0x4F, (byte) 0x4F, (byte) 0x2D, // ascii FOO-
        (byte) 0x46, (byte) 0x4F, (byte) 0x4F // ascii FOO

    );

    assertThat(chunks.get(1)).containsExactly(
        MagicNumbers.Format.STRING_PART,
        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x1C, // total length
        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x13, // starting index
        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x09, // partial length
        (byte) 0x2D, (byte) 0x46, (byte) 0x4F, (byte) 0x4F, // ascii -FOO
        (byte) 0x2D, (byte) 0x46, (byte) 0x4F, (byte) 0x4F, // ascii -FOO
        (byte) 0x2D // ascii -

    );



  }

  @Test
  void writes_empty_lists() throws Exception {
    ChunkOut chunkOut = new ChunkOut(Values.EMPTY_LIST, 16);
    ArrayList<byte[]> chunks = new ArrayList<>();
    while (chunkOut.hasMoreChunks()) {
      chunks.add(chunkOut.nextChunk());
    }

    assertThat(chunks).asList().hasSize(1);
    assertThat(chunks.get(0)).containsExactly(
        MagicNumbers.Format.LIST,
        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00 // length
    );

  }

  @Test
  void writes_full_lists_of_bool() throws Exception {
    ChunkOut chunkOut = new ChunkOut(Values.makeList(Values.TRUE, Values.FALSE, Values.TRUE), 16);
    ArrayList<byte[]> chunks = new ArrayList<>();
    while (chunkOut.hasMoreChunks()) {
      chunks.add(chunkOut.nextChunk());
    }

    assertThat(chunks).asList().hasSize(1);
    assertThat(chunks.get(0)).containsExactly(
        MagicNumbers.Format.LIST,
        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x03, // length
        MagicNumbers.Format.BOOLEAN, (byte) 0x01, // bool 1
        MagicNumbers.Format.BOOLEAN, (byte) 0x00, // bool 2
        MagicNumbers.Format.BOOLEAN, (byte) 0x01  // bool 3
    );

  }



  @Test
  void writes_split_lists_of_bool() throws Exception {
    ChunkOut chunkOut = new ChunkOut(Values.makeList(Values.TRUE, Values.FALSE, Values.TRUE, Values.FALSE, Values.TRUE, Values.FALSE, Values.TRUE, Values.FALSE, Values.TRUE, Values.FALSE), 16);
    ArrayList<byte[]> chunks = new ArrayList<>();
    while (chunkOut.hasMoreChunks()) {
      chunks.add(chunkOut.nextChunk());
    }

    assertThat(chunks).asList().hasSize(2);
    assertThat(chunks.get(0)).containsExactly(
        MagicNumbers.Format.LIST,
        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x0A, // length
        MagicNumbers.Format.BOOLEAN, (byte) 0x01, // bool 1
        MagicNumbers.Format.BOOLEAN, (byte) 0x00, // bool 2
        MagicNumbers.Format.BOOLEAN, (byte) 0x01, // bool 3
        MagicNumbers.Format.BOOLEAN, (byte) 0x00, // bool 4
        MagicNumbers.Format.BOOLEAN, (byte) 0x01  // bool 5
    );

    assertThat(chunks.get(1)).containsExactly(
        MagicNumbers.Format.BOOLEAN, (byte) 0x00, // bool 6
        MagicNumbers.Format.BOOLEAN, (byte) 0x01, // bool 7
        MagicNumbers.Format.BOOLEAN, (byte) 0x00, // bool 8
        MagicNumbers.Format.BOOLEAN, (byte) 0x01, // bool 9
        MagicNumbers.Format.BOOLEAN, (byte) 0x00  // bool 10
    );

  }

  @Test
  void writes_nested_lists_of_bool() throws Exception {
    ChunkOut chunkOut = new ChunkOut(Values.makeList(Values.TRUE, Values.makeList(Values.FALSE, Values.makeList(Values.TRUE)), Values.FALSE), 16);
    ArrayList<byte[]> chunks = new ArrayList<>();
    while (chunkOut.hasMoreChunks()) {
      chunks.add(chunkOut.nextChunk());
    }

    assertThat(chunks).asList().hasSize(2);
    assertThat(chunks.get(0)).containsExactly(
        MagicNumbers.Format.LIST,
        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x03, // length
        MagicNumbers.Format.BOOLEAN, (byte) 0x01, // bool 1
        MagicNumbers.Format.LIST, //   list(FALSE, ...)
        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x02, // length
        MagicNumbers.Format.BOOLEAN, (byte) 0x00 // bool 2
    );

    assertThat(chunks.get(1)).containsExactly(
        MagicNumbers.Format.LIST, //   list(FALSE, list(TRUE))
        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x01, // length
        MagicNumbers.Format.BOOLEAN, (byte) 0x01, // bool 3
        MagicNumbers.Format.BOOLEAN, (byte) 0x00 // bool 4

    );

  }


  @Test
  void writes_empty_dict() throws Exception {
    ChunkOut chunkOut = new ChunkOut(Values.EMPTY_DICT, 16);
    ArrayList<byte[]> chunks = new ArrayList<>();
    while (chunkOut.hasMoreChunks()) {
      chunks.add(chunkOut.nextChunk());
    }

    assertThat(chunks).asList().hasSize(1);
    assertThat(chunks.get(0)).containsExactly(
        MagicNumbers.Format.DICT,
        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00 // length
    );

  }


  @Test
  void writes_full_dict() throws Exception {
    ChunkOut chunkOut = new ChunkOut(Values.makeDict("foo", Values.TRUE), 16);
    ArrayList<byte[]> chunks = new ArrayList<>();
    while (chunkOut.hasMoreChunks()) {
      chunks.add(chunkOut.nextChunk());
    }

    assertThat(chunks).asList().hasSize(1);
    assertThat(chunks.get(0)).containsExactly(
        MagicNumbers.Format.DICT,
        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x01, // length
        MagicNumbers.Format.KEY,
        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x03, // key length
        (byte) 0x66, (byte) 0x6F, (byte) 0x6F,              // ASCII foo
        MagicNumbers.Format.BOOLEAN, (byte) 0x01            // value

    );

  }

  @Test
  void writes_split_dict() throws Exception {
    ChunkOut chunkOut = new ChunkOut(Values.makeDict("foo", "bar"), 16);
    ArrayList<byte[]> chunks = new ArrayList<>();
    while (chunkOut.hasMoreChunks()) {
      chunks.add(chunkOut.nextChunk());
    }

    assertThat(chunks).asList().hasSize(2);
    assertThat(chunks.get(0)).containsExactly(
        MagicNumbers.Format.DICT,
        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x01, // length
        MagicNumbers.Format.KEY,
        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x03, // key length
        (byte) 0x66, (byte) 0x6F, (byte) 0x6F              // ASCII foo
    );

    assertThat(chunks.get(1)).containsExactly(
        MagicNumbers.Format.STRING,
        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x03, // length
        (byte) 0x62, (byte) 0x61, (byte) 0x72              // ASCII bar
    );

  }

}
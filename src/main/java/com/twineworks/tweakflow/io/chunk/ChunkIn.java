package com.twineworks.tweakflow.io.chunk;

import com.twineworks.tweakflow.io.MagicNumbers;
import com.twineworks.tweakflow.lang.values.*;

import java.math.BigDecimal;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Iterator;

public class ChunkIn {
  ByteBuffer buffer;

  Iterator<byte[]> chunkIterator;

  public ChunkIn(Iterator<byte[]> chunkIterator) {
    this.chunkIterator = chunkIterator;

  }

  public Value read() {
    return readNextValue();
  }

  private ByteBuffer nextBuffer() {
    if (!chunkIterator.hasNext()) return null;
    return ByteBuffer.wrap(chunkIterator.next());
  }

  private String readNextKey() {

    byte[] partial = null;

    while (true) {
      // out of buffer, get the next
      if (buffer == null || !buffer.hasRemaining()) {
        buffer = nextBuffer();
        if (buffer == null) {
          throw new AssertionError("Unexpected end of chunks");
        }
      }

      while (buffer.remaining() > 0) {
        byte magic = buffer.get();
        switch (magic) {
          case MagicNumbers.Format.KEY:
            byte[] str = new byte[buffer.getInt()];
            buffer.get(str);
            return new String(str, StandardCharsets.UTF_8);
          case MagicNumbers.Format.KEY_PART: {
            int totalSize = buffer.getInt();
            int index = buffer.getInt();
            int partLen = buffer.getInt();
            if (partial == null) {
              partial = new byte[totalSize];
            }
            buffer.get(partial, index, partLen);
            if (index + partLen == totalSize) {
              return new String(partial, StandardCharsets.UTF_8);
            }
            continue;
          }
          default:
            throw new RuntimeException("Unexpected magic number in key place: " + magic);
        }
      }
    }
  }

  private Value bytesToDatetime(byte[] bytes){
    ByteBuffer b = ByteBuffer.wrap(bytes);
    long secs = b.getLong();
    int nanos = b.getInt();
    String tz = new String(bytes, 12, bytes.length-12, StandardCharsets.UTF_8);
    ZoneId zoneId = ZoneId.of(tz);
    ZonedDateTime zonedDateTime = ZonedDateTime.ofInstant(Instant.ofEpochSecond(secs, nanos), zoneId);
    return Values.make(new DateTimeValue(zonedDateTime));
  }

  private Value readNextValue() {

    byte[] partial = null;
    ListValue list = null;
    TransientDictValue t = null;

    while (true) {
      // out of buffer, get the next
      if (buffer == null || !buffer.hasRemaining()) {
        buffer = nextBuffer();
        if (buffer == null) {
          throw new AssertionError("Unexpected end of chunks");
        }
      }

      while (buffer.remaining() > 0) {
        byte magic = buffer.get();
        byte[] bin = null;
        switch (magic) {
          case MagicNumbers.Format.VOID:
            return Values.NIL;
          case MagicNumbers.Format.BOOLEAN:
            return Values.make(buffer.get() != (byte) 0x00);
          case MagicNumbers.Format.LONG:
            return Values.make(buffer.getLong());
          case MagicNumbers.Format.DOUBLE:
            return Values.make(buffer.getDouble());
          case MagicNumbers.Format.DECIMAL:
            bin = new byte[buffer.getInt()];
            buffer.get(bin);
            return Values.make(new BigDecimal(new String(bin, StandardCharsets.UTF_8)));
          case MagicNumbers.Format.DECIMAL_PART: {
            int totalSize = buffer.getInt();
            int index = buffer.getInt();
            int partLen = buffer.getInt();
            if (partial == null) {
              partial = new byte[totalSize];
            }
            buffer.get(partial, index, partLen);
            if (index + partLen == totalSize) {
              return Values.make(new BigDecimal(new String(partial, StandardCharsets.UTF_8)));
            }
            continue;
          }
          case MagicNumbers.Format.STRING:
            bin = new byte[buffer.getInt()];
            buffer.get(bin);
            return Values.make(new String(bin, StandardCharsets.UTF_8));
          case MagicNumbers.Format.STRING_PART: {
            int totalSize = buffer.getInt();
            int index = buffer.getInt();
            int partLen = buffer.getInt();
            if (partial == null) {
              partial = new byte[totalSize];
            }
            buffer.get(partial, index, partLen);
            if (index + partLen == totalSize) {
              return Values.make(new String(partial, StandardCharsets.UTF_8));
            }
            continue;
          }
          case MagicNumbers.Format.BINARY:
            bin = new byte[buffer.getInt()];
            buffer.get(bin);
            return Values.make(bin);
          case MagicNumbers.Format.BINARY_PART: {
            int totalSize = buffer.getInt();
            int index = buffer.getInt();
            int partLen = buffer.getInt();
            if (partial == null) {
              partial = new byte[totalSize];
            }
            buffer.get(partial, index, partLen);
            if (index + partLen == totalSize) {
              return Values.make(partial);
            }
            continue;
          }

          case MagicNumbers.Format.DATETIME:
            bin = new byte[buffer.getInt()];
            buffer.get(bin);
            return bytesToDatetime(bin);
          case MagicNumbers.Format.DATETIME_PART: {
            int totalSize = buffer.getInt();
            int index = buffer.getInt();
            int partLen = buffer.getInt();
            if (partial == null) {
              partial = new byte[totalSize];
            }
            buffer.get(partial, index, partLen);
            if (index + partLen == totalSize) {
              return bytesToDatetime(partial);
            }
            continue;
          }

          case MagicNumbers.Format.LIST: {
            int size = buffer.getInt();
            if (size == 0) return Values.EMPTY_LIST;
            list = new ListValue();
            for (int i = 0; i < size; i++) {
              list = list.append(readNextValue());
            }
            return Values.make(list);
          }
          case MagicNumbers.Format.DICT: {
            int size = buffer.getInt();
            if (size == 0) return Values.EMPTY_DICT;

            t = new TransientDictValue();

            for (int i = 0; i < size; i++) {
              String key = readNextKey();
              Value v = readNextValue();
              t.put(key, v);
            }
            return Values.make(t.persistent());
          }
          default:
            throw new RuntimeException("Unknown magic number: " + magic);
        }
      }
    }


  }

}

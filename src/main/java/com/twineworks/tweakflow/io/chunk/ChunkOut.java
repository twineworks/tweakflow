package com.twineworks.tweakflow.io.chunk;

import com.twineworks.tweakflow.io.MagicNumbers;
import com.twineworks.tweakflow.lang.values.*;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.ArrayDeque;
import java.util.Iterator;

public class ChunkOut {

  private static class State {
    Value v;
    Value c;
    int idx;
    int sidx;
    byte written;
    String key;
    Iterator<String> keys;
    boolean keyWritten;
    byte[] bytes;

    public State(Value c, Value v, int idx, String key, Iterator<String> keys) {
      this.c = c;
      this.v = v;
      this.idx = idx;
      this.sidx = -1;
      this.written = MagicNumbers.Format.NOTHING;
      this.bytes = null;
      this.key = key;
      this.keys = keys;
      this.keyWritten = false;
    }
  }

  private State s;
  private ArrayDeque<State> t = new ArrayDeque<>(4);
  private final int maxChunkSize;

  private final ByteBuffer buffer;

  public ChunkOut(Value v, int maxChunkSize) {
    s = new State(Values.NIL, v, -1, null, null);
    this.maxChunkSize = maxChunkSize;
    if (maxChunkSize < 16) throw new RuntimeException("minimal size for chunk size is 16 bytes, got: " + maxChunkSize);
    this.buffer = ByteBuffer.allocate(maxChunkSize);
  }

  public boolean hasMoreChunks() {
    return s != null;
  }

  private void writeNextChunk() {

    while (true) {
      if (!writeNextElement()) break;
    }
    ;

  }

  private boolean writeNextElement() {
    if (s == null) return false;

    boolean isContinuation = s.written == MagicNumbers.Format.STRING_PART
        || s.written == MagicNumbers.Format.KEY_PART
        || s.written == MagicNumbers.Format.BINARY_PART
        || s.written == MagicNumbers.Format.DECIMAL_PART
        || s.written == MagicNumbers.Format.DATETIME_PART;

    if (isContinuation){
      switch(s.written){
        case MagicNumbers.Format.STRING_PART:
          writeStringPart();
          break;
        case MagicNumbers.Format.KEY_PART:
          writeKeyPart();
          break;
        case MagicNumbers.Format.BINARY_PART:
          writeBinaryPart();
          break;
        case MagicNumbers.Format.DECIMAL_PART:
          writeDecimalPart();
          break;
        case MagicNumbers.Format.DATETIME_PART:
          writeDatetimePart();
          break;
        default:
          throw new AssertionError("Unexpected continuation type: "+s.written);
      }
    } else if (s.c.isDict() && !s.keyWritten) {
      // must write the dict key before the value
      writeDictKey();
    } else {
      byte magicByte = s.v.type().getId();

      switch (magicByte) {
        case MagicNumbers.Format.VOID:
          writeNil();
          break;
        case MagicNumbers.Format.BOOLEAN:
          writeBoolean();
          break;
        case MagicNumbers.Format.LONG:
          writeLong();
          break;
        case MagicNumbers.Format.DOUBLE:
          writeDouble();
          break;
        case MagicNumbers.Format.DECIMAL:
          writeDecimal();
          break;
        case MagicNumbers.Format.DATETIME:
          writeDatetime();
          break;
        case MagicNumbers.Format.STRING:
          writeString();
          break;
        case MagicNumbers.Format.BINARY:
          writeBinary();
          break;
        case MagicNumbers.Format.LIST:
          writeList();
          break;
        case MagicNumbers.Format.DICT:
          writeDict();
          break;
        case MagicNumbers.Format.FUNCTION:
        default:
          throw new RuntimeException("Unsupported value type: " + s.v.type().name());
      }
    }

    return nextState();

  }


  private boolean writeDatetime() {
    DateTimeValue dt = s.v.dateTime();
    ZonedDateTime dz = dt.getZoned();
    Instant di = dt.getInstant();
    String z = dz.getZone().getId();

    byte[] binBytes = new byte[8+4+z.length()];
    ByteBuffer b = ByteBuffer.wrap(binBytes);
    b.putLong(di.getEpochSecond());
    b.putInt(di.getNano());
    b.put(z.getBytes(StandardCharsets.UTF_8));
    int remaining = buffer.remaining();
    if (remaining >= binBytes.length + 1 + 4) {
      buffer.put(MagicNumbers.Format.DATETIME);
      buffer.putInt(binBytes.length);
      buffer.put(binBytes);
      s.written = MagicNumbers.Format.DATETIME;
      return true;
    } else if (remaining > 1 + 4 + 4 + 4) {
      int lenToPut = remaining - (1 + 4 + 4 + 4);
      buffer.put(MagicNumbers.Format.DATETIME_PART);
      buffer.putInt(binBytes.length);
      buffer.putInt(0);
      buffer.putInt(lenToPut);
      buffer.put(binBytes, 0, lenToPut);
      s.written = MagicNumbers.Format.DATETIME_PART;
      s.sidx = lenToPut;
      s.bytes = binBytes;
      return true;
    }
    s.written = MagicNumbers.Format.NOTHING;
    return false;
  }

  private boolean writeDatetimePart() {
    byte[] binBytes = s.bytes;
    int remaining = buffer.remaining();
    if (remaining > 1 + 4 + 4 + 4) {
      int lenToPut = Math.min(remaining - (1 + 4 + 4 + 4), binBytes.length-s.sidx);
      buffer.put(MagicNumbers.Format.DATETIME_PART);
      buffer.putInt(binBytes.length);
      buffer.putInt(s.sidx);
      buffer.putInt(lenToPut);
      buffer.put(binBytes, s.sidx, lenToPut);
      s.sidx += lenToPut;

      if (s.sidx == binBytes.length){
        s.written = MagicNumbers.Format.DATETIME; // report having written a full value on finish
        s.bytes = null;
        s.sidx = -1;
      }
      else{
        s.written = MagicNumbers.Format.DATETIME_PART;
        s.bytes = binBytes;
      }
      return true;
    }
    s.written = MagicNumbers.Format.NOTHING;
    return false;
  }

  private boolean writeBinary() {
    byte[] binBytes = s.v.bytes();
    int remaining = buffer.remaining();
    if (remaining >= binBytes.length + 1 + 4) {
      buffer.put(MagicNumbers.Format.BINARY);
      buffer.putInt(binBytes.length);
      buffer.put(binBytes);
      s.written = MagicNumbers.Format.BINARY;
      return true;
    } else if (remaining > 1 + 4 + 4 + 4) {
      int lenToPut = remaining - (1 + 4 + 4 + 4);
      buffer.put(MagicNumbers.Format.BINARY_PART);
      buffer.putInt(binBytes.length);
      buffer.putInt(0);
      buffer.putInt(lenToPut);
      buffer.put(binBytes, 0, lenToPut);
      s.written = MagicNumbers.Format.BINARY_PART;
      s.sidx = lenToPut;
      s.bytes = binBytes;
      return true;
    }
    s.written = MagicNumbers.Format.NOTHING;
    return false;
  }

  private boolean writeBinaryPart() {
    byte[] binBytes = s.bytes;
    int remaining = buffer.remaining();
    if (remaining > 1 + 4 + 4 + 4) {
      int lenToPut = Math.min(remaining - (1 + 4 + 4 + 4), binBytes.length-s.sidx);
      buffer.put(MagicNumbers.Format.BINARY_PART);
      buffer.putInt(binBytes.length);
      buffer.putInt(s.sidx);
      buffer.putInt(lenToPut);
      buffer.put(binBytes, s.sidx, lenToPut);
      s.sidx += lenToPut;

      if (s.sidx == binBytes.length){
        s.written = MagicNumbers.Format.BINARY; // report having written a full binary on finish
        s.bytes = null;
        s.sidx = -1;
      }
      else{
        s.written = MagicNumbers.Format.BINARY_PART;
        s.bytes = binBytes;
      }
      return true;
    }
    s.written = MagicNumbers.Format.NOTHING;
    return false;
  }

  private boolean writeDictKey() {
    byte[] strBytes = s.key.getBytes(StandardCharsets.UTF_8);
    int remaining = buffer.remaining();
    if (remaining >= strBytes.length + 1 + 4) {
      buffer.put(MagicNumbers.Format.KEY);
      buffer.putInt(strBytes.length);
      buffer.put(strBytes);
      s.written = MagicNumbers.Format.KEY;
      s.keyWritten = true;
      return true;
    } else if (remaining > 1 + 4 + 4 + 4) {
      int lenToPut = remaining - (1 + 4 + 4 + 4);
      buffer.put(MagicNumbers.Format.KEY_PART);
      buffer.putInt(strBytes.length);
      buffer.putInt(0);
      buffer.putInt(lenToPut);
      buffer.put(strBytes, 0, lenToPut);
      s.written = MagicNumbers.Format.KEY_PART;
      s.sidx = lenToPut;
      s.bytes = strBytes;
      return true;
    }
    s.written = MagicNumbers.Format.NOTHING;
    return false;
  }

  private boolean writeKeyPart() {
    byte[] strBytes = s.bytes;
    int remaining = buffer.remaining();
    if (remaining > 1 + 4 + 4 + 4) {
      int lenToPut = Math.min(remaining - (1 + 4 + 4 + 4), strBytes.length-s.sidx);
      buffer.put(MagicNumbers.Format.KEY_PART);
      buffer.putInt(strBytes.length);
      buffer.putInt(s.sidx);
      buffer.putInt(lenToPut);
      buffer.put(strBytes, s.sidx, lenToPut);
      s.sidx += lenToPut;

      if (s.sidx == strBytes.length){
        s.written = MagicNumbers.Format.KEY; // report having written a full key on finish
        s.bytes = null;
        s.sidx = -1;
        s.keyWritten = true;
      }
      else{
        s.written = MagicNumbers.Format.KEY_PART;
        s.bytes = strBytes;
      }
      return true;
    }
    s.written = MagicNumbers.Format.NOTHING;
    return false;
  }

  private boolean writeString() {
    byte[] strBytes = s.v.string().getBytes(StandardCharsets.UTF_8);
    int remaining = buffer.remaining();
    if (remaining >= strBytes.length + 1 + 4) {
      buffer.put(MagicNumbers.Format.STRING);
      buffer.putInt(strBytes.length);
      buffer.put(strBytes);
      s.written = MagicNumbers.Format.STRING;
      return true;
    } else if (remaining > 1 + 4 + 4 + 4) {
      int lenToPut = remaining - (1 + 4 + 4 + 4);
      buffer.put(MagicNumbers.Format.STRING_PART);
      buffer.putInt(strBytes.length);
      buffer.putInt(0);
      buffer.putInt(lenToPut);
      buffer.put(strBytes, 0, lenToPut);
      s.written = MagicNumbers.Format.STRING_PART;
      s.sidx = lenToPut;
      s.bytes = strBytes;
      return true;
    }
    s.written = MagicNumbers.Format.NOTHING;
    return false;
  }

  private boolean writeDecimal() {
    byte[] strBytes = s.v.decimal().toString().getBytes(StandardCharsets.UTF_8);
    int remaining = buffer.remaining();
    if (remaining >= strBytes.length + 1 + 4) {
      buffer.put(MagicNumbers.Format.DECIMAL);
      buffer.putInt(strBytes.length);
      buffer.put(strBytes);
      s.written = MagicNumbers.Format.DECIMAL;
      return true;
    } else if (remaining > 1 + 4 + 4 + 4) {
      int lenToPut = remaining - (1 + 4 + 4 + 4);
      buffer.put(MagicNumbers.Format.DECIMAL_PART);
      buffer.putInt(strBytes.length);
      buffer.putInt(0);
      buffer.putInt(lenToPut);
      buffer.put(strBytes, 0, lenToPut);
      s.written = MagicNumbers.Format.DECIMAL_PART;
      s.sidx = lenToPut;
      s.bytes = strBytes;
      return true;
    }
    s.written = MagicNumbers.Format.NOTHING;
    return false;
  }
  private boolean writeDecimalPart() {
    byte[] strBytes = s.bytes;
    int remaining = buffer.remaining();
    if (remaining > 1 + 4 + 4 + 4) {
      int lenToPut = Math.min(remaining - (1 + 4 + 4 + 4), strBytes.length-s.sidx);
      buffer.put(MagicNumbers.Format.DECIMAL_PART);
      buffer.putInt(strBytes.length);
      buffer.putInt(s.sidx);
      buffer.putInt(lenToPut);
      buffer.put(strBytes, s.sidx, lenToPut);
      s.sidx += lenToPut;

      if (s.sidx == strBytes.length){
        s.written = MagicNumbers.Format.DECIMAL; // report having written a full value  on finish
        s.bytes = null;
        s.sidx = -1;
      }
      else{
        s.written = MagicNumbers.Format.DECIMAL_PART;
        s.bytes = strBytes;
      }
      return true;
    }
    s.written = MagicNumbers.Format.NOTHING;
    return false;
  }

  private boolean writeList() {
    ListValue list = s.v.list();
    int remaining = buffer.remaining();
    if (remaining >= 1 + 4) {
      buffer.put(MagicNumbers.Format.LIST);
      buffer.putInt(list.size());
      if (list.size() > 0){
        t.push(s);
        s = new State(s.v, list.get(0), 0, null, null);
        s.written = MagicNumbers.Format.LIST_HEAD;
      }
      else {
        s.written = MagicNumbers.Format.LIST;
      }
      return true;
    }
    s.written = MagicNumbers.Format.NOTHING;
    return false;
  }

  private boolean writeDict() {
    DictValue d = s.v.dict();
    int remaining = buffer.remaining();
    if (remaining >= 1 + 4) {
      buffer.put(MagicNumbers.Format.DICT);
      buffer.putInt(d.size());
      if (d.size() > 0){
        t.push(s);
        Iterator<String> keys = d.keyIterator();
        String key = keys.next();
        s = new State(s.v, d.get(key), -1, key, keys);
        s.written = MagicNumbers.Format.DICT_HEAD;
      }
      else {
        s.written = MagicNumbers.Format.DICT;
      }
      return true;
    }
    s.written = MagicNumbers.Format.NOTHING;
    return false;
  }

  private boolean writeStringPart() {
    byte[] strBytes = s.bytes;
    int remaining = buffer.remaining();
    if (remaining > 1 + 4 + 4 + 4) {
      int lenToPut = Math.min(remaining - (1 + 4 + 4 + 4), strBytes.length-s.sidx);
      buffer.put(MagicNumbers.Format.STRING_PART);
      buffer.putInt(strBytes.length);
      buffer.putInt(s.sidx);
      buffer.putInt(lenToPut);
      buffer.put(strBytes, s.sidx, lenToPut);
      s.sidx += lenToPut;

      if (s.sidx == strBytes.length){
        s.written = MagicNumbers.Format.STRING; // report having written a full string on finish
        s.bytes = null;
        s.sidx = -1;
      }
      else{
        s.written = MagicNumbers.Format.STRING_PART;
        s.bytes = strBytes;
      }
      return true;
    }
    s.written = MagicNumbers.Format.NOTHING;
    return false;
  }

  private boolean writeBoolean() {
    if (buffer.remaining() >= 2) {
      buffer.put(MagicNumbers.Format.BOOLEAN);
      buffer.put((byte) (s.v.bool() ? 1 : 0));
      s.written = MagicNumbers.Format.BOOLEAN;
      return true;
    }
    s.written = MagicNumbers.Format.NOTHING;
    return false;
  }

  private boolean writeNil() {
    if (buffer.hasRemaining()) {
      buffer.put(MagicNumbers.Format.VOID);
      s.written = MagicNumbers.Format.VOID;
      return true;
    }
    s.written = MagicNumbers.Format.NOTHING;
    return false;
  }

  private boolean writeLong() {
    if (buffer.remaining() >= 9) {
      buffer.put(MagicNumbers.Format.LONG);
      buffer.putLong(s.v.longNum());
      s.written = MagicNumbers.Format.LONG;
      return true;
    }
    s.written = MagicNumbers.Format.NOTHING;
    return false;
  }

  private boolean writeDouble() {
    if (buffer.remaining() >= 9) {
      buffer.put(MagicNumbers.Format.DOUBLE);
      buffer.putDouble(s.v.doubleNum());
      s.written = MagicNumbers.Format.DOUBLE;
      return true;
    }
    s.written = MagicNumbers.Format.NOTHING;
    return false;
  }

  /**
   * @return whether ready to write next value into current chunk
   */
  private boolean nextState() {

    byte magicByte = s.written;
    switch (magicByte) {
      // nothing done, too little space
      case MagicNumbers.Format.NOTHING:
      // partials  written
      case MagicNumbers.Format.KEY_PART:
      case MagicNumbers.Format.STRING_PART:
      case MagicNumbers.Format.BINARY_PART:
      case MagicNumbers.Format.DECIMAL_PART:
      case MagicNumbers.Format.DATETIME_PART:
        return false;
        // container head done
      case MagicNumbers.Format.LIST_HEAD:
      case MagicNumbers.Format.DICT_HEAD:
        return true;
        // key done
      case MagicNumbers.Format.KEY:
        return true;
      // leaf done
      case MagicNumbers.Format.VOID:
      case MagicNumbers.Format.BOOLEAN:
      case MagicNumbers.Format.LONG:
      case MagicNumbers.Format.DOUBLE:
      case MagicNumbers.Format.STRING:
      case MagicNumbers.Format.BINARY:
      case MagicNumbers.Format.DECIMAL:
      case MagicNumbers.Format.DATETIME:
      case MagicNumbers.Format.LIST:
      case MagicNumbers.Format.DICT:
        if (s.c.isNil()) {
          s = null;
          return false;
        } else if (s.c.isList()) {
          s.idx++;
          if (s.idx < s.c.list().size()){
            // write next index
            s.v = s.c.list().get(s.idx);
          }
          else{
            // finished writing the list as a whole
            s = t.pop();
            s.written = MagicNumbers.Format.LIST;
            return nextState();
          }
          return true;
        } else if (s.c.isDict()){
          s.key = s.keys.hasNext() ? s.keys.next() : null;
          s.keyWritten = false;
          if (s.key != null){
            // write next key
            s.v = s.c.dict().get(s.key);
            return true;
          } else{
            // finished writing the dict as a whole
            s = t.pop();
            s.written = MagicNumbers.Format.DICT;
            return nextState();
          }
        }
      case MagicNumbers.Format.FUNCTION:
      default:
        throw new RuntimeException("Unsupported value type: " + s.v.type().name());
    }

  }

  public byte[] nextChunk() {
    if (s == null) return null;
    writeNextChunk();
    byte[] ret = new byte[buffer.position()];
    System.arraycopy(buffer.array(), 0, ret, 0, buffer.position());
    buffer.rewind();
    return ret;
  }


}


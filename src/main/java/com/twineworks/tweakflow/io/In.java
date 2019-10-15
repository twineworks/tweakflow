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

import com.twineworks.tweakflow.lang.values.Value;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;
import java.util.ArrayDeque;

import static com.twineworks.tweakflow.util.InOut.MiB;

public class In implements AutoCloseable {

  private final ReadableByteChannel channel;
  private final ByteBuffer buffer;
  private final ArrayDeque<ValueDeserializer> deserializers;
  private boolean channelDepleted = false;

  private final LongSerializer longSerializer = new LongSerializer();
  private final DoubleSerializer doubleSerializer = new DoubleSerializer();
  private final DecimalSerializer decimalSerializer = new DecimalSerializer();
  private final StringSerializer stringSerializer = new StringSerializer();
  private final BinarySerializer binarySerializer = new BinarySerializer();
  private final DatetimeSerializer datetimeSerializer = new DatetimeSerializer();
  private final ListSerializer listSerializer = new ListSerializer();
  private final DictSerializer dictSerializer = new DictSerializer();
  private final VoidSerializer voidSerializer = new VoidSerializer();

  public In(ReadableByteChannel channel) {
    this(channel, 8*MiB);
  }

  public In(ReadableByteChannel channel, int bufferSize) {
    this.channel = channel;
    buffer = ByteBuffer.allocate(bufferSize);
    buffer.clear();
    buffer.limit(0);
    deserializers = new ArrayDeque<>();
  }

  private void nextBuffer() throws IOException {
      // Prepare the buffer for reading
      if (buffer.position() < buffer.limit()) {
        // account for any unread bytes from the previous buffer
        buffer.compact();
      } else {
        buffer.clear();
      }

      channelDepleted = (channel.read(buffer) == -1);
      buffer.flip();

  }

  private Value fromBuffer(byte format) throws IOException {

    ValueDeserializer d = deserializers.peek();

    while(!d.get(this, format, buffer)){
      if (channelDepleted) throw new IOException("Invalid data format: premature EOF");
      nextBuffer();
    }
    return d.getSubject();

  }

  public Value readNext() throws IOException {

    if (channelDepleted) return null;

    // find next format
    if (!buffer.hasRemaining()){
      nextBuffer();
      if (!buffer.hasRemaining() && channelDepleted) return null;
    }

    byte format = buffer.get();
    ValueDeserializer d;

    switch (format){
      case MagicNumbers.Format.VOID:
        d = voidSerializer;
        break;
      case MagicNumbers.Format.LONG:
        d = longSerializer;
        break;
      case MagicNumbers.Format.DECIMAL:
        d = decimalSerializer;
        break;
      case MagicNumbers.Format.DOUBLE:
        d = doubleSerializer;
        break;
      case MagicNumbers.Format.STRING:
        d = stringSerializer;
        break;
      case MagicNumbers.Format.BINARY:
        d = binarySerializer;
        break;
      case MagicNumbers.Format.DATETIME:
        d = datetimeSerializer;
        break;
      case MagicNumbers.Format.LIST:
        d = listSerializer;
        break;
      case MagicNumbers.Format.DICT:
        d = dictSerializer;
        break;
      default:
        throw new IOException("Unknown format: "+format);
    }

    deserializers.push(d);
    Value ret = fromBuffer(format);
    deserializers.pop();
    return ret;
  }

  @Override
  public void close() {
  }
}

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
import com.twineworks.tweakflow.lang.values.Values;

import java.nio.ByteBuffer;
import java.util.Arrays;

class BinarySerializer implements ValueSerializer, ValueDeserializer {

  private Value subject;

  private boolean writtenSize = false;
  private boolean readSize = false;

  private ByteBuffer bytes;

  @Override
  public void setSubject(Value subject) {
    this.subject = subject;
    bytes = ByteBuffer.wrap(subject.bytes());

    writtenSize = false;
  }

  @Override
  public boolean put(Out out, ByteBuffer buffer) {

    if (!writtenSize){
      if (buffer.remaining() >= 5){
        buffer.put(MagicNumbers.Format.BINARY);
        buffer.putInt(bytes.limit());
        writtenSize = true;
      }
      else{
        return false;
      }
    }

    if (buffer.remaining() >= bytes.remaining()){
      buffer.put(bytes);
    }
    else{
      // need to write out bytes
      while (bytes.hasRemaining() && buffer.hasRemaining()){
        buffer.put(bytes.get());
      }
    }

    return !bytes.hasRemaining();

  }

  @Override
  public Value getSubject() {
    return subject;
  }

  private boolean getFromBinaryFormat(ByteBuffer buffer){

    if (!readSize){
      if (buffer.remaining() >= 4){
        int bytesSize = buffer.getInt();
        if (bytes == null || bytes.capacity() < bytesSize){
          bytes = ByteBuffer.allocate(bytesSize);
        }
        else{
          bytes.position(0);
          bytes.limit(bytesSize);
        }
        readSize = true;
      }
      else{
        return false;
      }
    }

    if (bytes.remaining() >= buffer.remaining()){
      bytes.put(buffer);
    }
    else{
      while(buffer.hasRemaining() && bytes.hasRemaining()){
        bytes.put(buffer.get());
      }
    }

    if (bytes.hasRemaining()) return false;


    byte[] arr = bytes.array();
    subject = Values.make(Arrays.copyOf(arr, bytes.position()));

    bytes.position(0);
    readSize = false;

    return true;

  }

  @Override
  public boolean get(In in, byte format, ByteBuffer buffer) {
    switch (format){
      case MagicNumbers.Format.BINARY: {
        return getFromBinaryFormat(buffer);
      }
      default: {
        throw new AssertionError("Unknown format: "+format);
      }
    }
  }
}

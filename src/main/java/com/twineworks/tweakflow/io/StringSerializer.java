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
import java.nio.CharBuffer;
import java.nio.charset.StandardCharsets;

class StringSerializer implements ValueSerializer, ValueDeserializer {

  private Value subject;

  private boolean writtenSize = false;
  private boolean readSize = false;

  private ByteBuffer strBytes;
  private String lastStr;

  @Override
  public void setSubject(Value subject) {
    this.subject = subject;
    String str = subject.string();

    if (!str.equals(lastStr)){
      lastStr = str;
      strBytes = StandardCharsets.UTF_8.encode(str);
    }
    else{
      strBytes.position(0);
    }

    writtenSize = false;
  }

  @Override
  public boolean put(Out out, ByteBuffer buffer) {


    if (!writtenSize){
      if (buffer.remaining() >= 5){
        buffer.put(MagicNumbers.Format.STRING);
        buffer.putInt(strBytes.limit());
        writtenSize = true;
      }
      else{
        return false;
      }
    }

    // need to write out bytes
    while (strBytes.hasRemaining() && buffer.hasRemaining()){
      buffer.put(strBytes.get());
    }

    return !strBytes.hasRemaining();

  }

  @Override
  public Value getSubject() {
    return subject;
  }

  private boolean getFromStringFormat(ByteBuffer buffer){

    if (!readSize){
      if (buffer.remaining() >= 4){
        int bytesSize = buffer.getInt();
        if (strBytes == null || strBytes.capacity() < bytesSize){
          strBytes = ByteBuffer.allocate(bytesSize);
        }
        else{
          strBytes.position(0);
          strBytes.limit(bytesSize);
        }
        readSize = true;
      }
      else{
        return false;
      }
    }

    while(buffer.hasRemaining() && strBytes.hasRemaining()){
      strBytes.put(buffer.get());
    }

    if (strBytes.hasRemaining()) return false;

    strBytes.position(0);
    CharBuffer decoded = StandardCharsets.UTF_8.decode(strBytes);
    subject = Values.make(decoded.toString());

    readSize = false;

    return true;

  }

  @Override
  public boolean get(In in, byte format, ByteBuffer buffer) {
    switch (format){
      case MagicNumbers.Format.STRING: {
        return getFromStringFormat(buffer);
      }
      default: {
        throw new AssertionError("Unknown format: "+format);
      }
    }
  }
}

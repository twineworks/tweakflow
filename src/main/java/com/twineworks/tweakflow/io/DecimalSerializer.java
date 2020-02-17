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

import java.math.BigDecimal;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.StandardCharsets;

class DecimalSerializer implements ValueSerializer, ValueDeserializer {

  private Value subject;

  private boolean writtenSize = false;
  private boolean readSize = false;

  private ByteBuffer decBytes;
  private BigDecimal lastStr;

  @Override
  public void setSubject(Value subject) {
    this.subject = subject;
    BigDecimal dec = subject.decimal();

    if (!dec.equals(lastStr)){
      lastStr = dec;
      decBytes = StandardCharsets.UTF_8.encode(dec.toString());
    }
    else{
      decBytes.position(0);
    }

    writtenSize = false;
  }

  @Override
  public boolean put(Out out, ByteBuffer buffer) {


    if (!writtenSize){
      if (buffer.remaining() >= 5){
        buffer.put(MagicNumbers.Format.DECIMAL);
        buffer.putInt(decBytes.limit());
        writtenSize = true;
      }
      else{
        return false;
      }
    }

    // need to write out bytes
    if (buffer.remaining() >= decBytes.remaining()){
      buffer.put(decBytes);
    }
    else{
      while (decBytes.hasRemaining() && buffer.hasRemaining()){
        buffer.put(decBytes.get());
      }
    }

    return !decBytes.hasRemaining();

  }

  @Override
  public Value getSubject() {
    return subject;
  }

  private boolean getFromDecimalFormat(ByteBuffer buffer){

    if (!readSize){
      if (buffer.remaining() >= 4){
        int bytesSize = buffer.getInt();
        if (decBytes == null || decBytes.capacity() < bytesSize){
          decBytes = ByteBuffer.allocate(bytesSize);
        }
        else{
          decBytes.position(0);
          decBytes.limit(bytesSize);
        }
        readSize = true;
      }
      else{
        return false;
      }
    }

    if (decBytes.remaining() >= buffer.remaining()){
      decBytes.put(buffer);
    }
    else{
      while(buffer.hasRemaining() && decBytes.hasRemaining()){
        decBytes.put(buffer.get());
      }
    }

    if (decBytes.hasRemaining()) return false;

    decBytes.position(0);
    CharBuffer decoded = StandardCharsets.UTF_8.decode(decBytes);
    subject = Values.make(new BigDecimal(decoded.toString()));

    readSize = false;

    return true;

  }

  @Override
  public boolean get(In in, byte format, ByteBuffer buffer) {
    switch (format){
      case MagicNumbers.Format.DECIMAL: {
        return getFromDecimalFormat(buffer);
      }
      default: {
        throw new AssertionError("Unknown format: "+format);
      }
    }
  }
}

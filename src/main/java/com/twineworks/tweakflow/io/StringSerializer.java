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
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CoderResult;
import java.nio.charset.StandardCharsets;

class StringSerializer implements ValueSerializer, ValueDeserializer {

  private Value subject;

  private boolean writtenSize = false;
  private boolean readSize = false;

  private ByteBuffer strBytes;
  private CharBuffer charBuffer;
  private String lastStr;
  private CharsetEncoder encoder;
  private CharsetDecoder decoder;

  @Override
  public void setSubject(Value subject) {
    this.subject = subject;
    String str = subject.string();

    if (!str.equals(lastStr)){
      lastStr = str;
      if (encoder == null){
        encoder = StandardCharsets.UTF_16BE.newEncoder();
      }

      int len2 = str.length()*2;
      if (strBytes == null){
        strBytes = ByteBuffer.allocate(Math.max(len2, 256));
      }

      if (strBytes.capacity() < len2){
        strBytes = ByteBuffer.allocate(len2);
      }

      if (charBuffer == null || charBuffer.capacity() < str.length()) {
        charBuffer = CharBuffer.allocate(Math.max(str.length(), 128));
      }

      charBuffer.clear();
      charBuffer.append(str);
      charBuffer.flip();

      strBytes.clear();
      encoder.encode(charBuffer, strBytes, true);
      strBytes.flip();

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

    if (buffer.remaining() >= strBytes.remaining()){
      buffer.put(strBytes);
      return true;
    }

    // need to write out only some bytes
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
          strBytes = ByteBuffer.allocate(Math.max(bytesSize, 256));
        }
        else{
          strBytes.position(0);
        }
        strBytes.limit(bytesSize);
        readSize = true;
      }
      else{
        return false;
      }
    }

    if (strBytes.remaining() >= buffer.remaining()){
      strBytes.put(buffer);
    }
    else{
      while(buffer.hasRemaining() && strBytes.hasRemaining()){
        strBytes.put(buffer.get());
      }
    }

    if (strBytes.hasRemaining()) return false;

    if (decoder == null){
      decoder = StandardCharsets.UTF_16BE.newDecoder();
    }

    if (charBuffer == null || charBuffer.capacity() < strBytes.capacity()/2){
      charBuffer = CharBuffer.allocate(Math.max(strBytes.capacity()/2, 128));
    }

    strBytes.flip();
    charBuffer.clear();
    CoderResult result = decoder.decode(strBytes, charBuffer, true);
    strBytes.flip();
    subject = Values.make(charBuffer.flip().toString());

    readSize = false;

    return true;

  }

  @Override
  public boolean get(In in, byte format, ByteBuffer buffer) {
    if (format == MagicNumbers.Format.STRING) {
      return getFromStringFormat(buffer);
    }
    throw new AssertionError("Unknown format: " + format);
  }
}

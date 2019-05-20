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

class DoubleSerializer implements ValueSerializer, ValueDeserializer {

  private Value subject;

  @Override
  public void setSubject(Value subject) {
    this.subject = subject;
  }

  @Override
  public boolean put(Out out, ByteBuffer buffer) {

    if (buffer.remaining() >= 9){
      buffer.put(MagicNumbers.Format.DOUBLE);
      buffer.putDouble(subject.doubleNum());
      return true;
    }
    else{
      return false;
    }

  }

  @Override
  public Value getSubject() {
    return subject;
  }

  @Override
  public boolean get(In in, byte format, ByteBuffer buffer) {
    switch (format){
      case MagicNumbers.Format.DOUBLE: {
        if (buffer.remaining() >= 8){
          subject = Values.make(buffer.getDouble());
          return true;
        }
        return false;
      }
      default: {
        throw new AssertionError("Unknown format: "+format);
      }
    }
  }
}

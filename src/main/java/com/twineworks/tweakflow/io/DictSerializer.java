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

import com.twineworks.tweakflow.lang.values.*;

import java.io.IOException;
import java.nio.ByteBuffer;

class DictSerializer implements ValueSerializer, ValueDeserializer {

  private Value subject;

  @Override
  public void setSubject(Value subject) {
    this.subject = subject;
  }

  @Override
  public boolean put(Out out, ByteBuffer buffer) throws IOException {

    if (buffer.remaining() >= 5){
      DictValue dictValue = subject.dict();
      buffer.put(MagicNumbers.Format.DICT);
      buffer.putInt(dictValue.size());
      for (String k : dictValue.keys()) {
        out.write(Values.make(k));
        out.write(dictValue.get(k));
      }
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
  public boolean get(In in, byte format, ByteBuffer buffer) throws IOException {
    switch (format){
      case MagicNumbers.Format.DICT: {
        if (buffer.remaining() >= 4){
          TransientDictValue t = new TransientDictValue();
          int size = buffer.getInt();
          for (int i=0;i<size;i++){
            String k = in.readNext().string();
            Value v = in.readNext();
            t.put(k, v);
          }
          subject = Values.make(t.persistent());
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

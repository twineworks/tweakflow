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

import com.twineworks.tweakflow.lang.values.ListValue;
import com.twineworks.tweakflow.lang.values.Value;
import com.twineworks.tweakflow.lang.values.Values;

import java.io.IOException;
import java.nio.ByteBuffer;

class ListSerializer implements ValueSerializer, ValueDeserializer {

  private Value subject;

  @Override
  public void setSubject(Value subject) {
    this.subject = subject;
  }

  @Override
  public boolean put(Out out, ByteBuffer buffer) throws IOException {

    if (buffer.remaining() >= 5){
      ListValue listValue = subject.list();
      buffer.put(MagicNumbers.Format.LIST);
      buffer.putInt(listValue.size());
      for (Value value : listValue) {
        out.write(value);
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
      case MagicNumbers.Format.LIST: {
        if (buffer.remaining() >= 4){
          ListValue listValue = new ListValue();
          int size = buffer.getInt();
          for (int i=0;i<size;i++){
            listValue = listValue.append(in.readNext());
          }
          subject = Values.make(listValue);
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

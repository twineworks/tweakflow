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

package com.twineworks.tweakflow.spec.reporter.helpers;

public class HumanReadable {

  /**
   * Returns a string of format hh:mm:ss.SSS
   * @param ms
   * @return
   */
  public static String formatDuration(long ms){

    StringBuilder s = new StringBuilder();
    long hours = ms / 1000 / 60 / 60;
    long minutes = (ms - (hours * 1000 * 60 * 60)) / 1000 / 60;
    long seconds = (ms - (hours * 1000 * 60 * 60) - (minutes * 1000 * 60)) / 1000;

    boolean showHours = hours > 0;
    boolean showMinutes = showHours || minutes > 0;

    if (showHours){
      if (hours < 10){
        s.append("0");
      }
      s.append(hours).append("h ");
    }

    if (showMinutes){
      if (minutes < 10){
        s.append("0");
      }
      s.append(minutes).append("m ");
    }
    if (showMinutes){
      if (seconds < 10){
        s.append("0");
      }
    }
    if (!showHours && !showMinutes && seconds == 0){
      s.append("<1").append("s");
    }
    else{
      s.append(seconds).append("s");
    }

    return s.toString();
  }
}

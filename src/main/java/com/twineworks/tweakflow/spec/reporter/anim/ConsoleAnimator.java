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

package com.twineworks.tweakflow.spec.reporter.anim;

import java.util.Timer;
import java.util.TimerTask;

public class ConsoleAnimator {

  private Timer timer;
  private final Object timerLock = new Object();
  private boolean timerActive;
  private ConsoleAnimation animation;

  public void startAnimation(ConsoleAnimation a){
    this.animation = a;
    this.animation.start();
    timer = new Timer();
    timerActive = true;
    timer.scheduleAtFixedRate(new TimerTask() {
      @Override
      public void run() {
        synchronized (timerLock){
          if (timerActive){
            animation.tick();
          }
        }
      }
    }, 0, 500);
  }

  public void finishAnimation(){
    // cancel any compilation animation
    if (timer != null){
      synchronized (timerLock){
        timer.cancel();
        timer = null;
        timerActive = false;
        if (animation != null){
          animation.finish();
          animation = null;
        }
      }
    }
  }
}

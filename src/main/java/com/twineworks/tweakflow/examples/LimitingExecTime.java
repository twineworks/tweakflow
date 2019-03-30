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

package com.twineworks.tweakflow.examples;

import com.twineworks.tweakflow.lang.TweakFlow;
import com.twineworks.tweakflow.lang.errors.LangException;
import com.twineworks.tweakflow.lang.values.Value;
import com.twineworks.tweakflow.lang.values.ValueInspector;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class LimitingExecTime {

  @SuppressWarnings("deprecation")
  private static void stopTask(FutureTask task, Thread taskThread) {
    // ask the thread to stop
    task.cancel(true);
    try {
      // give it some time to cleanly finish
      taskThread.join(100);
    } catch (InterruptedException ignored) {
    }

    // forcibly stop it, if it did not cleanly finish
    if (taskThread.isAlive()) {
      taskThread.stop();
    }
  }

  public static void main(String[] args) {

    // evaluating naive implementation of fibonacci function
    // with exponential runtime complexity causing O(2^x) recursive calls
    String[] expressions = {
        "let { f: (x) -> if x > 2 then f(x-2)+f(x-1) else 1 } f(1)",
        "let { f: (x) -> if x > 2 then f(x-2)+f(x-1) else 1 } f(5)",
        "let { f: (x) -> if x > 2 then f(x-2)+f(x-1) else 1 } f(10)",
        "let { f: (x) -> if x > 2 then f(x-2)+f(x-1) else 1 } f(20)",
        "let { f: (x) -> if x > 2 then f(x-2)+f(x-1) else 1 } f(30)",
        "let { f: (x) -> if x > 2 then f(x-2)+f(x-1) else 1 } f(40)",
        "let { f: (x) -> if x > 2 then f(x-2)+f(x-1) else 1 } f(50)",
        "let { f: (x) -> if x > 2 then f(x-2)+f(x-1) else 1 } f(60)",
        "let { f: (x) -> if x > 2 then f(x-2)+f(x-1) else 1 } f(70)",
        "let { f: (x) -> if x > 2 then f(x-2)+f(x-1) else 1 } f(80)",
        "let { f: (x) -> if x > 2 then f(x-2)+f(x-1) else 1 } f(90)",
        "let { f: (x) -> if x > 2 then f(x-2)+f(x-1) else 1 } f(100)",
    };

    for (String expression : expressions) {
      FutureTask<Value> task = new FutureTask<>(() -> TweakFlow.evaluate(expression));
      Thread thread = new Thread(task);
      try {
        System.out.println("evaluating: " + expression);
        thread.start();
        Value value = task.get(1000, TimeUnit.MILLISECONDS);
        // successful evaluation
        System.out.println("result: " + ValueInspector.inspect(value));
      } catch (ExecutionException e) {
        // evaluation failed
        if (e.getCause() instanceof LangException) {
          LangException langException = (LangException) e.getCause();
          System.out.println(langException.getDigestMessage());
        } else {
          e.getCause().printStackTrace();
        }

      } catch (TimeoutException e) {
        // evaluation timed out
        System.out.println("could not evaluate expression in time, skipping");
        stopTask(task, thread);

      } catch (InterruptedException e) {
        // main program got interrupted (Ctrl+C)
        System.out.println("program interrupted");
        stopTask(task, thread);
        break;
      }

    }
  }

}

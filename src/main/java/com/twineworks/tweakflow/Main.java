/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2017 Twineworks GmbH
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

package com.twineworks.tweakflow;

import com.twineworks.tweakflow.doc.DocMain;
import com.twineworks.tweakflow.repl.Repl;
import com.twineworks.tweakflow.run.Run;

import java.util.Arrays;

public class Main {

  private static void printUsage(){
    System.out.println("usage: tweakflow [repl | run | doc] [args]");
  }

  public static void main(String[] args){

    // no args -> print usage
    if (args.length == 0){
      printUsage();
    }
    else if (args[0].equals("repl")){
      Repl.main(Arrays.copyOfRange(args, 1, args.length));
    }
    else if (args[0].equals("run")){
      Run.main(Arrays.copyOfRange(args, 1, args.length));
    }
    else if (args[0].equals("doc")){
      DocMain.main(Arrays.copyOfRange(args, 1, args.length));
    }
    else{
      // unknown args -> print usage
      printUsage();
    }

  }

}

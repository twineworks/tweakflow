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

import com.twineworks.tweakflow.lang.runtime.Runtime;
import com.twineworks.tweakflow.spec.nodes.*;
import com.twineworks.tweakflow.spec.reporter.SpecReporter;
import com.twineworks.tweakflow.spec.runner.SpecRunner;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

public class SpecReporterDelegate implements SpecReporter {

  private final ArrayList<SpecReporter> reporters;

  public SpecReporterDelegate(Collection<SpecReporter> reporters) {
    this.reporters = new ArrayList<>(reporters);
  }

  @Override
  public void onFoundSpecModules(SpecRunner specRunner) {

    for (SpecReporter reporter : reporters) {
      reporter.onFoundSpecModules(specRunner);
    }
  }

  @Override
  public void onEnterSuite(SuiteNode node) {

    for (SpecReporter reporter : reporters) {
      reporter.onEnterSuite(node);
    }
  }

  @Override
  public void onEnterDescribe(DescribeNode node) {
    for (SpecReporter reporter : reporters) {
      reporter.onEnterDescribe(node);
    }
  }

  @Override
  public void onEnterBefore(BeforeNode node) {
    for (SpecReporter reporter : reporters) {
      reporter.onEnterBefore(node);
    }
  }

  @Override
  public void onLeaveBefore(BeforeNode node) {
    for (SpecReporter reporter : reporters) {
      reporter.onLeaveBefore(node);
    }
  }

  @Override
  public void onEnterAfter(AfterNode node) {
    for (SpecReporter reporter : reporters) {
      reporter.onEnterAfter(node);
    }
  }

  @Override
  public void onLeaveAfter(AfterNode node) {
    for (SpecReporter reporter : reporters) {
      reporter.onLeaveAfter(node);
    }
  }

  @Override
  public void onEnterSubject(SpecNode node) {
    for (SpecReporter reporter : reporters) {
      reporter.onEnterSubject(node);
    }
  }

  @Override
  public void onLeaveSubject(SpecNode node) {
    for (SpecReporter reporter : reporters) {
      reporter.onLeaveSubject(node);
    }
  }

  @Override
  public void onLeaveDescribe(DescribeNode node) {
    for (SpecReporter reporter : reporters) {
      reporter.onLeaveDescribe(node);
    }
  }

  @Override
  public void onEnterIt(ItNode node) {
    for (SpecReporter reporter : reporters) {
      reporter.onEnterIt(node);
    }

  }

  @Override
  public void onLeaveIt(ItNode node) {
    for (SpecReporter reporter : reporters) {
      reporter.onLeaveIt(node);
    }
  }

  @Override
  public void onLeaveSuite(SuiteNode node) {
    for (SpecReporter reporter : reporters) {
      reporter.onLeaveSuite(node);
    }
  }

  @Override
  public void onEnterFile(FileNode node) {
    for (SpecReporter reporter : reporters) {
      reporter.onEnterFile(node);
    }
  }

  @Override
  public void onLeaveFile(FileNode node) {
    for (SpecReporter reporter : reporters) {
      reporter.onLeaveFile(node);
    }
  }

  @Override
  public void setOptions(Map<String, String> options) {
    for (SpecReporter reporter : reporters) {
      reporter.setOptions(options);
    }
  }

  @Override
  public void onModuleCompiled(String module, Runtime runtime) {
    for (SpecReporter reporter : reporters) {
      reporter.onModuleCompiled(module, runtime);
    }
  }

  @Override
  public void onModuleFailedToCompile(FileNode module, Throwable error) {
    for (SpecReporter reporter : reporters) {
      reporter.onModuleFailedToCompile(module, error);
    }

  }

}

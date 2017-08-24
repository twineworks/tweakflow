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

package com.twineworks.tweakflow.lang.analysis;

import com.twineworks.tweakflow.lang.ast.UnitNode;
import com.twineworks.tweakflow.lang.load.loadpath.LoadPathLocation;

public class AnalysisUnit {

  private UnitNode unit;
  private LoadPathLocation location;
  private String path;
  private AnalysisStage stage;

  private long totalLoadDurationMillis;
  private long loadDurationMillis;
  private long parseDurationMillis;
  private long buildDurationMillis;

  public AnalysisStage getStage() {
    return stage;
  }

  public AnalysisUnit setStage(AnalysisStage stage) {
    this.stage = stage;
    return this;
  }

  public UnitNode getUnit() {
    return unit;
  }

  public AnalysisUnit setUnit(UnitNode unit) {
    this.unit = unit;
    return this;
  }

  public LoadPathLocation getLocation() {
    return location;
  }

  public AnalysisUnit setLocation(LoadPathLocation location) {
    this.location = location;
    return this;
  }

  public String getPath() {
    return path;
  }

  public AnalysisUnit setPath(String path) {
    this.path = path;
    return this;
  }

  public long getTotalLoadDurationMillis() {
    return totalLoadDurationMillis;
  }

  public long getParseDurationMillis() {
    return parseDurationMillis;
  }

  public long getBuildDurationMillis() {
    return buildDurationMillis;
  }

  public long getLoadDurationMillis() {
    return loadDurationMillis;
  }

  public AnalysisUnit setTotalLoadDurationMillis(long totalLoadDurationMillis) {
    this.totalLoadDurationMillis = totalLoadDurationMillis;
    return this;
  }

  public AnalysisUnit setLoadDurationMillis(long loadDurationMillis) {
    this.loadDurationMillis = loadDurationMillis;
    return this;
  }

  public AnalysisUnit setParseDurationMillis(long parseDurationMillis) {
    this.parseDurationMillis = parseDurationMillis;
    return this;
  }

  public AnalysisUnit setBuildDurationMillis(long buildDurationMillis) {
    this.buildDurationMillis = buildDurationMillis;
    return this;
  }
}

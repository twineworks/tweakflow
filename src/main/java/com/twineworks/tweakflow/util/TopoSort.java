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

package com.twineworks.tweakflow.util;

import java.util.*;

@SuppressWarnings("unchecked")
public class TopoSort {

  final private static class SortContext<T> {
    // linked hashsets preserve a deterministic result
    final LinkedHashSet<T> processed = new LinkedHashSet<>();
    final LinkedHashSet<T> unmarked = new LinkedHashSet<>();
    // A linked hashset preserves sensible order when reporting a cycle going from a -> b -> c -> a.
    // Making it a regular hashset might report a cycle like b -> a -> a -> c, since order is not preserved
    final LinkedHashSet<T> processing = new LinkedHashSet<>();
  }

  final public static class CyclicDependencyException extends RuntimeException {

    private final List cyclicOrder;
    private final Object cyclicItem;


    CyclicDependencyException(String message, List cyclicOrder, Object cyclicItem) {
      super(message);
      this.cyclicOrder = cyclicOrder;
      this.cyclicItem = cyclicItem;
    }

    public List getCycle() {
      return cyclicOrder;
    }

    public Object getCyclicItem() {
      return cyclicItem;
    }

    public String cycleSummary(){
      Object[] items = cyclicOrder.toArray(new Object[cyclicOrder.size()]);
      return Text.join(items, " -> ");
    }

  }

  private static <T> void visitNode(T node, Map<T, ? extends Set<T>> dependencyGraph, SortContext<T> context, List<T> order){
    // already seen?
    if (context.processing.contains(node)){
      List<T> cycle = new ArrayList<>(context.processing);
      cycle.add(node);
      throw new CyclicDependencyException("Cyclic dependency: "+node+" depends through its dependencies on itself", cycle, node);
    }

    // not seen yet?
    if (context.unmarked.contains(node)){
      context.unmarked.remove(node);
      context.processing.add(node);
      Set<T> children = dependencyGraph.get(node);
      for (T child : children) {
        visitNode(child, dependencyGraph, context, order);
      }
      context.processed.add(node);
      context.processing.remove(node);
      order.add(node);
    }

  }

  public static <T> List<T> calcTopoOrder(Map<T, ? extends Set<T>> dependencyGraph){
    ArrayList<T> order = new ArrayList<>(dependencyGraph.size());
    SortContext<T> context = new SortContext<>();
    context.unmarked.addAll(dependencyGraph.keySet());

    while(!context.unmarked.isEmpty()){
      T node = context.unmarked.iterator().next();
      visitNode(node, dependencyGraph, context, order);
    }
    return order;
  }

}

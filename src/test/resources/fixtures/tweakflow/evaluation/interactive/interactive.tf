interactive
  in_scope `fixtures/tweakflow/evaluation/interactive/module_a.tf` {
    e0: 0;
    e1: lib.a;
    e2: [e1, lib.a];
    e3: () -> e1;
    e4: e3();
  }

  in_scope `fixtures/tweakflow/evaluation/interactive/module_b.tf` {
    e0: 0;
    e1: lib.b;
    e2: [e1, lib.b];
    e3: () -> e1;
    e4: e3();
  }

import fun, core, data, math from "std";
import expect, expect_error, to from "std/assert.tf";

alias fun.signature as sig;
alias data.map as map;
alias data.update as update;
alias math.inc as inc;

library f {
  a: (a) -> dict {:a a};
  ab: (long a=0, long b=1) -> dict {:a a, :b b};
  abc: (a=0, b=1, c=2) -> dict {:a a, :b b, :c c};
}

library curry_spec {

  a_1:
    let {
      c: f.a(a=1);
    }
    expect(sig(c), to.be(
      {
        :return_type "dict",
        :parameters []
      }
    )) &&
    expect(c(), to.be({:a 1})) &&
    expect_error(
      () -> c(a: 1),
      to.have_code("UNEXPECTED_ARGUMENT")
    );

  ab_a_1:
    let {
      c: f.ab(a=1);
    }
    expect(sig(c), to.be(
      {
        :return_type "dict",
        :parameters [
          {
            :name "b",
            :index 0,
            :default_value 1,
            :declared_type "long"
          }
        ]
      }
    )) &&
    expect(c(), to.be({:a 1, :b 1})) &&
    expect(c(2), to.be({:a 1, :b 2})) &&
    expect(c(b:3), to.be({:a 1, :b 3}));

  ab_b_2:
    let {
      c: f.ab(b=2);
    }
    expect(sig(c), to.be(
      {
        :return_type "dict",
        :parameters [
          {
            :name "a",
            :index 0,
            :default_value 0,
            :declared_type "long"
          }
        ]
      }
    )) &&
    expect(c(), to.be({:a 0, :b 2})) &&
    expect(c(2), to.be({:a 2, :b 2})) &&
    expect(c(a:3), to.be({:a 3, :b 2}));

  abc_b4_c5:
    let {
      c: f.abc(b=4, c=5);
    }
    expect(sig(c), to.be(
      {
        :return_type "dict",
        :parameters [
          {
            :name "a",
            :index 0,
            :default_value 0,
            :declared_type "any"
          }
        ]
      }
    )) &&
    expect(c(), to.be({:a 0, :b 4, :c 5})) &&
    expect(c(2), to.be({:a 2, :b 4, :c 5})) &&
    expect(c(a:3), to.be({:a 3, :b 4, :c 5}));

  abc_b4:
    let {
      c: f.abc(b=4);
    }
    expect(sig(c), to.be(
      {
        :return_type "dict",
        :parameters [
          {
            :name "a",
            :index 0,
            :default_value 0,
            :declared_type "any"
          },
          {
            :name "c",
            :index 1,
            :default_value 2,
            :declared_type "any"
          }
        ]
      }
    )) &&
    expect(c(), to.be({:a 0, :b 4, :c 2})) &&
    expect(c(2, 3), to.be({:a 2, :b 4, :c 3})) &&
    expect(c(2, c: 3), to.be({:a 2, :b 4, :c 3}));

  map_inc:
    let {
      c: map(f=inc);
    }
    expect(sig(c), to.be(
      {
        :return_type "any",
        :parameters [{
          :name "xs",
          :index 0,
          :default_value nil,
          :declared_type "any"
        }]
      }
    )) &&
    expect(c([0,1,2]), to.be([1,2,3]));

  inc_count:
    let {
      c: update(key=:count, f=inc);
    }
    expect(sig(c),
      to.be(
        {
          :return_type "any",
          :parameters [{
            :name "xs",
            :index 0,
            :default_value nil,
            :declared_type "any"
          }]
        }
      )
    ) &&
    expect(c({:count 0}), to.be({:count 1}));

  inc_count_steps:
    let {
      update_count: update(key=:count);
      c: update_count(f=inc);
    }
    expect(sig(c),
      to.be(
        {
          :return_type "any",
          :parameters [{
            :name "xs",
            :index 0,
            :default_value nil,
            :declared_type "any"
          }]
        }
      )
    ) &&
    expect(c({:count 0}), to.be({:count 1}));

}
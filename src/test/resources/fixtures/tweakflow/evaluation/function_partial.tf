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
  abcd: (a=0, b=1, c=2, d=3) -> dict {:a a, :b b, :c c, :d d};
  abcde: (a=0, b=1, c=2, d=3, e=4) -> dict {:a a, :b b, :c c, :d d, :e e};
  abcdef: (a=0, b=1, c=2, d=3, e=4, f=5) -> dict {:a a, :b b, :c c, :d d, :e e, :f f};
}

library partial_spec {

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
    expect(c(b: 3), to.be({:a 1, :b 3}));

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
    expect(c(a: 3), to.be({:a 3, :b 2}));

  ab_a1_b2:
    let {
      c: f.ab(b=2, a=1);
    }
    expect(sig(c), to.be(
      {
        :return_type "dict",
        :parameters []
      }
    )) &&
    expect(c(), to.be({:a 1, :b 2}));

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
    expect(c(a: 3), to.be({:a 3, :b 4, :c 5}));

  abc_a4_b5:
    let {
      c: f.abc(a=4, b=5);
    }
    expect(sig(c), to.be(
      {
        :return_type "dict",
        :parameters [
          {
            :name "c",
            :index 0,
            :default_value 2,
            :declared_type "any"
          }
        ]
      }
    )) &&
    expect(c(), to.be({:a 4, :b 5, :c 2})) &&
    expect(c(3), to.be({:a 4, :b 5, :c 3})) &&
    expect(c(c: 4), to.be({:a 4, :b 5, :c 4}));

  abc_a4_c5:
    let {
      c: f.abc(a=4, c=5);
    }
    expect(sig(c), to.be(
      {
        :return_type "dict",
        :parameters [
          {
            :name "b",
            :index 0,
            :default_value 1,
            :declared_type "any"
          }
        ]
      }
    )) &&
    expect(c(), to.be({:a 4, :b 1, :c 5})) &&
    expect(c(3), to.be({:a 4, :b 3, :c 5})) &&
    expect(c(b: 4), to.be({:a 4, :b 4, :c 5}));

  abc_a4_b5_c6:
    let {
      c: f.abc(a=4, b=5, c=6);
    }
    expect(sig(c), to.be(
      {
        :return_type "dict",
        :parameters []
      }
    )) &&
    expect(c(), to.be({:a 4, :b 5, :c 6}));

  abc_a4:
    let {
      c: f.abc(a=4);
    }
    expect(sig(c), to.be(
      {
        :return_type "dict",
        :parameters [
          {
            :name "b",
            :index 0,
            :default_value 1,
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
    expect(c(), to.be({:a 4, :b 1, :c 2})) &&
    expect(c(2, 3), to.be({:a 4, :b 2, :c 3})) &&
    expect(c(2, c: 3), to.be({:a 4, :b 2, :c 3}));

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

  abc_c4:
    let {
      c: f.abc(c=4);
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
            :name "b",
            :index 1,
            :default_value 1,
            :declared_type "any"
          }
        ]
      }
    )) &&
    expect(c(), to.be({:a 0, :b 1, :c 4})) &&
    expect(c(2, 3), to.be({:a 2, :b 3, :c 4})) &&
    expect(c(2, b: 3), to.be({:a 2, :b 3, :c 4}));


  abcd_a4:
    let {
      c: f.abcd(a=4);
    }
    expect(sig(c), to.be(
      {
        :return_type "dict",
        :parameters [
          {
            :name "b",
            :index 0,
            :default_value 1,
            :declared_type "any"
          },
          {
            :name "c",
            :index 1,
            :default_value 2,
            :declared_type "any"
          },
          {
            :name "d",
            :index 2,
            :default_value 3,
            :declared_type "any"
          }
        ]
      }
    )) &&
    expect(c(), to.be({:a 4, :b 1, :c 2, :d 3})) &&
    expect(c(2, 3), to.be({:a 4, :b 2, :c 3, :d 3})) &&
    expect(c(2, d: 6), to.be({:a 4, :b 2, :c 2, :d 6}));

  abcd_a4_b5:
    let {
      c: f.abcd(a=4, b=5);
    }
    expect(sig(c), to.be(
      {
        :return_type "dict",
        :parameters [
          {
            :name "c",
            :index 0,
            :default_value 2,
            :declared_type "any"
          },
          {
            :name "d",
            :index 1,
            :default_value 3,
            :declared_type "any"
          }
        ]
      }
    )) &&
    expect(c(), to.be({:a 4, :b 5, :c 2, :d 3})) &&
    expect(c(2, 3), to.be({:a 4, :b 5, :c 2, :d 3})) &&
    expect(c(2, d: 6), to.be({:a 4, :b 5, :c 2, :d 6}));

  abcd_a4_b5_c6:
    let {
      c: f.abcd(b=5, c=6, a=4);
    }
    expect(sig(c), to.be(
      {
        :return_type "dict",
        :parameters [
          {
            :name "d",
            :index 0,
            :default_value 3,
            :declared_type "any"
          }
        ]
      }
    )) &&
    expect(c(), to.be({:a 4, :b 5, :c 6, :d 3})) &&
    expect(c(2), to.be({:a 4, :b 5, :c 6, :d 2})) &&
    expect(c(d: 6), to.be({:a 4, :b 5, :c 6, :d 6}));

  abcd_b5_c6_d7:
    let {
      c: f.abcd(b=5, c=6, d=7);
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
    expect(c(), to.be({:a 0, :b 5, :c 6, :d 7})) &&
    expect(c(2), to.be({:a 2, :b 5, :c 6, :d 7})) &&
    expect(c(a: 6), to.be({:a 6, :b 5, :c 6, :d 7}));

  abcd_a4_b5_c6_d7:
    let {
      c: f.abcd(d=7, b=5, c=6, a=4);
    }
    expect(sig(c), to.be(
      {
        :return_type "dict",
        :parameters []
      }
    )) &&
    expect(c(), to.be({:a 4, :b 5, :c 6, :d 7}));

  abcde_a4:
    let {
      c: f.abcde(a=4);
    }
    expect(sig(c), to.be(
      {
        :return_type "dict",
        :parameters [
          {
            :name "b",
            :index 0,
            :default_value 1,
            :declared_type "any"
          },
          {
            :name "c",
            :index 1,
            :default_value 2,
            :declared_type "any"
          },
          {
            :name "d",
            :index 2,
            :default_value 3,
            :declared_type "any"
          },
          {
            :name "e",
            :index 3,
            :default_value 4,
            :declared_type "any"
          }
        ]
      }
    )) &&
    expect(c(), to.be({:a 4, :b 1, :c 2, :d 3, :e 4})) &&
    expect(c(2, 3), to.be({:a 4, :b 2, :c 3, :d 3, :e 4})) &&
    expect(c(2, 3, 4, 5), to.be({:a 4, :b 2, :c 3, :d 4, :e 5})) &&
    expect(c(2, d: 6), to.be({:a 4, :b 2, :c 2, :d 6, :e 4}));


  abcde_a4_b5_c6_d7_e8:
    let {
      c: f.abcde(d=7, b=5, e=8, c=6, a=4);
    }
    expect(sig(c), to.be(
      {
        :return_type "dict",
        :parameters []
      }
    )) &&
    expect(c(), to.be({:a 4, :b 5, :c 6, :d 7, :e 8}));

  abcdef_a4:
    let {
      c: f.abcdef(a=4);
    }
    expect(sig(c), to.be(
      {
        :return_type "dict",
        :parameters [
          {
            :name "b",
            :index 0,
            :default_value 1,
            :declared_type "any"
          },
          {
            :name "c",
            :index 1,
            :default_value 2,
            :declared_type "any"
          },
          {
            :name "d",
            :index 2,
            :default_value 3,
            :declared_type "any"
          },
          {
            :name "e",
            :index 3,
            :default_value 4,
            :declared_type "any"
          },
          {
            :name "f",
            :index 4,
            :default_value 5,
            :declared_type "any"
          }
        ]
      }
    )) &&
    expect(c(), to.be({:a 4, :b 1, :c 2, :d 3, :e 4, :f 5})) &&
    expect(c(2, 3), to.be({:a 4, :b 2, :c 3, :d 3, :e 4, :f 5})) &&
    expect(c(2, 3, 4, 5), to.be({:a 4, :b 2, :c 3, :d 4, :e 5, :f 5})) &&
    expect(c(2, d: 6), to.be({:a 4, :b 2, :c 2, :d 6, :e 4, :f 5}));

  abcde_a4_b5_c6_d7_e8_f9:
    let {
      c: f.abcdef(d=7, b=5, e=8, c=6, a=4, f=9);
    }
    expect(sig(c), to.be(
      {
        :return_type "dict",
        :parameters []
      }
    )) &&
    expect(c(), to.be({:a 4, :b 5, :c 6, :d 7, :e 8, :f 9}));

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
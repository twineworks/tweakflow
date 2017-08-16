library lib
{
  a: () -> 1
  ac: a()           # 1

  f: (x) -> x
  f_: f()       # :x nil
  fx: f("x")    # "x"

  v: (x=0, y=0) -> list [x, y]
  v__: v()                # [0,0]
  v00: v(0,0)             # [0,0]
  v12: v(1,2)             # [1,2]
  vaa: v(a(), a())        # [1,1]
  vll: v(v(0,1), a())     # [[0,1], 1]
  v_x1_y2: v(x: 1, y: 2)  # [1,2]
  v_1_y2: v(1, y: 2)      # [1,2]
  v_1_x2: v(1, x: 2)      # [2,0], positional x replaced, y = default
  v_splat: v(...{:x 3 :y 4}) # [3,4]
  v_splat_overwrite: v(1, 2, ...{:x 3 :y 4}) # [3,4], splat overwrite
  v_splat_overwrite_name: v(...{:x 3 :y 4}, x: 5) # [5,4]
  v_splat_overwrite_splat: v(...{:x 3 :y 4}, ...{:x 5}) # [5,4]

  g: (k="key", v=0) -> dict {k library::v(v,v)} # {k, [v,v]}
  g_a1: g("a", 1)     # {:a [1,1]}

  p: (f, g, x) -> g(f(x))
  p_vv1: p(v,v,1)  # v(f(x), 0) -> v(v(1, 0), 0) -> [[1,0], 0]

  v_list_splat: v(...[3 4]) # [3,4]
  v_list_splat_front: v(...[3], 4) # [3,4]
  v_list_splat_back: v(3, ...[4]) # [3,4]
  v_list_splat_both: v(...[3], ...[4]) # [3,4]

  v3: (x, y, z) -> list [x, y, z]
  v3_list_splat_interleave: v3(...[3], 4,...[5]) # [3, 4, 5]
  v3_list_splat_type_interleave: v3(...[3], 4, ...{:z 5}) # [3, 4, 5]
  v3_list_splat_type_interleave_overwrite: v3(...[3], 4, ...{:z 5}, ...{:x 0}) # [0, 4, 5]

  params_as_list_8: (p1, p2, p3, p4, p5, p6, p7, p8) -> list via {:class "com.twineworks.tweakflow.lang.values.ParamsAsList"}

  params_as_list_8_auto_cast: (p1, p2, p3, p4, p5, p6, p7, p8) -> dict via {:class "com.twineworks.tweakflow.lang.values.ParamsAsList"}

  escaped_param: (`my x`=0) -> `my x`+1
}

library lib_spec {

  ac:      lib.ac == 1
  f_:      lib.f_ == nil
  fx:      lib.fx == "x"
  v__:     lib.v__ == [0,0]
  v00:     lib.v00 == [0,0]
  v12:     lib.v(1,2) == [1,2]
  vaa:     lib.vaa == [1,1]
  vll:     lib.vll == [[0,1], 1]
  v_x1_y2: lib.v_x1_y2 == [1,2]
  v_1_y2:  lib.v_1_y2 == [1,2]
  v_1_x2:  lib.v_1_x2 == [2,0] # positional x replaced, y = default

  v_splat: lib.v_splat == [3, 4]
  v_splat_overwrite: lib.v_splat_overwrite == [3,4]
  v_splat_overwrite_name: lib.v_splat_overwrite_name == [5,4]
  v_splat_overwrite_splat: lib.v_splat_overwrite_splat == [5,4]

  g_a1:  lib.g_a1 == {:a [1,1]}
  p_vv1: lib.p_vv1 == [[1,0], 0]

  v_list_splat: lib.v_list_splat == [3,4]
  v_list_splat_front: lib.v_list_splat_front == [3,4]
  v_list_splat_back: lib.v_list_splat_back == [3,4]
  v_list_splat_both: lib.v_list_splat_both == [3,4]

  v3_list_splat_interleave: lib.v3_list_splat_interleave == [3, 4, 5]
  v3_list_splat_type_interleave: lib.v3_list_splat_type_interleave == [3, 4, 5]
  v3_list_splat_type_interleave_overwrite: lib.v3_list_splat_type_interleave_overwrite == [0, 4, 5]

  thread: ->> (1)
              (x) -> x+1,
              (x) -> x*2,
              (x) -> x*x
          ==
          16 # ((1+1)*2)*((1+1)*2)

  default_param_exp: ((x=3+4) -> x == 7)() == true

  params_as_list_8: lib.params_as_list_8(1, 2, 3, 4, 5, 6, 7, 8) == [1, 2, 3, 4, 5, 6, 7, 8]

  params_as_list_autocast:
    lib.params_as_list_8_auto_cast("a", 1, "b", 2, "c", 3, "d", 4)
    ==
    {:a 1, :b 2, :c 3, :d 4}

  escaped_param:
    lib.escaped_param(`my x`: 8) == 9


}

global module scope_module

alias $scope_module as m

export library lib_a {
  a: let {
        a: "foo"
      }
      a;
}

library lib_b {
  a:      lib_a.a
  b:      "b"
  lib_c:  "shadow"
  d:      $scope_module.lib_a.a   # explicit global ref
}

library lib_c {
  a:   ::lib_a.a              # module anchor
  b:   library::a             # library anchor
  c:   $scope_module.lib_a.a  # global anchor
  m_a: m.lib_a.a              # aliased ref
}

library lib_empty {

}
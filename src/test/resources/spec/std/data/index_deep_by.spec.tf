import data from "std";
import assert, expect, expect_error, to, describe, it, subject, before, after from "std/spec";

alias data.index_deep_by as index_deep_by;

library spec {
  spec:
    describe("data.index_deep_by", [

      it("indexes nil", () ->
        expect(
          index_deep_by(nil, (x) -> nil),
          to.be(nil)
        )
      ),

      it("indexes empty list", () ->
        expect(
          index_deep_by([], (x) -> ["foo"]),
          to.be({})
        )
      ),

      it("indexes empty dict", () ->
        expect(
          index_deep_by({}, (x) -> ["foo"]),
          to.be({})
        )
      ),

      it("indexes list 1 level", () ->
        expect(
          index_deep_by([{:id "a1", :v 1}, {:id "a2", :v 2}], (x) -> [x[:id]]),
          to.be({:a1 {:id "a1", :v 1}, :a2 {:id "a2", :v 2}})
        )
      ),

      it("indexes dict 1 level", () ->
        expect(
          index_deep_by({:a {:id "a1", :v 1}, :b {:id "a2", :v 2}}, (x) -> [x[:id]]),
          to.be({:a1 {:id "a1", :v 1}, :a2 {:id "a2", :v 2}})
        )
      ),

      it("indexes list overwriting duplicate paths", () ->
        expect(
          index_deep_by([{:id "a1", :v 1}, {:id "a2", :v 1}], (x) -> [x[:v]]),
          to.be({:1 {:id "a2", :v 1}})
        )
      ),

      it("indexes dict overwriting duplicate paths", () ->
        expect(
          index_deep_by({:a 1, :b 1, :c 1}, (x) -> [x]),
          to.be({:1 1})
        )
      ),

      it("indexes list 2 levels", () ->
        expect(
          index_deep_by([{:l1 "a", :l2 "b"}, {:l1 "a", :l2 "c"}, {:l1 "b", :l2 "a"}], (x) -> [x[:l1], x[:l2]]),
          to.be({
                  :a {
                    :b {:l1 "a", :l2 "b"},
                    :c {:l1 "a", :l2 "c"}
                  },
                  :b {
                    :a  {:l1 "b", :l2 "a"}
                  }
                })
        )
      ),

      it("indexes dict 2 levels", () ->
        expect(
          index_deep_by({:a {:l1 "a", :l2 "b"}, :b {:l1 "a", :l2 "c"}, :c {:l1 "b", :l2 "a"}}, (x) -> [x[:l1], x[:l2]]),
          to.be({
                  :a {
                    :b {:l1 "a", :l2 "b"},
                    :c {:l1 "a", :l2 "c"}
                  },
                  :b {
                    :a  {:l1 "b", :l2 "a"}
                  }
                })
        )
      ),

      it("indexes list 0 levels", () ->
        expect(
          index_deep_by([{:l1 "a", :l2 "b"}, {:l1 "a", :l2 "c"}, {:l1 "b", :l2 "a"}], (x) -> []),
          to.be({})
        )
      ),

      it("indexes dict 0 levels", () ->
        expect(
          index_deep_by({:a {:l1 "a", :l2 "b"}, :b {:l1 "a", :l2 "c"}, :c {:l1 "b", :l2 "a"}}, (x) -> []),
          to.be({})
        )
      ),

      it("indexes list with omit returning nil", () ->
        expect(
          index_deep_by([{:l1 "a", :l2 "b"}, {:l1 "a", :l2 "c"}, {:l1 "b"}], (x) -> if x[:l2] then [x[:l2]] else nil),
          to.be({
                  :b {
                    :l1 "a", :l2 "b"
                  },
                  :c {
                    :l1 "a", :l2 "c"
                  }
                }
          )
        )
      ),

      it("indexes dict with omit returning nil", () ->
        expect(
          index_deep_by({:a {:l1 "a", :l2 "b"}, :b {:l1 "a", :l2 "c"}, :c {:l1 "b"}}, (x) -> if x[:l2] then [x[:l2]] else nil),
          to.be({
                  :b {
                    :l1 "a", :l2 "b"
                  },
                  :c {
                    :l1 "a", :l2 "c"
                  }
                }
          )
        )
      ),

      it("indexes list mixed levels with omit", () ->
        expect(
          index_deep_by([{:l1 "a", :l2 "b", :l3 "c"}, {:l1 "a", :l3 "c"}, {:l1 "b", :l2 "a"}, {:foo "a"}],
          (
            (x) -> ->> ([])
                       (keys) -> if x[:l1] then [...keys, x[:l1]] else keys,
                       (keys) -> if x[:l2] then [...keys, x[:l2]] else keys,
                       (keys) -> if x[:l3] then [...keys, x[:l3]] else keys
          )),
          to.be(
             {
               :a {
                 :b {
                   :c {
                     :l1 "a", :l2 "b", :l3 "c"
                   }
                 },
                 :c {
                   :l1 "a", :l3 "c"
                 }
               },
               :b {
                 :a {
                   :l1 "b", :l2 "a"
                 }
               }
             }
          )
        )
      ),

      it("indexes dict mixed levels with omit", () ->
        expect(
          index_deep_by({:a {:l1 "a", :l2 "b", :l3 "c"}, :b {:l1 "a", :l3 "c"}, :c {:l1 "b", :l2 "a"}, :d {:foo "a"}},
          (
            (x) -> ->> ([])
                       (keys) -> if x[:l1] then [...keys, x[:l1]] else keys,
                       (keys) -> if x[:l2] then [...keys, x[:l2]] else keys,
                       (keys) -> if x[:l3] then [...keys, x[:l3]] else keys
          )),
          to.be(
             {
               :a {
                 :b {
                   :c {
                     :l1 "a", :l2 "b", :l3 "c"
                   }
                 },
                 :c {
                   :l1 "a", :l3 "c"
                 }
               },
               :b {
                 :a {
                   :l1 "b", :l2 "a"
                 }
               }
             }
          )
        )
      ),

      it("indexes list omitting nil keys", () ->
        expect(
          index_deep_by(["one", "two", "three"], (x) -> if x != "two" then [x] else nil),
          to.be({:one "one", :three "three"})
        )
      ),

     it("indexes list with i", () ->
       expect(
         index_deep_by([{:id "a1", :v 1}, {:id "a2", :v 2}], (x, i) -> ["i"..i]),
         to.be({:i0 {:id "a1", :v 1}, :i1 {:id "a2", :v 2}})
       )
     ),

      it("indexes list with i omitting nil keys", () ->
        expect(
          index_deep_by(["one", "two", "three"], (x, i) -> if i!=1 then [x] else nil),
          to.be({:one "one", :three "three"})
       )
     ),

      it("indexes dict", () ->
        expect(
          index_deep_by({:a1 1, :a2 2}, (x) -> ["b"..x]),
          to.be({:b1 1, :b2 2})
        )
      ),

      it("indexes dict omitting nil keys", () ->
        expect(
          index_deep_by({:a1 1, :a2 2}, (x) -> if x!=1 ["b"..x] else nil),
          to.be({:b2 2})
        )
      ),

      it("indexes dict with key", () ->
        expect(
          index_deep_by({:a1 1, :a2 2}, (x, k) -> [k..x]),
          to.be({:a11 1, :a22 2})
        )
      ),

      it("indexes dict with key omitting nil keys", () ->
        expect(
          index_deep_by({:a1 1, :a2 2}, (x, k) -> if k!=:a1 ["b"..x] else nil),
          to.be({:b2 2})
        )
      ),

      it("nil_f", () ->
        expect_error(
          () -> index_deep_by([0,1], nil),
          to.have_code("NIL_ERROR")
        )
      ),

      it("non_list_returning_f", () ->
        expect_error(
          () -> index_deep_by([0,1], (x) -> true),
          to.have_code("CAST_ERROR")
        )
      ),

      it("list_with_nil_returning_f", () ->
        expect_error(
          () -> index_deep_by([0,1], (x) -> [nil]),
          to.have_code("CAST_ERROR")
        )
      ),

      it("zero_arg_f", () ->
        expect_error(
          () -> index_deep_by([0,1], () -> true),
          to.have_code("ILLEGAL_ARGUMENT")
        )
      ),

      it("non_collection_xs", () ->
        expect_error(
          () -> index_deep_by("foo", (x) -> x),
          to.have_code("ILLEGAL_ARGUMENT")
        )
      ),
  ]);
}
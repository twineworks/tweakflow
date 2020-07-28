import data from "std";
import assert, expect, expect_error, to, describe, it, subject, before, after from "std/spec";

alias data.pluck as pluck;

library spec {
  spec:
    describe("data.pluck", [

      it("pluck_nil_from_list", () ->
        expect(pluck([], nil), to.be_nil())
      ),

      it("pluck_from_empty_list", () ->
        expect(pluck([], 0), to.be([]))
      ),

      it("pluck_from_empty_dict", () ->
        expect(pluck({}, :k), to.be({}))
      ),

      it("pluck_from_list", () ->
        expect(pluck([["a", "b"], ["c", "d"]], 0), to.be(["a", "c"]))
      ),

      it("pluck_from_list_missing_key", () ->
        expect(pluck([["a", "b"], ["c"]], 1), to.be(["b", nil]))
      ),

      it("pluck_from_dicts", () ->
        let {
            plucked: data.pluck([
            {:id 1, :name "Sherlock", :address "221B Baker Street" },
            {:id 2, :name "Bruce", :address "1007 Mountain Drive"}],
            "name"
          );
        }
        expect(plucked, to.be(["Sherlock", "Bruce"]))
      ),

      it("pluck_from_dicts_missing_key", () ->
        let {
            plucked: data.pluck([
            {:id 1, :name "Sherlock", },
            {:id 2, :name "Bruce", :address "1007 Mountain Drive"}],
            "address"
          );
        }
        expect(plucked, to.be([nil, "1007 Mountain Drive"]))
      ),

      it("pluck_from_nil_element", () ->
        let {
            plucked: data.pluck([
            {:id 1, :name "Sherlock", :address nil},
            {:id 2, :name "Bruce", :address "1007 Mountain Drive"}],
            "address"
          );
        }
        expect(plucked, to.be([nil, "1007 Mountain Drive"]))
      ),

      it("pluck_from_dict_dict", () ->
        let {
            plucked: data.pluck({
              :detective {:id 1, :name "Sherlock"},
              :dark_knight {:id 2, :name "Bruce Wayne"}
            },
            "name"
          );
        }
        expect(plucked, to.be({:detective "Sherlock", :dark_knight "Bruce Wayne"}))
      ),

      it("pluck_from_dict_list", () ->
        let {
            plucked: data.pluck({
              :detective [221, 222],
              :dark_knight [666, 667]
            },
            0
          );
        }
        expect(plucked, to.be({:detective 221, :dark_knight 666}))
      ),

      it("of_nil", () ->
        expect(pluck(nil), to.be_nil())
      ),

      it("of_bad_key_in_dict", () ->
        expect_error(
          () -> pluck({:a ["foo"]}, [[]]),
          to.have_code("CAST_ERROR")
        )
      ),

      it("of_bad_key_in_list", () ->
        expect_error(
          () -> pluck([[1, 2, 3]], "foo"),
          to.have_code("CAST_ERROR")
        )
      ),

      it("of_non_indexable", () ->
        expect_error(
          () -> pluck("yo", 0),
          to.have_code("ILLEGAL_ARGUMENT")
        )
      ),
  ]);
}
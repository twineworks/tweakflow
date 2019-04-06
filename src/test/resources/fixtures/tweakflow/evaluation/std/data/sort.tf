import store from "./../../data.tf";
import data, math from "std.tf";

alias store.inventory as inv;
alias data.sort as sort;
alias math.compare as compare;

library books {
  all: inv[:book];
}

library map_spec {
  sorts_by_price:
    sort(
      books.all,
      (a, b) -> compare(a[:price], b[:price])
    )
    ==
    [books.all[0], books.all[2], books.all[1], books.all[3]];

}
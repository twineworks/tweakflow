import data from "./lib/values.tf";

library main_spec {
  data_present: data == {:a "hello", :b "world"};
}
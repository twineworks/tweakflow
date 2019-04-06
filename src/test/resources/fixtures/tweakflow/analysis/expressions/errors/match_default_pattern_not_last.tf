
library main {
  a: match "foo"
    "foo"   ->  1,
    default ->  2,
    "bar"   ->  3;
}

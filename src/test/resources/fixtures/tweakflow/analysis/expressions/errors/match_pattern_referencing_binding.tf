
library main {
  a: match [1, 2]
    [@x, x]  ->  1,
    default  ->  nil;
}

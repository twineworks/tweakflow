library lib
{
  f: (x) -> x+3;
  a: (x) ->
       ->>
           f,
           f
       ;
}
library lib
{
  f: (x) -> x+3;
  a: (x) ->
       ->> (x)
           f
           f
       ;
}
library lib
{
person?: (dict x) ->
  match x
    {:name string, 1 string} -> true,
    default -> false;
}
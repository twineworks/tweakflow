library lib
{
person?: (dict x) ->
  match x
    {:name string :profession string} -> true,
    default -> false;
}
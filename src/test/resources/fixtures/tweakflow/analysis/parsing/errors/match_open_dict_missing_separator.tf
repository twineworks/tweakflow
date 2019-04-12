library lib
{
person?: (dict x) ->
  match x
    {:name string @...rest} -> true,
    default -> false;
}
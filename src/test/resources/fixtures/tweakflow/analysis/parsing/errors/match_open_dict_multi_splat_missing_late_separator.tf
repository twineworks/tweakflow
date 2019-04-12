library lib
{
person?: (dict x) ->
  match x
    {@...rest, :name "foo", @...rest, :profession "bar" :at "baz"} -> true,
    default -> false;
}
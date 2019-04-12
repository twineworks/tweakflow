library lib
{
person?: (dict x) ->
  match x
    {@...rest, :name "foo" :profession "bar"} -> true,
    default -> false;
}
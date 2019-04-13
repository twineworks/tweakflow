library lib
{
person?: (dict x) ->
  match x
    {@...rest, :foo string, :name} -> true,
    default -> false;
}
library lib
{
person?: (dict x) ->
  match x
    {@...rest, :name string, 1 string} -> true,
    default -> false;
}
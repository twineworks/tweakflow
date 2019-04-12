library lib
{
person?: (dict x) ->
  match x
    {@...rest, :name string, @...rest, 1 string} -> true,
    default -> false;
}
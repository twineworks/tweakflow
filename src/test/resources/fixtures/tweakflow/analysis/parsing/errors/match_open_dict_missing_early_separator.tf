library lib
{
person?: (dict x) ->
  match x
    {@...rest :name string} -> true,
    default -> false;
}
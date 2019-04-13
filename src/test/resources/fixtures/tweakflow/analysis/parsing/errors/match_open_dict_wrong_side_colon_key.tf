library lib
{
person?: (dict x) ->
  match x
    {@...rest, :name string, profession: string} -> true,
    default -> false;
}
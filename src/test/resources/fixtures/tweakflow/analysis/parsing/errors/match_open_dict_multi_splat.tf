library lib
{
person?: (dict x) ->
  match x
    {@...start, :name string, @...end} -> true,
    default -> false;
}
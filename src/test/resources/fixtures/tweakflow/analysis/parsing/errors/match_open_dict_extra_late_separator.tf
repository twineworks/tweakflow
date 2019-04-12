library lib
{
person?: (dict x) ->
  match x
    {@...rest, :name string, @...rest, :profession string,, :born datetime} -> true,
    default -> false;
}
library lib
{
person?: (dict x) ->
  match x
    {:born datetime, name: string} -> true,
    default -> false;
}
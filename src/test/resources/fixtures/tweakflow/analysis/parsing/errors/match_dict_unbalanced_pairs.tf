library lib
{
person?: (dict x) ->
  match x
    {:born datetime, :name} -> true,
    default -> false;
}
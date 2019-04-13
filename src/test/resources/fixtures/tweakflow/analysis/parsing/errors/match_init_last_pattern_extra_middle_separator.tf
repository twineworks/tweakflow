library lib
{
  vector?: (list xs) ->
    match x
      [@...init,,@] -> true,
      default -> false;
}
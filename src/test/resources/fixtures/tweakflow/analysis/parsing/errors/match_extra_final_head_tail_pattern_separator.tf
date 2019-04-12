library lib
{
  vector?: (list xs) ->
    match x
      [@, @, @...tail,] -> true,
      default -> false;
}
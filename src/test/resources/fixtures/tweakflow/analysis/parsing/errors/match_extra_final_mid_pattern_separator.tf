library lib
{
  vector?: (list xs) ->
    match x
      [@, @...mid,,] -> true,
      default -> false;
}
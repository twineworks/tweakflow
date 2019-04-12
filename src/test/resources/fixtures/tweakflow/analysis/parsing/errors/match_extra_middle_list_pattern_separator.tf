library lib
{
  vector?: (list xs) ->
    match x
      [@,,@,@] -> true,
      default -> false;
}
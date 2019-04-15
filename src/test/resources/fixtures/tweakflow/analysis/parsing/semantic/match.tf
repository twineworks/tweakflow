library lib {
  match_42: match 42                          # match wit basic patterns
        10 ->      "ten",
        20 ->      "twenty",
        42 ->      "the answer to everything",
        default -> "unknown";
}
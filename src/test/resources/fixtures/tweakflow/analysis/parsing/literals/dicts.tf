library lib
{
  empty: {};
  basic: {:key "value"};
  simple: {:key1 "value1", :key2 "value2"};
  extra_final_separator: {:key1 "value1", :key2 "value2",};
  nested: {"k" "v", "sub" {:key "value"}};
  escaped_key: {:`escaped key` "value"};
}
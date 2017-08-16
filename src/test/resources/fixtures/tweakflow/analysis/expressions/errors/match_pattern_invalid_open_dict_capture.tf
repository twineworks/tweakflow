
library main {
  a: match {:a 1}
    {"".."a" 1, @...rest} -> "invalid"
    default  ->  nil
}

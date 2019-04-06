library lib
{
  e0: nil;
  e1: "string value";
  e2: 1;
  e3: 0x01;
  e4: [];
  e5: [1, 2, 3];
  e6: [1, "a", ["x","y"]];
  e7: {};
  e8: {:key "value"};
  e9: {:key1 "value1", :key2 "value2"};
  e10: {"k" "v", "sub" {:key "value"}};
  e11: "-\n-";
  e12: true;
  e13: false;
  e14: () -> true; # constant function returning true
  e15: (double x = 0.0, double y = 0.0) -> list [x, y];
  e16: (list xs) -> any via "native";
  e17: 2e-1;
  e18: 3.1315;
  e19: 0.31315e1;
  e20: .31315e1;
  e21: 31315e-4;
  e22: 'single quoted ''string''';
  e23: "string with\nescape sequence";
  e24: 'single quoted
multi
line
string';
  e25: "double quoted
multi
line
string";
  e26: '';
  e27: "";
  e28: Infinity;
  e29: NaN;
  e30: {:`escaped key` "value"};
  e31:
~~~
Here ~~~ String
~~~;
  e32: 2017-03-17T16:04:02;                 # local date, implied UTC, second precision
  e33: 2017-03-17T16:04:02.123456789;       # local date, implied UTC, nano-second precision
  e34: 2017-03-17T16:04:02+01:00@`Europe/Berlin`; # local date in Berlin, second precision
  e35: 2017-03-17T16:04:02.123+01:00@`Europe/Berlin`; # local date in Berlin, milli-second precision
  e36: 2017-03-17T16:04:02Z;                 # UTC time, second precision
  e37: 2017-03-17T16:04:02+02:00;            # UTC+2 time, implied time zone
  e38: 2017-03-17T;                          # local date, implied UTC, implied time midnight
}
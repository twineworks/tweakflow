library lib
{
  utc_imp_s:    2017-03-17T16:04:02;                 # implied UTC, second precision
  utc_imp_ns:   2017-03-17T16:04:02.123456789;       # implied UTC, nano-second precision
  local:        2017-03-17T16:04:02+01:00@`Europe/Berlin`; # local date in Berlin, second precision
  local_ms:     2017-03-17T16:04:02.123+01:00@`Europe/Berlin`; # local date in Berlin, milli-second precision
  utc_s:        2017-03-17T16:04:02Z;                 # UTC time, second precision
  utc_plus_2:   2017-03-17T16:04:02+02:00;            # UTC+2 time, implied time zone
  utc_midnight: 2017-03-17T;                          # implied UTC, implied time midnight
  deep_time_ms: 999992017-03-17T16:04:02.123;

  neg_utc_imp_s:    -2017-03-17T16:04:02;                 # implied UTC, second precision
  neg_utc_imp_ns:   -2017-03-17T16:04:02.123456789;       # implied UTC, nano-second precision
  neg_utc_s:        -2017-03-17T16:04:02Z;                 # UTC time, second precision
  neg_utc_plus_2:   -2017-03-17T16:04:02+02:00;            # UTC+2 time, implied time zone
  neg_utc_midnight: -2017-03-17T;                          # implied UTC, implied time midnight
  neg_deep_time_ms: -999992017-03-17T16:04:02.123;

}
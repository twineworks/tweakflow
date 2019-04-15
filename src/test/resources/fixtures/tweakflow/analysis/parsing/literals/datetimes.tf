library lib
{
  utc_imp_s:    2017-03-17T16:04:02;                 # implied UTC, second precision
  utc_imp_ns:   2017-03-17T16:04:02.123456789;       # implied UTC, nano-second precision
  local:        2017-03-17T16:04:02+01:00@Europe/Berlin; # local date in Berlin, second precision
  local_ms:     2017-03-17T16:04:02.123+01:00@Europe/Berlin; # local date in Berlin, milli-second precision
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

  minimal_date:                1999-1-1T;
  minimal_date_time:           1999-1-1T1:2:3;
  minimal_date_time_offset:    1999-1-1T1:2:3+1:0;
  minimal_date_time_offset_tz: 1999-1-1T1:2:3+1:0@`UTC+01:00`;

  tz_implied_offset:          2019-01-01T00:00:00@Europe/Berlin;
  tz_implied_offset_gap:      2019-03-31T02:30:00@Europe/Berlin;
  tz_implied_offset_overlap:  2019-10-27T02:30:00@Europe/Berlin;
}
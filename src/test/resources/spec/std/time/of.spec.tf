import time as t, math from 'std.tf';
import assert, expect, expect_error, to, describe, it, subject, before, after from "std/spec";

alias t.of as of;

library spec {
  spec:
    describe("time.of", [
    
      it("of_default", () -> 
        expect(of(), to.be(t.epoch))
      ),
    
      it("of_date", () -> 
        expect(
          of(2019, 04, 10),
          to.be(2019-04-10T)
        )
      ),
    
      it("of_date_and_time", () -> 
        expect(
          of(2019, 04, 10, 23, 11, 32),
          to.be(2019-04-10T23:11:32)
        )
      ),
    
      it("of_date_and_time_with_ns", () -> 
        expect(
          of(2019, 04, 10, 23, 11, 32, 999000000),
          to.be(2019-04-10T23:11:32.999)
        )
      ),
    
      it("of_date_and_time_and_tz", () -> 
        expect(
          of(2019, 04, 10, 23, 11, 32, tz: "America/New_York"),
          to.be(2019-04-10T23:11:32-04:00@America/New_York)
        )
      ),
    
      it("of_date_and_time_and_offset_tz", () -> 
        expect(
          of(2019, 04, 10, 23, 11, 32, tz: "UTC+03:00"),
          to.be(2019-04-10T23:11:32+03:00)
        )
      ),
    
      it("in_dst_gap", () -> 
        expect(
          of(2019, 03, 31, 2, 30, tz: "Europe/Berlin"),
          to.be(2019-03-31T03:30:00+02:00@Europe/Berlin)
        )
      ),
    
      it("in_dst_overlap", () -> 
        expect(
          of(2019, 10, 27, 2, 30, tz: "Europe/Berlin"),
          to.be(2019-10-27T02:30:00+02:00@Europe/Berlin)
        ) &&
        expect(
          t.add_duration(of(2019, 10, 27, 2, 30, tz: "Europe/Berlin"), 3600),
          to.be(2019-10-27T02:30:00+01:00@Europe/Berlin)
        )
      ),
    
      it("of_nil_year", () -> 
        expect(of(year: nil), to.be_nil())
      ),
    
      it("of_nil_month", () -> 
        expect(of(month: nil), to.be_nil())
      ),
    
      it("of_nil_day_of_month", () -> 
        expect(of(day_of_month: nil), to.be_nil())
      ),
    
      it("of_nil_hour", () -> 
        expect(of(hour: nil), to.be_nil())
      ),
    
      it("of_nil_minute", () -> 
        expect(of(minute: nil), to.be_nil())
      ),
    
      it("of_nil_second", () -> 
        expect(of(second: nil), to.be_nil())
      ),
    
      it("of_nil_nano_of_second", () -> 
        expect(of(nano_of_second: nil), to.be_nil())
      ),
    
      it("of_nil_tz", () -> 
        expect(of(tz: nil), to.be_nil())
      ),
    
      it("of_bad_year", () -> 
        expect_error(
          () -> of(year: math.max_long),
          to.have_code("ILLEGAL_ARGUMENT")
        )
      ),
    
      it("of_bad_month", () -> 
        expect_error(
          () -> of(month: 13),
          to.have_code("ILLEGAL_ARGUMENT")
        )
      ),
    
      it("of_bad_day_of_month", () -> 
        expect_error(
          () -> of(day_of_month: -1),
          to.have_code("ILLEGAL_ARGUMENT")
        )
      ),
    
      it("of_bad_hour", () -> 
        expect_error(
          () -> of(hour: 25),
          to.have_code("ILLEGAL_ARGUMENT")
        )
      ),
    
      it("of_bad_minute", () -> 
        expect_error(
          () -> of(minute: 60),
          to.have_code("ILLEGAL_ARGUMENT")
        )
      ),
    
      it("of_bad_second", () -> 
        expect_error(
          () -> of(second: 60),
          to.have_code("ILLEGAL_ARGUMENT")
        )
      ),
    
      it("of_bad_nano_of_second", () -> 
        expect_error(
          () -> of(nano_of_second: -1),
          to.have_code("ILLEGAL_ARGUMENT")
        )
      ),
    
      it("of_bad_tz", () -> 
        expect_error(
          () -> of(tz: "invalid timezone"),
          to.have_code("ILLEGAL_ARGUMENT")
        )
      ),
  ]);
}
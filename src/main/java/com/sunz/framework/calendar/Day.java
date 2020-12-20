//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.sunz.framework.calendar;

import java.util.Calendar;
import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.Table;

@Table(
    name = "T_S_Calendar"
)
@Entity
public class Day extends com.sunz.framework.core.Entity {
    int year;
    int month;
    int day;
    boolean holiday;
    String remark;
    Date dayDate;

    public Day() {
    }

    public int getYear() {
        return this.year;
    }

    public int getMonth() {
        return this.month;
    }

    public int getDay() {
        return this.day;
    }

    public boolean isHoliday() {
        return this.holiday;
    }

    public String getRemark() {
        return this.remark;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public void setHoliday(boolean holiday) {
        this.holiday = holiday;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Date toDate() {
        return this.getDayDate();
    }

    public static Day fromDate(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        Day d = new Day();
        d.setYear(c.get(1));
        d.setMonth(c.get(2) + 1);
        d.setDay(c.get(5));
        return d;
    }

    public Date getDayDate() {
        if (this.dayDate == null) {
            Calendar c = Calendar.getInstance();
            c.set(this.year, this.month - 1, this.day);
            this.dayDate = c.getTime();
        }

        return this.dayDate;
    }

    public void setDayDate(Date dayDate) {
        this.dayDate = dayDate;
    }
}

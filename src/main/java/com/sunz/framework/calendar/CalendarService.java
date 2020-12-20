//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.sunz.framework.calendar;

import com.sunz.framework.core.GuidGenerator;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;
import org.jeecgframework.core.common.service.CommonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CalendarService implements ICalendarService {
    private static final Logger logger = Logger.getLogger(CalendarService.class);
    private int[] monthDays = new int[]{31, -1, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
    private boolean[] weekWorkdays = new boolean[]{false, true, true, true, true, true, false};
    private CommonService commonService;
    private Map<Integer, List<Day>> dictOtherYears = new HashMap();

    public CalendarService() {
    }

    private int getFebruaryDays(int year) {
        return (year % 100 == 0 ? year % 400 : year % 4) == 0 ? 29 : 28;
    }

    @Autowired
    public void setCommonService(CommonService commonService) {
        this.commonService = commonService;
    }

    public synchronized void initYear(int year) {
        int current = Calendar.getInstance().get(1);
        if (!this.dictOtherYears.containsKey(year)) {
            List<Day> daysOfYear = this.commonService.findHql("from Day t where t.year=? order by t.month,t.day asc", new Object[]{year});
            if (daysOfYear.size() != 0) {
                this.dictOtherYears.put(year, daysOfYear);
            } else if (year < current) {
                throw new RuntimeException("不提供以往年份日期初始化服务");
            } else if (year - current > 2) {
                throw new RuntimeException("不提供1年以后的日期初始化服务");
            } else {
                Calendar c = Calendar.getInstance();

                for(int m = 0; m < 12; ++m) {
                    int count = m == 1 ? this.getFebruaryDays(year) : this.monthDays[m];

                    for(int d = 0; d < count; ++d) {
                        int dayOfMonth = d + 1;
                        c.set(year, m, dayOfMonth);
                        Day day = new Day();
                        day.setId(GuidGenerator.getGuid());
                        day.setYear(year);
                        day.setMonth(m + 1);
                        day.setDay(dayOfMonth);
                        day.setHoliday(!this.weekWorkdays[c.get(7) - 1]);
                        daysOfYear.add(day);
                    }
                }

                this.commonService.batchSave(daysOfYear);
                this.dictOtherYears.put(year, daysOfYear);
            }
        }
    }

    private int getYear(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        return c.get(1);
    }

    private int getMonth(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        return c.get(2) + 1;
    }

    private int getDay(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        return c.get(5);
    }

    private List<Day> getDays(int year) {
        if (!this.dictOtherYears.containsKey(year)) {
            this.initYear(year);
        }

        return (List)this.dictOtherYears.get(year);
    }

    private List<Day> getDays(Date date) {
        return this.getDays(this.getYear(date));
    }

    private Day find(List<Day> daysOfYear, Date date) {
        return this.find(daysOfYear, this.getYear(date), this.getMonth(date), this.getDay(date));
    }

    private final Day find(List<Day> daysOfYear, int year, int month, int day) {
        int len = daysOfYear.size();

        for(int i = 0; i < len; ++i) {
            Day d = (Day)daysOfYear.get(i);
            if (d.getYear() == year && d.getMonth() == month && d.getDay() == day) {
                return d;
            }
        }

        return null;
    }

    private void setDay(int year, int month, int day, Boolean isHoliday, String remark) {
        Day d = this.find(this.getDays(year), year, month, day);
        d.setHoliday(isHoliday);
        d.setRemark(remark);
        this.commonService.updateEntitie(d);
    }

    public void setHoliday(Date date, String remark) {
        this.setHoliday(this.getYear(date), this.getMonth(date), this.getDay(date), remark);
    }

    public void setHoliday(int year, int month, int day, String remark) {
        this.setDay(year, month, day, true, remark);
    }

    public void setWorkday(Date date, String remark) {
        this.setWorkday(this.getYear(date), this.getMonth(date), this.getDay(date), remark);
    }

    public void setWorkday(int year, int month, int day, String remark) {
        this.setDay(year, month, day, false, remark);
    }

    public boolean isHoliday() {
        Calendar c = Calendar.getInstance();
        int year = c.get(1);
        int month = c.get(2);
        int day = c.get(5);
        return this.find(this.getDays(year), year, month, day).isHoliday();
    }

    public Date dueWorkDate(Date date, int workdays) {
        return this.dueDay(date, workdays, true).toDate();
    }

    private Day dueDay(Date date, int stamp, boolean skipHoliday) {
        if (date == null) {
            throw new RuntimeException("到期时间计算未指定起始时间");
        } else {
            int rax = 1;
            if (stamp < 0) {
                rax = -1;
            }

            List<Day> days = this.getDays(date);
            int startYear = this.getYear(date);
            int start = ((List)days).indexOf(this.find((List)days, date));
            Day due = null;
            int i = rax;

            for(int w = 0; w != stamp; i += rax) {
                if (((List)days).size() == start + i || start + i == 0) {
                    List<Day> newList = new ArrayList();
                    List<Day> nextList = this.getDays(startYear += rax);
                    if (rax > 0) {
                        newList.addAll((Collection)days);
                        newList.addAll(nextList);
                    } else {
                        newList.addAll(nextList);
                        newList.addAll((Collection)days);
                        i += nextList.size();
                    }

                    days = newList;
                }

                due = (Day)((List)days).get(start + i);
                if (!skipHoliday || !due.isHoliday()) {
                    w += rax;
                }
            }

            if (due == null) {
                due = Day.fromDate(date);
            }

            return due;
        }
    }

    public Date dueDate(Date date, int days) {
        return this.dueDay(date, days, false).toDate();
    }

    public List yearDays(int year) {
        return this.getDays(year);
    }

    public boolean isHoliday(Date date) {
        return this.find(this.getDays(date), date).isHoliday();
    }

    public boolean isHoliday(int year, int month, int day) {
        return this.find(this.getDays(year), year, month, day).isHoliday();
    }

    private int stamp(Date from, Date to, int flag) {
        if (from != null && to != null) {
            int fromYear = this.getYear(from);
            int toYear = this.getYear(to);
            if (toYear - fromYear > 1) {
                throw new RuntimeException("不提供超过一年的运算");
            } else {
                List<Day> days = this.getDays(from);
                int toIndex;
                if (toYear != fromYear) {
                    List<Day> newList = new ArrayList();
                    newList.addAll((Collection)days);
                    toIndex = fromYear;

                    do {
                        toIndex += fromYear > toYear ? -1 : 1;
                        newList.addAll(this.getDays(toIndex));
                    } while(toIndex != toYear);

                    days = newList;
                }

                int fromIndex = ((List)days).indexOf(this.find((List)days, from));
                toIndex = ((List)days).indexOf(this.find((List)days, to));
                int rax = 1;
                int count;
                if (fromIndex > toIndex) {
                    rax = -1;
                    count = toIndex;
                    toIndex = fromIndex;
                    fromIndex = count;
                }

                count = 0;

                for(int index = fromIndex; index < toIndex; ++index) {
                    if (flag == 0) {
                        ++count;
                    }

                    if (flag == 1 && !((Day)((List)days).get(index)).isHoliday()) {
                        ++count;
                    }

                    if (flag == -1 && ((Day)((List)days).get(index)).isHoliday()) {
                        ++count;
                    }
                }

                return count * rax;
            }
        } else {
            throw new RuntimeException("日期间距计算未指定起止时间");
        }
    }

    public int dayCount(Date from, Date to) {
        return this.stamp(from, to, 0);
    }

    public int holidayCount(Date from, Date to) {
        return this.stamp(from, to, -1);
    }

    public int workdayCount(Date from, Date to) {
        return this.stamp(from, to, 1);
    }
}

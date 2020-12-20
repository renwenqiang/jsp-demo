//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.sunz.framework.calendar;

import com.sunz.framework.core.JsonResult;
import com.sunz.framework.core.ListJsonResult;
import java.util.Date;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping({"framework/calendar"})
public class CalendarController {
    ICalendarService calenderService;

    public CalendarController() {
    }

    @Autowired
    public void setCalenderService(ICalendarService calenderService) {
        this.calenderService = calenderService;
    }

    @RequestMapping(
        params = {"setting"}
    )
    public ModelAndView setting() {
        ModelAndView mv = new ModelAndView("framework/calendar/setting");
        return mv;
    }

    @RequestMapping(
        params = {"yearDays"}
    )
    @ResponseBody
    public ListJsonResult yearDays(int year) {
        return new ListJsonResult(this.calenderService.yearDays(year));
    }

    @RequestMapping(
        params = {"isWorkday"}
    )
    @ResponseBody
    public JsonResult isWorkday(int year, int month, int day) {
        return new JsonResult(!this.calenderService.isHoliday(year, month, day));
    }

    @RequestMapping(
        params = {"workdayCount"}
    )
    @ResponseBody
    public JsonResult workdayCount(Date from, Date to) {
        return new JsonResult(this.calenderService.workdayCount(from, to));
    }

    @RequestMapping(
        params = {"dayCount"}
    )
    @ResponseBody
    public JsonResult dayCount(Date from, Date to) {
        return new JsonResult(this.calenderService.dayCount(from, to));
    }

    @RequestMapping(
        params = {"holidayCount"}
    )
    @ResponseBody
    public JsonResult holidayCount(Date from, Date to) {
        return new JsonResult(this.calenderService.holidayCount(from, to));
    }

    @RequestMapping(
        params = {"dueDate"}
    )
    @ResponseBody
    public JsonResult dueDate(Date date, int count) {
        return new JsonResult(this.calenderService.dueDate(date, count));
    }

    @RequestMapping(
        params = {"dueWorkDate"}
    )
    @ResponseBody
    public JsonResult dueWorkDate(Date date, int count) {
        return new JsonResult(this.calenderService.dueWorkDate(date, count));
    }

    @RequestMapping(
        params = {"setHoliday"}
    )
    @ResponseBody
    public JsonResult setHoliday(Date date, String remark) {
        this.calenderService.setHoliday(date, remark);
        return JsonResult.Success;
    }

    @RequestMapping(
        params = {"setWorkday"}
    )
    @ResponseBody
    public JsonResult setWorkday(Date date, String remark) {
        this.calenderService.setWorkday(date, remark);
        return JsonResult.Success;
    }
}

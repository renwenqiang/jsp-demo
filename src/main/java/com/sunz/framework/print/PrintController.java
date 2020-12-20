//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.sunz.framework.print;

import com.sunz.framework.core.BaseController;
import com.sunz.framework.core.JsonResult;
import com.sunz.framework.print.entity.PrintElementSetting;
import com.sunz.framework.print.entity.PrinterSetting;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping({"/framework/print"})
public class PrintController extends BaseController {
    public PrintController() {
    }

    @RequestMapping(
        params = {"print"}
    )
    public ModelAndView goPrint(String formid) {
        List<PrintElementSetting> settings = (List)this.elementSetting(formid).getData();
        Map<String, Object> mModel = new HashMap();
        mModel.put("setting", settings);
        return new ModelAndView("framework/print/printFrame", mModel);
    }

    @RequestMapping(
        params = {"setting"}
    )
    public ModelAndView goSetting(String formid) {
        List<PrintElementSetting> settings = (List)this.elementSetting(formid).getData();
        Map<String, Object> mModel = new HashMap();
        mModel.put("setting", settings);
        return new ModelAndView("framework/print/printSetting", mModel);
    }

    @RequestMapping(
        params = {"printersetting"}
    )
    @ResponseBody
    public JsonResult printerSetting(String printer) {
        PrinterSetting s = (PrinterSetting)this.commonService.findUniqueByProperty(PrinterSetting.class, "printer", printer);
        return new JsonResult(s);
    }

    @RequestMapping(
        params = {"elementsetting"}
    )
    @ResponseBody
    public JsonResult elementSetting(String formid) {
        List<PrintElementSetting> settings = this.commonService.findByProperty(PrintElementSetting.class, "formid", formid);
        return new JsonResult(settings);
    }
}

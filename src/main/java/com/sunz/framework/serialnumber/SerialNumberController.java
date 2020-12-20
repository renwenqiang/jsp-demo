//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.sunz.framework.serialnumber;

import com.sunz.framework.core.BaseController;
import com.sunz.framework.core.JsonResult;
import com.sunz.framework.core.ListJsonResult;
import com.sunz.framework.serialnumber.service.ISerialNumberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping({"framework/serialNumber"})
public class SerialNumberController extends BaseController {
    private ISerialNumberService serialNumberService;

    public SerialNumberController() {
    }

    @Autowired
    public void setSerialNumberService(ISerialNumberService serialNumberService) {
        this.serialNumberService = serialNumberService;
    }

    @RequestMapping(
        params = {"edit"}
    )
    public String edit() {
        return "framework/serialnumber/serialNumber";
    }

    @RequestMapping(
        params = {"getNext"}
    )
    @ResponseBody
    public JsonResult getNext(@RequestParam(required = false) String key, @RequestParam(required = false) String k, String userid, String jobid) throws Exception {
        return new JsonResult(this.serialNumberService.generateNumber(this.isStringEmpty(k) ? key : k, userid, jobid));
    }

    @RequestMapping(
        params = {"getAll"}
    )
    @ResponseBody
    public ListJsonResult getAll() {
        return new ListJsonResult(this.serialNumberService.getAllDefine());
    }
}

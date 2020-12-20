//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.sunz.framework.mobile;

import com.sunz.framework.core.BaseController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping({"/mobile"})
public class MobileController extends BaseController {
    public MobileController() {
    }

    @RequestMapping({"main"})
    public String main() {
        return "../mobile/main";
    }

    @RequestMapping({"onlinedebug"})
    public String onlinedebug() {
        return "../mobile/onlinedebug";
    }
}

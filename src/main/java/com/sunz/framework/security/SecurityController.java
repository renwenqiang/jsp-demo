//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.sunz.framework.security;

import com.sunz.framework.core.BaseController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping({"framework/security"})
@Controller
public class SecurityController extends BaseController {
    public SecurityController() {
    }

    @RequestMapping(
        params = {"config"}
    )
    public String config() {
        return "framework/security/config";
    }
}

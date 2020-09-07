package com.boot.controller;

import com.boot.service.DemoService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;

/**
 * @author WindShadow
 * @verion 2020/8/16.
 */

@Controller
public class DemoController {

    @Resource
    private DemoService demoService;

    @RequestMapping("/demo")
    @ResponseBody
    public String sayDemo() {

        return demoService.getDemoString();
    }
}

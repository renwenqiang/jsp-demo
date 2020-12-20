//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.sunz.framework.springmvc;

import java.util.Date;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.support.WebBindingInitializer;
import org.springframework.web.context.request.WebRequest;

public class DateBindingInitializer implements WebBindingInitializer {
    public DateBindingInitializer() {
    }

    public void initBinder(WebDataBinder binder, WebRequest request) {
        this.initBinder(binder);
    }

    public void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(Date.class, new DatePropertyEditor());
    }
}

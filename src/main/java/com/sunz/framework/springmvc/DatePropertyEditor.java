//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.sunz.framework.springmvc;

import com.sunz.framework.util.DateUtil;
import com.sunz.framework.util.StringUtil;
import java.beans.PropertyEditorSupport;

public class DatePropertyEditor extends PropertyEditorSupport {
    public DatePropertyEditor() {
    }

    public void setAsText(String dateOrTime) throws IllegalArgumentException {
        if (StringUtil.isEmpty(dateOrTime)) {
            this.setValue((Object)null);
        } else {
            this.setValue(DateUtil.parseDate(dateOrTime));
        }

    }
}

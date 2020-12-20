//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.sunz.framework.mobile;

import com.sunz.framework.core.ICacheRefreshable;
import com.sunz.framework.core.WebRootSupport;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import javax.annotation.PostConstruct;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

@Component
public class MobileJsGenerator extends WebRootSupport implements ICacheRefreshable {
    Logger logger = Logger.getLogger(this.getClass());
    String[] fileNames = new String[]{"validate", "sunz.ui.mobile", "swipe.extend", "textbox", "form", "combobox", "dictcombobox", "listitem", "list", "panel", "button", "switch", "switchgroup", "textarea", "filebox", "window", "carousel", "navhead", "nav", "splitbar", "searchlist", "menu", "tabitem", "tab", "progress", "sliderbar", "imageView", "radio", "activebottom", "calendar-plugin", "calendar", "star", "pay", "apiend"};

    public MobileJsGenerator() {
    }

    @PostConstruct
    public void init() {
        this.refresh((String)null);
    }

    public void refresh(String item) {
        String savePath = webRoot + "mobile/sunzmobile.js";
        String srcPath = webRoot + "/webpage/framework/mobile/js/";

        try {
            FileOutputStream fjs = new FileOutputStream(savePath);
            String[] var5 = this.fileNames;
            int var6 = var5.length;

            for(int var7 = 0; var7 < var6; ++var7) {
                String fileName = var5[var7];
                fjs.write(("\r\n\r\n/********************\r\n * " + fileName + "\r\n/********************/\r\n").getBytes("UTF-8"));
                FileInputStream fin = new FileInputStream(srcPath + fileName + ".js");
                byte[] data = new byte[fin.available()];
                fin.read(data);
                fjs.write(data);
                fin.close();
            }

            fjs.close();
        } catch (IOException var11) {
            this.logger.warn("sunzmobile.js生成未成功，移动端可能不可用", var11);
        }

    }

    public String getCategory() {
        return "mobilejs";
    }

    public String getDescription() {
        return "sunzmobile.js生成";
    }
}

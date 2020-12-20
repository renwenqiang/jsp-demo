//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.sunz.framework.print.entity;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(
    name = "T_S_PrinterSetting"
)
public class PrinterSetting extends com.sunz.framework.core.Entity {
    private String printer;
    private int offsetX;
    private int offsetY;
    private int pageWidth;
    private int pageHeight;
    private boolean landscape;

    public PrinterSetting() {
    }

    public String getPrinter() {
        return this.printer;
    }

    public int getOffsetX() {
        return this.offsetX;
    }

    public int getOffsetY() {
        return this.offsetY;
    }

    public int getPageWidth() {
        return this.pageWidth;
    }

    public int getPageHeight() {
        return this.pageHeight;
    }

    public boolean isLandscape() {
        return this.landscape;
    }

    public void setPrinter(String printer) {
        this.printer = printer;
    }

    public void setOffsetX(int offsetX) {
        this.offsetX = offsetX;
    }

    public void setOffsetY(int offsetY) {
        this.offsetY = offsetY;
    }

    public void setPageWidth(int pageWidth) {
        this.pageWidth = pageWidth;
    }

    public void setPageHeight(int pageHeight) {
        this.pageHeight = pageHeight;
    }

    public void setLandscape(boolean landscape) {
        this.landscape = landscape;
    }
}

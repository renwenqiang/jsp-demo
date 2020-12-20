//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.sunz.framework.security;

public class ValidateInfo {
    public boolean hasRight;
    public String msg;
    public String featurePath;

    public ValidateInfo() {
        this.hasRight = true;
    }

    public ValidateInfo(boolean r, String path) {
        this.hasRight = r;
        this.featurePath = path;
    }

    public ValidateInfo(boolean r, String path, String msg) {
        this(r, path);
        this.msg = msg;
    }
}

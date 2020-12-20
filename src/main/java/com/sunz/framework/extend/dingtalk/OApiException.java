//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.sunz.framework.extend.dingtalk;

public class OApiException extends Exception {
    public OApiException(int errCode, String errMsg) {
        super("error code: " + errCode + ", error message: " + errMsg);
    }
}

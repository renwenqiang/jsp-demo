//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.sunz.framework.extend.dingtalk;

public class OApiResultException extends OApiException {
    public static final int ERR_RESULT_RESOLUTION = -2;

    public OApiResultException(String field) {
        super(-2, "Cannot resolve field " + field + " from oapi resonpse");
    }
}

//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.sunz.framework.message;

import com.sunz.framework.core.Config;
import com.sunz.framework.core.JsonHelper;
import com.sunz.framework.core.Config.ChangeHandler;
import com.sunz.framework.sms.IShortMessageService;
import com.sunz.framework.util.StringUtil;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class BaseMessageService implements IMessageService {
    protected byte[] emptyContent = " ".getBytes();
    IShortMessageService sms;
    Long lastTime = 0L;
    static Long Short_Message_Tolerence;
    String Admin_Phone;
    Map<String, Object> shortMessageExtendParams;

    public BaseMessageService() {
        String k1 = "mq.ShortMessageTolerence";
        String k2 = "Sys.Administrator.Phone";
        ChangeHandler handler = (k) -> {
            if (k == null || k1.equals(k)) {
                Short_Message_Tolerence = Long.parseLong(StringUtil.ifEmpty(Config.get(k1), "300000"));
            }

            if (k == null || k2.equals(k)) {
                this.Admin_Phone = Config.get(k2);
                if (StringUtil.isEmpty(this.Admin_Phone)) {
                    Logger.getLogger(this.getClass()).warn("当前系统未配置系统管理员手机号，当系统出现重要事件时将无法通知给管理员");
                }
            }

        };
        Config.addChangeListener(handler);
        handler.onChange((String)null);
        this.shortMessageExtendParams = new HashMap<String, Object>() {
            {
                this.put("title", "MQ服务异常警告");
                this.put("btype", "mq-error-notice");
            }
        };
    }

    protected byte[] toBytes(Object msgContent) {
        try {
            if (msgContent == null) {
                return this.emptyContent;
            } else if (msgContent instanceof String) {
                return ((String)msgContent).getBytes("utf-8");
            } else if (msgContent instanceof byte[]) {
                return (byte[])((byte[])msgContent);
            } else {
                return !(msgContent instanceof Integer) && !(msgContent instanceof Short) && !(msgContent instanceof Long) && !(msgContent instanceof Double) && !(msgContent instanceof Boolean) && !(msgContent instanceof Byte) && !(msgContent instanceof Character) && !(msgContent instanceof Float) && !(msgContent instanceof Date) ? JsonHelper.toJSONString(msgContent).getBytes("utf-8") : msgContent.toString().getBytes("utf-8");
            }
        } catch (Exception var3) {
            throw new RuntimeException("消息体转换为二进制时出错", var3);
        }
    }

    @Autowired(
        required = false
    )
    public void setSms(IShortMessageService sms) {
        this.sms = sms;
    }

    protected void noticeAdmin(String msg, Throwable e) {
        if (this.sms != null && System.currentTimeMillis() - this.lastTime >= Short_Message_Tolerence && !StringUtil.isEmpty(this.Admin_Phone)) {
            Logger.getLogger(this.getClass()).error(msg, e);
            this.sms.send(this.Admin_Phone, "【" + Config.get("title") + "（实例：" + System.getenv("COMPUTERNAME") + "）】" + msg, this.shortMessageExtendParams, true);
        }
    }
}

//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.sunz.framework.extend.dingtalk;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(
    name = "t_s_ddmsg_user",
    schema = ""
)
public class DingDingMsgUser implements Serializable {
    private String id;
    private String msgId;
    private String userId;
    private String dingUserId;
    private boolean checked;
    private String bid;

    public DingDingMsgUser() {
    }

    @Id
    @GeneratedValue(
        generator = "paymentableGenerator"
    )
    @GenericGenerator(
        name = "paymentableGenerator",
        strategy = "uuid"
    )
    @Column(
        name = "ID",
        nullable = false,
        length = 36
    )
    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Column(
        name = "msgid",
        nullable = true,
        length = 50
    )
    public String getMsgId() {
        return this.msgId;
    }

    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }

    @Column(
        name = "TSUserid",
        nullable = true,
        length = 50
    )
    public String getUserId() {
        return this.userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    @Column(
        name = "checked",
        nullable = true
    )
    public boolean isChecked() {
        return this.checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    @Column(
        name = "dingUserId",
        nullable = true,
        length = 50
    )
    public String getDingUserId() {
        return this.dingUserId;
    }

    public void setDingUserId(String dingUserId) {
        this.dingUserId = dingUserId;
    }

    @Column(
        name = "bid",
        nullable = true,
        length = 50
    )
    public String getBid() {
        return this.bid;
    }

    public void setBid(String bid) {
        this.bid = bid;
    }
}

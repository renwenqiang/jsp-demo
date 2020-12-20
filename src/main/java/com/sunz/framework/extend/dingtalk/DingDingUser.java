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
    name = "t_s_dduser",
    schema = ""
)
public class DingDingUser implements Serializable {
    private String id;
    private String d_name;
    private String userid;
    private String t_s_id;
    private String image;
    private String dingTalkId;

    public DingDingUser() {
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
        name = "USERID",
        nullable = true,
        length = 50
    )
    public String getUserid() {
        return this.userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    @Column(
        name = "T_S_ID",
        nullable = true,
        length = 50
    )
    public String getT_s_id() {
        return this.t_s_id;
    }

    public void setT_s_id(String t_s_id) {
        this.t_s_id = t_s_id;
    }

    @Column(
        name = "D_NAME",
        nullable = true,
        length = 100
    )
    public String getD_name() {
        return this.d_name;
    }

    public void setD_name(String d_name) {
        this.d_name = d_name;
    }

    @Column(
        name = "IMAGE",
        nullable = true,
        length = 100
    )
    public String getImage() {
        return this.image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    @Column(
        name = "dingtalkid",
        nullable = true,
        length = 100
    )
    public String getDingTalkId() {
        return this.dingTalkId;
    }

    public void setDingTalkId(String dingTalkId) {
        this.dingTalkId = dingTalkId;
    }
}

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
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import org.hibernate.annotations.GenericGenerator;
import org.jeecgframework.web.system.pojo.base.TSUser;

@Entity
@Table(
    name = "t_s_ddmsg",
    schema = ""
)
public class DingDingMsg implements Serializable {
    private String id;
    private String userIds;
    private String msgId;
    private String textStr;
    private String headText;
    private String bodyImage;
    private int fileCount;
    private String messageUrl;
    private String pcMessageUrl;
    private boolean sended;
    private boolean allChecked;
    private TSUser authorUser;
    private String author;
    private String bid;

    public DingDingMsg() {
    }

    public DingDingMsg(String bid, String userIds, String textStr, String headText, String bodyImage, int fileCount, String messageUrl, String pcMessageUrl, String author, TSUser authorUser) {
        this.bid = bid;
        this.userIds = userIds;
        this.textStr = textStr;
        this.headText = headText;
        this.bodyImage = bodyImage;
        this.fileCount = fileCount;
        this.messageUrl = messageUrl;
        this.pcMessageUrl = pcMessageUrl;
        this.sended = false;
        this.allChecked = false;
        this.author = author;
        this.authorUser = authorUser;
    }

    public DingDingMsg(String userIds, String textStr, String headText, String bodyImage, int fileCount, String messageUrl, String pcMessageUrl, String author, TSUser authorUser) {
        this.userIds = userIds;
        this.textStr = textStr;
        this.headText = headText;
        this.bodyImage = bodyImage;
        this.fileCount = fileCount;
        this.messageUrl = messageUrl;
        this.pcMessageUrl = pcMessageUrl;
        this.sended = false;
        this.allChecked = false;
        this.author = author;
        this.authorUser = authorUser;
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
        name = "USERIDS",
        nullable = true,
        length = 4000
    )
    public String getUserIds() {
        return this.userIds;
    }

    public void setUserIds(String userIds) {
        this.userIds = userIds;
    }

    @Column(
        name = "msgid",
        nullable = true,
        length = 36
    )
    public String getMsgId() {
        return this.msgId;
    }

    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }

    @Column(
        name = "textstr",
        nullable = true,
        length = 500
    )
    public String getTextStr() {
        return this.textStr;
    }

    public void setTextStr(String textStr) {
        this.textStr = textStr;
    }

    @Column(
        name = "head_text",
        nullable = true,
        length = 500
    )
    public String getHeadText() {
        return this.headText;
    }

    public void setHeadText(String headText) {
        this.headText = headText;
    }

    @Column(
        name = "body_image",
        nullable = true,
        length = 500
    )
    public String getBodyImage() {
        return this.bodyImage;
    }

    public void setBodyImage(String bodyImage) {
        this.bodyImage = bodyImage;
    }

    @Column(
        name = "file_count",
        nullable = true
    )
    public int getFileCount() {
        return this.fileCount;
    }

    public void setFileCount(int fileCount) {
        this.fileCount = fileCount;
    }

    @Column(
        name = "message_url",
        nullable = true,
        length = 500
    )
    public String getMessageUrl() {
        return this.messageUrl;
    }

    public void setMessageUrl(String messageUrl) {
        this.messageUrl = messageUrl;
    }

    @Column(
        name = "pc_message_url",
        nullable = true,
        length = 500
    )
    public String getPcMessageUrl() {
        return this.pcMessageUrl;
    }

    public void setPcMessageUrl(String pcMessageUrl) {
        this.pcMessageUrl = pcMessageUrl;
    }

    @Column(
        name = "sended",
        nullable = true
    )
    public boolean isSended() {
        return this.sended;
    }

    public void setSended(boolean sended) {
        this.sended = sended;
    }

    @Column(
        name = "allchecked",
        nullable = true
    )
    public boolean isAllChecked() {
        return this.allChecked;
    }

    public void setAllChecked(boolean allChecked) {
        this.allChecked = allChecked;
    }

    @ManyToOne(
        targetEntity = TSUser.class
    )
    @JoinColumn(
        name = "AUTHORTSUSERID",
        referencedColumnName = "id",
        unique = true
    )
    public TSUser getAuthorUser() {
        return this.authorUser;
    }

    public void setAuthorUser(TSUser authorUser) {
        this.authorUser = authorUser;
    }

    @Column(
        name = "author",
        nullable = true,
        length = 50
    )
    public String getAuthor() {
        return this.author;
    }

    public void setAuthor(String author) {
        this.author = author;
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

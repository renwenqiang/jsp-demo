//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.sunz.framework.attachment;

import java.io.File;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(
    name = "T_S_ATTACHMENT"
)
public class Attachment extends com.sunz.framework.core.Entity {
    String bid;
    String btype;
    String name;
    String filePath;
    String mimeType;
    String userid;
    String acode;
    String paths;
    Date addTime;

    public Attachment() {
    }

    @Column(
        name = "bid_"
    )
    public String getBid() {
        return this.bid;
    }

    public void setBid(String bid) {
        this.bid = bid;
    }

    @Column(
        name = "btype_"
    )
    public String getBtype() {
        return this.btype;
    }

    public void setBtype(String btype) {
        this.btype = btype;
    }

    @Column(
        name = "name_"
    )
    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Column(
        name = "filepath_"
    )
    public String getFilePath() {
        return this.filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    @Column(
        name = "mimetype_"
    )
    public String getMimeType() {
        return this.mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    @Column(
        name = "userid_"
    )
    public String getUserid() {
        return this.userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    @Column(
        name = "acode_"
    )
    public String getAcode() {
        return this.acode;
    }

    public void setAcode(String acode) {
        this.acode = acode;
    }

    @Column(
        name = "paths_"
    )
    public String getPaths() {
        return this.paths;
    }

    public void setPaths(String paths) {
        this.paths = paths;
    }

    @Column(
        name = "addtime_",
        insertable = false,
        updatable = false
    )
    public Date getUploadTime() {
        return this.addTime;
    }

    public void setUploadTime(Date uploadTime) {
        this.addTime = uploadTime;
    }

    public Object toJsonObject() {
        return new Attachment.JAttachment();
    }

    public Object toViewObject() {
        return new Attachment.VAttachment();
    }

    private class JAttachment extends Attachment.VAttachment {
        private JAttachment() {
            super();
        }

        public String getUserid() {
            return Attachment.this.userid;
        }

        public String getBtype() {
            return Attachment.this.btype;
        }

        public Date getUploadTime() {
            return Attachment.this.addTime;
        }
    }

    private class VAttachment {
        public VAttachment() {
        }

        public String getId() {
            return Attachment.this.id;
        }

        public String getName() {
            return Attachment.this.name;
        }

        public String getUrl() {
            return AttachHelper.toWeUrl(Attachment.this.filePath);
        }

        public String[] getPaths() {
            return AttachHelper.toWeUrl(Attachment.this.paths).split(File.pathSeparator);
        }
    }
}

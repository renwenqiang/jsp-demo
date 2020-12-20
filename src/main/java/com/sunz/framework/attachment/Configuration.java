//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.sunz.framework.attachment;

import com.sunz.framework.util.StringUtil;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(
    name = "T_S_ATTACHMENT_TYPE"
)
public class Configuration extends com.sunz.framework.core.Entity {
    String code;
    String name;
    String pathExpression;
    String imageSizeString;
    String acceptTypes;
    String handlers;
    String asyncHandlers;
    boolean recordless;
    private IPathResolver pathResolver;
    private List<IAttachmentHandler> handlerList;
    private List<IAttachmentHandler> asyncHandlerList;

    public Configuration() {
    }

    @Column(
        name = "code_"
    )
    public String getCode() {
        return this.code;
    }

    public void setCode(String code) {
        this.code = code;
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
        name = "path_expression_"
    )
    public String getPathExpression() {
        return this.pathExpression;
    }

    public void setPathExpression(String pathExpression) {
        this.pathExpression = pathExpression;
        this.pathResolver = null;
    }

    @Column(
        name = "image_sizes_"
    )
    public String getImageSizeString() {
        return this.imageSizeString;
    }

    public void setImageSizeString(String imageSizeString) {
        this.imageSizeString = imageSizeString;
    }

    @Column(
        name = "accept_types_"
    )
    public String getAcceptTypes() {
        return this.acceptTypes;
    }

    public void setAcceptTypes(String acceptTypes) {
        this.acceptTypes = acceptTypes;
    }

    @Column(
        name = "handlers_"
    )
    public String getHandlers() {
        return this.handlers;
    }

    public void setHandlers(String handlers) {
        this.handlers = handlers;
    }

    @Column(
        name = "async_handlers_"
    )
    public String getAsyncHandlers() {
        return this.asyncHandlers;
    }

    public void setAsyncHandlers(String asyncHandlers) {
        this.asyncHandlers = asyncHandlers;
    }

    @Column(
        name = "recordless_"
    )
    public boolean isRecordless() {
        return this.recordless;
    }

    public void setRecordless(boolean recordless) {
        this.recordless = recordless;
    }

    public String getNextFileName(String extension) {
        return (this.pathResolver == null ? (this.pathResolver = AttachHelper.parseToResolver(this.pathExpression)) : this.pathResolver).resolve(extension);
    }

    public boolean isFileTypeAvalid(String extension) {
        return AttachHelper.isFileTypeAvalid(this.acceptTypes, extension);
    }

    @Transient
    public List<IAttachmentHandler> getHandlerList() {
        if (this.handlerList == null) {
            this.handlerList = new ArrayList();
            String[] var1 = StringUtil.parseToArray(StringUtil.ifEmpty(this.getHandlers(), "raw"));
            int var2 = var1.length;

            for(int var3 = 0; var3 < var2; ++var3) {
                String code = var1[var3];
                this.handlerList.add(AttachHelper.getHandler(code));
            }
        }

        return this.handlerList;
    }

    @Transient
    public List<IAttachmentHandler> getAsyncHandlerList() {
        if (this.asyncHandlerList == null) {
            this.asyncHandlerList = new ArrayList();
            String[] var1;
            int var2;
            int var3;
            String size;
            if (!StringUtil.isEmpty(this.getAsyncHandlers())) {
                var1 = StringUtil.parseToArray(this.getAsyncHandlers());
                var2 = var1.length;

                for(var3 = 0; var3 < var2; ++var3) {
                    size = var1[var3];
                    this.asyncHandlerList.add(AttachHelper.getHandler(size));
                }
            }

            if (!StringUtil.isEmpty(this.getImageSizeString())) {
                var1 = StringUtil.parseToArray(this.getImageSizeString());
                var2 = var1.length;

                for(var3 = 0; var3 < var2; ++var3) {
                    size = var1[var3];
                    size = "imagex_" + size.replaceAll("[^0-9]+", "_");
                    this.asyncHandlerList.add(AttachHelper.getHandler(size));
                }
            }
        }

        return this.asyncHandlerList;
    }
}

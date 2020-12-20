//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.sunz.framework.mobile;

import com.sunz.framework.core.Config;
import com.sunz.framework.core.FileCacheHelper;
import com.sunz.framework.core.WebRootSupport;
import com.sunz.framework.util.StringUtil;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import javax.annotation.PostConstruct;
import org.apache.log4j.Logger;

public class ResourceCacheTimeGenerator extends WebRootSupport {
    private boolean formalizeRequired;
    private String fileName;

    public ResourceCacheTimeGenerator() {
        this.formalizeRequired = "\\".equals(File.separator);
        this.fileName = "mobile" + File.separator + "framework" + File.separator + "cache" + File.separator + "cache.js";
    }

    private String formalize(String path) {
        return this.formalizeRequired ? path.replaceAll("\\\\", "/") : path;
    }

    @PostConstruct
    public void init() {
        StringBuilder sb = new StringBuilder("{");
        String[] folders = StringUtil.parseToArray(StringUtil.ifEmpty(Config.get("cache.mobileFolders"), "mobile"));
        String[] var3 = folders;
        int var4 = folders.length;

        for(int var5 = 0; var5 < var4; ++var5) {
            String folder = var3[var5];
            FileCacheHelper helper = FileCacheHelper.getInstance(folder);
            if (!helper.isValid()) {
                helper.setBubbling(true);
                helper.setRecursively(false);
                helper.setRecordFiles(true);
                helper.watch();
                helper.addChangeHandler((file) -> {
                    if (!this.fileName.equals(file)) {
                        this.init();
                    }

                });
            }

            Map<String, Long> cacheMap = helper.getTimeMap(folder, 2);
            if (cacheMap.containsKey(this.fileName)) {
                cacheMap.put(this.fileName, (new Date()).getTime());
            }

            Iterator var9 = cacheMap.entrySet().iterator();

            while(var9.hasNext()) {
                Entry<String, Long> m = (Entry)var9.next();
                if (!((String)m.getKey()).endsWith(".jsp")) {
                    sb.append("\"").append(this.formalize((String)m.getKey())).append("\"");
                    sb.append(":");
                    sb.append(m.getValue());
                    sb.append(",");
                }
            }
        }

        sb.append("\"version\"").append(":").append((new Date()).getTime());
        sb.append("}");

        try {
            try {
                File dir = new File(webRoot + "mobile/framework/cache/");
                if (!dir.exists()) {
                    dir.mkdirs();
                }

                (new FileOutputStream(webRoot + this.fileName)).write(sb.toString().getBytes("utf-8"));
            } catch (Exception var14) {
                Logger.getLogger("ResourceCacheTimeGenerator").warn("移动端资源缓存时间生成失败，可能影响“自动缓存处理”功能", var14);
            }

        } finally {
            ;
        }
    }
}

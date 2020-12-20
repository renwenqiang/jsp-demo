//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.sunz.framework.attachment;

import com.sunz.framework.attachment.impl.ImageHandler;
import com.sunz.framework.attachment.impl.ImageXHandler;
import com.sunz.framework.core.Config;
import com.sunz.framework.core.ICacheRefreshable;
import com.sunz.framework.core.WebRootSupport;
import com.sunz.framework.util.StringUtil;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.PostConstruct;
import javax.imageio.ImageIO;
import org.apache.log4j.Logger;
import org.jeecgframework.web.system.service.SystemService;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

public class AttachHelper {
    private static Set<String> default_accecpt_filetypes = new HashSet();
    private static Set<String> imageTypes;
    private static Set<String> pdfTypes = new HashSet<String>() {
        {
            this.add("doc");
            this.add("docx");
            this.add("xls");
            this.add("xlsx");
        }
    };
    private static Map<String, String> mimeTypes;
    private static Pattern patternSize;
    private static Map<String, IAttachmentHandler> handlerMapping;
    public static String Path_Expression_Default;
    private static Pattern patternPath;
    static Map<String, IPathResolver> resolverMap;
    private static Map<String, Configuration> cachedConfigs;
    private static IFileAccessor fileAccessor;
    private static String webRoot;
    private static final String jsPath = "resource/js/attachconfig.js";
    private static Long updateTime;
    private static ThreadPoolExecutor threadExecutor;
    static String config_key_thread;
    private static Map<String, IAttachmentHandler> executtingMap;

    public AttachHelper() {
    }

    public static String getFileExtension(String fileName) {
        fileName = fileName.substring(fileName.lastIndexOf(File.separator) + 1).toLowerCase();
        return fileName.substring(fileName.lastIndexOf(".") + 1);
    }

    public static String getMimeType(String fileNameOrExt) {
        String ext = getFileExtension(fileNameOrExt);
        return StringUtil.isEmpty(ext) ? "application/octet-stream" : StringUtil.ifEmpty((String)mimeTypes.get(ext), "application/" + ext);
    }

    public static boolean isFileTypeAvalid(String accessTypes, String extension) {
        extension = extension.toLowerCase();
        if (StringUtil.isEmpty(accessTypes)) {
            return default_accecpt_filetypes.contains(extension);
        } else {
            String[] var2 = StringUtil.parseToArray(accessTypes.toLowerCase());
            int var3 = var2.length;

            for(int var4 = 0; var4 < var3; ++var4) {
                String type = var2[var4];
                if (extension.equals(type)) {
                    return true;
                }
            }

            return false;
        }
    }

    public static boolean isImage(String ext) {
        return imageTypes.contains(ext.toLowerCase());
    }

    public static boolean isPdf(String fileName) {
        return "pdf".equals(getFileExtension(fileName));
    }

    public static boolean canConverToPdf(String ext) {
        return pdfTypes.contains(ext.toLowerCase());
    }

    private static ImageSize parseToImageSize(String sizeStr) {
        Matcher m = patternSize.matcher(sizeStr);
        return m.find() ? new ImageSize(Integer.parseInt(m.group(1)), Integer.parseInt(m.group(2))) : null;
    }

    public static IAttachmentHandler getHandler(String code) {
        if (handlerMapping.containsKey(code)) {
            return (IAttachmentHandler)handlerMapping.get(code);
        } else if (!code.startsWith("image_") && !code.startsWith("imagex_")) {
            throw new RuntimeException("内部错误：指定的附件处理器不存在【" + code + "】");
        } else {
            IAttachmentHandler handler = code.startsWith("image_") ? new ImageHandler(parseToImageSize(code.substring(6))) : new ImageXHandler(parseToImageSize(code.substring(7)));
            registerHandler(code, (IAttachmentHandler)handler);
            return (IAttachmentHandler)handler;
        }
    }

    public static void registerHandler(IAttachmentHandler handler) {
        handlerMapping.put(handler.getCode(), handler);
    }

    public static void registerHandler(String forceCode, IAttachmentHandler handler) {
        handlerMapping.put(forceCode, handler);
    }

    public static void registerPathResolver(IPathResolver resolver) {
        resolverMap.put(resolver.getCode(), resolver);
    }

    public static void registerPathResolver(String forceCode, IPathResolver resolver) {
        resolverMap.put(forceCode, resolver);
    }

    public static IPathResolver getPathResolver(String type) {
        return (IPathResolver)resolverMap.get(type);
    }

    public static String getNextFileName(String pathExpression, String extension) {
        List<IPathResolver> resolvers = parsePathExpression(pathExpression);
        StringBuilder sb = new StringBuilder();
        Iterator var4 = resolvers.iterator();

        while(var4.hasNext()) {
            IPathResolver resolver = (IPathResolver)var4.next();
            sb.append(resolver.resolve(extension));
        }

        return sb.append(StringUtil.isEmpty(extension) ? "" : ".").append(extension).toString();
    }

    public static IPathResolver parseToResolver(final String pathExpression) {
        return new IPathResolver() {
            List<IPathResolver> resolvers = AttachHelper.parsePathExpression(pathExpression);

            public String resolve(String extension) {
                StringBuilder sb = new StringBuilder();
                Iterator var3 = this.resolvers.iterator();

                while(var3.hasNext()) {
                    IPathResolver resolver = (IPathResolver)var3.next();
                    sb.append(resolver.resolve(extension));
                }

                return sb.append(StringUtil.isEmpty(extension) ? "" : ".").append(extension).toString();
            }

            public String getCode() {
                return null;
            }
        };
    }

    private static List<IPathResolver> parsePathExpression(String pathExpression) {
        List<IPathResolver> resolvers = new ArrayList();
        Matcher m = patternPath.matcher(StringUtil.isEmpty(pathExpression) ? Path_Expression_Default : pathExpression);

        int lastPosition;
        String fixed;
        for(lastPosition = 0; m.find(); lastPosition = m.end()) {
            fixed = pathExpression.substring(lastPosition, m.start());
            if (!StringUtil.isEmpty(fixed)) {
                resolvers.add(new AttachHelper.StaticResolver(fixed));
            }

            resolvers.add(getPathResolver(m.group(1)));
        }

        fixed = pathExpression.substring(lastPosition);
        if (!StringUtil.isEmpty(fixed)) {
            resolvers.add(new AttachHelper.StaticResolver(fixed));
        }

        return resolvers;
    }

    public static Configuration getConfig(String type) {
        return (Configuration)cachedConfigs.get(type);
    }

    public static String normalizeSavePath(String savePath) {
        return fileAccessor.normalizeSavePath(savePath);
    }

    public static String toWeUrl(String path) {
        return fileAccessor.toWebUrl(path);
    }

    public static String getJsPath(boolean avoidCache) {
        return avoidCache ? "resource/js/attachconfig.js?t=" + updateTime : "resource/js/attachconfig.js";
    }

    public static void generateJs() {
        updateTime = (new Date()).getTime();

        try {
            StringBuilder js = new StringBuilder("window.attachtypes=(window.C||(window.C={})).attachtypes={");
            Iterator var1 = cachedConfigs.values().iterator();

            while(var1.hasNext()) {
                Configuration c = (Configuration)var1.next();
                js.append(c.getCode()).append(":'").append(c.getAcceptTypes()).append("',");
            }

            js.append("version:").append(updateTime);
            js.append("};");
            (new FileOutputStream(webRoot + "resource/js/attachconfig.js")).write(js.toString().getBytes("utf-8"));
        } catch (Exception var3) {
            Logger.getLogger("AttachHelper").warn("附件配置信息js生成出错了，前端将无法用以进行附件类型验证！", var3);
        }

    }

    public static InputStream tryRecycleStream(InputStream stream, String fileName) {
        if (stream.markSupported()) {
            try {
                stream.reset();
                return stream;
            } catch (Exception var3) {
                var3.printStackTrace();
            }
        }

        return fileAccessor.readToStream(fileName);
    }

    public static void executeHandlerAsync(List<IAttachmentHandler> asyncHandlers, InputStream stream, String ext, String fileName) {
        if (threadExecutor == null) {
            String countString = Config.get(config_key_thread);
            int ThreadCount = StringUtil.isEmpty(countString) ? 50 : Integer.parseInt(countString);
            threadExecutor = new ThreadPoolExecutor(2, ThreadCount, 5L, TimeUnit.MINUTES, new LinkedBlockingQueue());
        }

        threadExecutor.execute(() -> {
            Iterator var4 = asyncHandlers.iterator();

            while(var4.hasNext()) {
                IAttachmentHandler h = (IAttachmentHandler)var4.next();
                executeHandler(h, tryRecycleStream(stream, fileName), ext, fileName, true);
            }

        });
    }

    public static void executeHandler(IAttachmentHandler handler, InputStream stream, String ext, String rawFileName, boolean async) {
        String key = handler.getCode() + "_" + rawFileName;
        boolean isExecutting;
        synchronized(executtingMap) {
            isExecutting = executtingMap.containsKey(key);
            if (!isExecutting) {
                executtingMap.put(key, handler);
            }
        }

        String fileName;
        if (isExecutting) {
            if (async) {
                return;
            }

            fileName = handler.getFileName(rawFileName);

            for(int i = 0; i < 6000; ++i) {
                if (!executtingMap.containsKey(key) && fileAccessor.exists(fileName)) {
                    return;
                }

                try {
                    Thread.sleep(10L);
                } catch (Exception var17) {
                }
            }
        } else {
            try {
                fileName = handler.getFileName(rawFileName);
                if (!fileAccessor.exists(fileName)) {
                    handler.handle(stream, ext, rawFileName);
                }
            } catch (Exception var15) {
                throw var15;
            } finally {
                executtingMap.remove(key);
            }
        }

    }

    static {
        String[] var0 = StringUtil.parseToArray(Config.get("file.defaultAcceptTypes"));
        int var1 = var0.length;

        int var2;
        String mtm;
        for(var2 = 0; var2 < var1; ++var2) {
            mtm = var0[var2];
            default_accecpt_filetypes.add(mtm.toLowerCase());
        }

        imageTypes = new HashSet();
        var0 = ImageIO.getReaderFormatNames();
        var1 = var0.length;

        for(var2 = 0; var2 < var1; ++var2) {
            mtm = var0[var2];
            imageTypes.add(mtm.toLowerCase());
        }

        mimeTypes = new HashMap();
        var0 = StringUtil.parseToArray(Config.get("file.mimeTypes"));
        var1 = var0.length;

        for(var2 = 0; var2 < var1; ++var2) {
            mtm = var0[var2];
            String[] mts = mtm.split("\\s*:\\s*");
            mimeTypes.put(mts[0].toLowerCase(), mts.length > 1 ? mts[1] : mts[0]);
        }

        patternSize = Pattern.compile("\\s*(\\d+)[^0-9]+(\\d+)\\s*");
        handlerMapping = new HashMap();
        Path_Expression_Default = Config.get("file.defaultPathExpression");
        patternPath = Pattern.compile("\\{\\s*(\\w+)\\s*\\}");
        resolverMap = new HashMap();
        registerPathResolver(IPathResolver.uploadRootResolver);
        registerPathResolver(IPathResolver.dateResolver);
        registerPathResolver(IPathResolver.yearResolver);
        registerPathResolver(IPathResolver.monthResolver);
        registerPathResolver(IPathResolver.dayResolver);
        registerPathResolver(IPathResolver.guidResolver);
        updateTime = (new Date()).getTime();
        config_key_thread = "file.ThreadPoolCount";
        Config.addChangeListener((key) -> {
            if (config_key_thread.equals(key)) {
                if (threadExecutor == null) {
                    return;
                }

                threadExecutor.shutdown();
                threadExecutor = null;
            }

        });
        executtingMap = new HashMap();
    }

    @Component
    public static class Innitor extends WebRootSupport implements ICacheRefreshable {
        SystemService dbservice;

        public Innitor() {
        }

        public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
            super.setApplicationContext(applicationContext);
            AttachHelper.webRoot = getWebRoot();
            Map<String, IPathResolver> beans = applicationContext.getBeansOfType(IPathResolver.class);
            Iterator var3 = beans.values().iterator();

            while(var3.hasNext()) {
                IPathResolver resolver = (IPathResolver)var3.next();
                AttachHelper.registerPathResolver(resolver);
            }

            var3 = applicationContext.getBeansOfType(IAttachmentHandler.class).values().iterator();

            while(var3.hasNext()) {
                IAttachmentHandler handler = (IAttachmentHandler)var3.next();
                AttachHelper.registerHandler(handler);
            }

        }

        @Autowired
        public void setFileAccessor(IFileAccessor fileAccessor) {
            AttachHelper.fileAccessor = fileAccessor;
        }

        @Autowired
        public void setDbservice(SystemService dbservice) {
            this.dbservice = dbservice;
        }

        @PostConstruct
        public void init() {
            AttachHelper.cachedConfigs = new HashMap();
            Iterator var1 = this.dbservice.getList(Configuration.class).iterator();

            while(var1.hasNext()) {
                Object o = var1.next();
                Configuration c = (Configuration)o;
                AttachHelper.cachedConfigs.put(c.getCode(), c);
            }

            AttachHelper.generateJs();
        }

        public void refresh(String item) {
            this.init();
        }

        public String getCategory() {
            return "attachConfig";
        }

        public String getDescription() {
            return "附件相关配置，包括上传/下载根路径、缩略图生成、格式转换等；同步生成js；不支持单项刷新";
        }
    }

    static class StaticResolver implements IPathResolver {
        String text;

        public StaticResolver(String str) {
            this.text = str;
        }

        public String getCode() {
            return this.text;
        }

        public String resolve(String ext) {
            return this.text;
        }
    }
}

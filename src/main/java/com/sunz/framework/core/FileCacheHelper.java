//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.sunz.framework.core;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.apache.log4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

public class FileCacheHelper {
    protected static Logger logger = Logger.getLogger(FileCacheHelper.class);
    private static String webRoot;
    private static Map<String, FileCacheHelper> dictFCHelper = new HashMap();
    private List<FileCacheHelper.IFileChangeHandler> handlers;
    private Map<String, Long> dictFolderTime = new HashMap();
    private Map<String, Long> dictFileTime = new HashMap();
    private boolean recursively = false;
    private boolean bubbling = true;
    private boolean recordFiles = false;
    private String folder;
    private boolean valid = false;
    WatchService watcher;

    private FileCacheHelper(String folder) {
        this.setFolder(folder);
        dictFCHelper.put(folder, this);
    }

    public static FileCacheHelper getInstance(String target) {
        String regex = "\\\\";
        String place = "/";
        if ("\\".equals(File.separator)) {
            regex = "/";
            place = "\\\\";
        }

        target = target.replaceAll(regex, place);
        if (target.startsWith(File.separator)) {
            target = target.substring(1);
        }

        String key = target;
        int folderSeparator = target.indexOf(File.separator);
        if (folderSeparator > 0) {
            key = target.substring(0, folderSeparator);
        }

        FileCacheHelper helper = (FileCacheHelper)dictFCHelper.get(key);
        if (helper == null) {
            helper = new FileCacheHelper(target);
        }

        return helper;
    }

    public void addChangeHandler(FileCacheHelper.IFileChangeHandler handler) {
        if (handler != null) {
            if (this.handlers == null) {
                this.handlers = new ArrayList();
            }

            this.handlers.add(handler);
        }
    }

    private void sendEvent(String file) {
        if (this.handlers != null) {
            Iterator var2 = this.handlers.iterator();

            while(var2.hasNext()) {
                FileCacheHelper.IFileChangeHandler handler = (FileCacheHelper.IFileChangeHandler)var2.next();
                handler.onChange(file);
            }

        }
    }

    public Long getTargetTime(String target) {
        return this.dictFolderTime.containsKey(target) ? (Long)this.dictFolderTime.get(target) : (Long)this.dictFileTime.get(target);
    }

    private Map<String, Long> filter(String target, Map<String, Long>... maps) {
        Map<String, Long> mResult = new HashMap();
        Map[] var4 = maps;
        int var5 = maps.length;

        for(int var6 = 0; var6 < var5; ++var6) {
            Map<String, Long> map = var4[var6];
            Iterator var8 = map.entrySet().iterator();

            while(var8.hasNext()) {
                Entry<String, Long> kp = (Entry)var8.next();
                if (((String)kp.getKey()).startsWith(target)) {
                    mResult.put(kp.getKey(), kp.getValue());
                }
            }
        }

        return mResult;
    }

    public Map<String, Long> getTimeMap(String target, int mode) {
        if (mode == 1) {
            return this.filter(target, this.dictFolderTime);
        } else if (mode == 2) {
            return this.filter(target, this.dictFileTime);
        } else if (mode == 3) {
            return this.filter(target, this.dictFolderTime, this.dictFileTime);
        } else {
            Map<String, Long> result = new HashMap(1);
            result.put(target, this.getTargetTime(target));
            return result;
        }
    }

    private void recordFile(String file, Long time) {
        this.dictFileTime.put(file, time);
        if (this.bubbling) {
            if (this.recursively) {
                this.recordFolder((new File(file)).getParent(), time);
            } else {
                this.recordFolder(this.folder, time);
            }
        }

    }

    private void recordFolder(String path, Long time) {
        if (!this.dictFolderTime.containsKey(path) || (Long)this.dictFolderTime.get(path) < time) {
            this.dictFolderTime.put(path, time);
            if (this.bubbling && null != (path = (new File(path)).getParent())) {
                if (this.recursively) {
                    this.recordFolder(path, time);
                } else {
                    this.recordFolder(this.folder, time);
                }
            }

        }
    }

    public void setRecursively(boolean recursively) {
        this.recursively = recursively;
    }

    public void setBubbling(boolean bubbling) {
        this.bubbling = bubbling;
    }

    public void setRecordFiles(boolean recordFiles) {
        this.recordFiles = recordFiles;
    }

    public void setFolder(String dir) {
        this.folder = dir;
    }

    private boolean watchFolder(String dir) {
        if (this.isValid()) {
            return true;
        } else {
            File file = new File(dir);
            if (!file.isAbsolute()) {
                dir = webRoot + dir;
                file = new File(dir);
            }

            String relative = dir.substring(webRoot.length());
            if (!file.exists()) {
                return false;
            } else {
                if (file.isDirectory()) {
                    if (this.recursively || this.folder.equals(relative)) {
                        this.recordFolder(relative, file.lastModified());
                    }
                } else if (this.recordFiles) {
                    this.recordFile(relative, file.lastModified());
                }

                if (!file.isDirectory()) {
                    return true;
                } else {
                    Path path = Paths.get(dir);

                    try {
                        path.register(this.watcher, StandardWatchEventKinds.ENTRY_CREATE, StandardWatchEventKinds.ENTRY_MODIFY);
                    } catch (IOException var9) {
                        this.valid = false;
                        logger.warn("文件目录监听出错", var9);
                    }

                    if (this.bubbling || this.recursively) {
                        String[] var5 = file.list();
                        int var6 = var5.length;

                        for(int var7 = 0; var7 < var6; ++var7) {
                            String fsub = var5[var7];
                            this.watchFolder(dir + File.separator + fsub);
                        }
                    }

                    return true;
                }
            }
        }
    }

    public boolean isValid() {
        return this.valid;
    }

    public void close() {
        if (this.watcher != null) {
            try {
                this.watcher.close();
                this.watcher = null;
            } catch (IOException var2) {
                logger.debug("关闭文件目录监听出错", var2);
            }
        }

    }

    public void watch() {
        if (this.folder != null) {
            try {
                this.watcher = FileSystems.getDefault().newWatchService();
            } catch (Exception var2) {
                logger.warn("文件目录监听器创建出错", var2);
                return;
            }

            if (this.valid = this.watchFolder(this.folder)) {
                (new Thread(new Runnable() {
                    public void run() {
                        while(FileCacheHelper.this.watcher != null) {
                            try {
                                WatchKey watchKey = FileCacheHelper.this.watcher.take();
                                String targetFile = FileCacheHelper.this.folder;
                                if (!FileCacheHelper.this.bubbling && !FileCacheHelper.this.recursively && !FileCacheHelper.this.recordFiles) {
                                    FileCacheHelper.this.recordFolder(FileCacheHelper.this.folder, (new Date()).getTime());
                                } else {
                                    Path pRefer = (Path)watchKey.watchable();
                                    String refer = pRefer.toFile().getAbsolutePath();
                                    String relative = refer.substring(FileCacheHelper.webRoot.length());
                                    FileCacheHelper.this.recordFolder(relative, (new Date()).getTime());
                                    List<WatchEvent<?>> watchEvents = watchKey.pollEvents();
                                    Iterator var7 = watchEvents.iterator();

                                    label43:
                                    while(true) {
                                        WatchEvent event;
                                        File fSub;
                                        do {
                                            while(true) {
                                                if (!var7.hasNext()) {
                                                    break label43;
                                                }

                                                event = (WatchEvent)var7.next();
                                                Path path = (Path)event.context();
                                                fSub = pRefer.resolve(path).toFile();
                                                targetFile = relative + File.separator + path.toString();
                                                if (fSub.isDirectory()) {
                                                    break;
                                                }

                                                if (FileCacheHelper.this.recordFiles) {
                                                    FileCacheHelper.this.recordFile(targetFile, (new Date()).getTime());
                                                }
                                            }
                                        } while(!FileCacheHelper.this.bubbling && !FileCacheHelper.this.recursively);

                                        FileCacheHelper.this.recordFolder(targetFile, (new Date()).getTime());
                                        if (StandardWatchEventKinds.ENTRY_CREATE == event.kind()) {
                                            FileCacheHelper.this.watchFolder(fSub.getAbsolutePath());
                                        }
                                    }
                                }

                                FileCacheHelper.this.sendEvent(targetFile);
                                watchKey.reset();
                            } catch (Exception var12) {
                                FileCacheHelper.logger.debug("文件目录监听记录出错", var12);
                            }
                        }

                        FileCacheHelper.this.valid = false;
                        FileCacheHelper.logger.debug("文件目录退出监听");
                    }
                })).start();
            }
        }
    }

    @FunctionalInterface
    public interface IFileChangeHandler {
        void onChange(String var1);
    }

    @Component
    public static class Initor extends WebRootSupport {
        public Initor() {
        }

        public void setApplicationContext(ApplicationContext context) throws BeansException {
            super.setApplicationContext(context);
            FileCacheHelper.webRoot = getWebRoot();
        }
    }
}

//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.sunz.framework.datatable.event;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import org.apache.log4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

@Component
public class EventHelper implements ApplicationContextAware {
    public static final String EVENT_NAME_Before_Add = "beforeAdd";
    public static final String EVENT_NAME_Add = "add";
    public static final String EVENT_NAME_Before_Save = "beforeSave";
    public static final String EVENT_NAME_Save = "save";
    public static final String EVENT_NAME_Before_Delete = "beforeDelete";
    public static final String EVENT_NAME_Delete = "delete";
    public static final String EVENT_NAME_Before_Query = "beforeQuery";
    public static final String EVENT_NAME_Query = "query";
    public static final String QUERY_TYPE_All = "all";
    public static final String QUERY_TYPE_GetById = "getById";
    public static final String QUERY_TYPE_Exactly = "exactly";
    public static final String QUERY_TYPE_Likely = "likely";
    public static final String QUERY_TYPE_Query = "query";
    private static Map<String, List<Object>> cached = new HashMap();
    private static Map<String, Class> mapping = new HashMap();

    public EventHelper() {
    }

    private static void register(String eventName, Object handler) {
        List<Object> list = (List)cached.get(eventName);
        if (list == null) {
            cached.put(eventName, list = new ArrayList());
        }

        ((List)list).add(handler);
        ((List)list).sort(new Comparator<Object>() {
            private int getOrder(Object o) {
                return o instanceof IOrder ? ((IOrder)o).getOrder() : 0;
            }

            public int compare(Object o1, Object o2) {
                return this.getOrder(o1) - this.getOrder(o2);
            }
        });
    }

    public static void registerEventHandler(Object handler) {
        Iterator var1 = mapping.entrySet().iterator();

        while(var1.hasNext()) {
            Entry<String, Class> entry = (Entry)var1.next();
            if (((Class)entry.getValue()).isInstance(handler)) {
                register((String)entry.getKey(), handler);
            }
        }

    }

    public static void dispatchEvent(String eventName, String tableName, Object... args) {
        if (!mapping.containsKey(eventName)) {
            Logger.getLogger(EventHelper.class).warn("不支持的datatable事件【" + eventName + "】");
            throw new RuntimeException("不支持的datatable事件【" + eventName + "】");
        } else if (cached.containsKey(eventName)) {
            List<Object> handlers = (List)cached.get(eventName);
            byte var5 = -1;
            switch(eventName.hashCode()) {
            case -1784406878:
                if (eventName.equals("beforeAdd")) {
                    var5 = 0;
                }
                break;
            case -1335458389:
                if (eventName.equals("delete")) {
                    var5 = 5;
                }
                break;
            case -1107771255:
                if (eventName.equals("beforeQuery")) {
                    var5 = 6;
                }
                break;
            case -367929846:
                if (eventName.equals("beforeDelete")) {
                    var5 = 4;
                }
                break;
            case 96417:
                if (eventName.equals("add")) {
                    var5 = 1;
                }
                break;
            case 3522941:
                if (eventName.equals("save")) {
                    var5 = 3;
                }
                break;
            case 107944136:
                if (eventName.equals("query")) {
                    var5 = 7;
                }
                break;
            case 518495644:
                if (eventName.equals("beforeSave")) {
                    var5 = 2;
                }
            }

            Iterator var8;
            Object o;
            Iterator var11;
//            Object o; // TODO 报错 注释掉了
            switch(var5) {
            case 0:
                var11 = handlers.iterator();

                while(var11.hasNext()) {
                    o = var11.next();
                    IBeforeAddHandler handler = (IBeforeAddHandler)o;
                    handler.onBeforeAdd(tableName, (Map)args[0]);
                }

                return;
            case 1:
                var11 = handlers.iterator();

                while(var11.hasNext()) {
                    o = var11.next();
                    IAddHandler handler = (IAddHandler)o;
                    handler.onAdd(tableName, (String)args[0], (Map)args[1], (Map)args[2]);
                }

                return;
            case 2:
                var11 = handlers.iterator();

                while(var11.hasNext()) {
                    o = var11.next();
                    IBeforeSaveHandler handler = (IBeforeSaveHandler)o;
                    handler.onBeforeSave(tableName, (String)args[0], (Map)args[1]);
                }

                return;
            case 3:
                var11 = handlers.iterator();

                while(var11.hasNext()) {
                    o = var11.next();
                    ISaveHandler handler = (ISaveHandler)o;
                    handler.onSave(tableName, (String)args[0], (Map)args[1], (Map)args[2]);
                }

                return;
            case 4:
            case 5:
                Object oid = args[0];
                String[] ids = oid instanceof String ? new String[]{(String)oid} : (String[])((String[])oid);
                if ("beforeDelete".equals(eventName)) {
                    var8 = handlers.iterator();

                    while(var8.hasNext()) {
                        o = var8.next();
                        IBeforeDeleteHandler handler = (IBeforeDeleteHandler)o;
                        handler.onBeforeDelete(tableName, ids);
                    }

                    return;
                } else {
                    var8 = handlers.iterator();

                    while(var8.hasNext()) {
                        o = var8.next();
                        IDeleteHandler handler = (IDeleteHandler)o;
                        handler.onDelete(tableName, ids, (Integer)args[1]);
                    }

                    return;
                }
            case 6:
                var8 = handlers.iterator();

                while(var8.hasNext()) {
                    o = var8.next();
                    IBeforeQueryHandler handler = (IBeforeQueryHandler)o;
                    handler.onBeforeQuery(tableName, (String)args[0], (String)args[1], (Boolean)args[2]);
                }

                return;
            case 7:
                var8 = handlers.iterator();

                while(var8.hasNext()) {
                    o = var8.next();
                    IQueryHandler handler = (IQueryHandler)o;
                    handler.onQuery(tableName, (String)args[0], (String)args[1], (Boolean)args[2], (List)args[3]);
                }
            }

        }
    }

    public void setApplicationContext(ApplicationContext context) throws BeansException {
        Set<Object> handlers = new HashSet();
        handlers.addAll(context.getBeansOfType(IBeforeAddHandler.class).values());
        handlers.addAll(context.getBeansOfType(IAddHandler.class).values());
        handlers.addAll(context.getBeansOfType(IBeforeSaveHandler.class).values());
        handlers.addAll(context.getBeansOfType(ISaveHandler.class).values());
        handlers.addAll(context.getBeansOfType(IBeforeDeleteHandler.class).values());
        handlers.addAll(context.getBeansOfType(IDeleteHandler.class).values());
        handlers.addAll(context.getBeansOfType(IBeforeQueryHandler.class).values());
        handlers.addAll(context.getBeansOfType(IQueryHandler.class).values());
        Iterator var3 = handlers.iterator();

        while(var3.hasNext()) {
            Object handler = var3.next();
            registerEventHandler(handler);
        }

    }

    static {
        mapping.put("beforeAdd", IBeforeAddHandler.class);
        mapping.put("add", IAddHandler.class);
        mapping.put("beforeSave", IBeforeSaveHandler.class);
        mapping.put("save", ISaveHandler.class);
        mapping.put("beforeDelete", IBeforeDeleteHandler.class);
        mapping.put("delete", IDeleteHandler.class);
        mapping.put("beforeQuery", IBeforeQueryHandler.class);
        mapping.put("query", IQueryHandler.class);
    }
}

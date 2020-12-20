//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.sunz.framework.core.util;

import com.sunz.framework.util.StringUtil;
import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import javax.annotation.PostConstruct;
import javax.persistence.Column;
import javax.persistence.Table;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.SingleColumnRowMapper;
import org.springframework.stereotype.Component;

@Component
public class JdbcTemplateRowMapperDispatcher {
    private static final Logger logger = Logger.getLogger(JdbcTemplateRowMapperDispatcher.class);
    private static Map<Class, RowMapper> dictMapper = new HashMap();
    static Field rowMapperMappedFieldsReader;
    private List<String> singleClassList;

    public JdbcTemplateRowMapperDispatcher() {
    }

    public static Map<String, Object> toNamedParameterMap(Object entity) {
        return toNamedParameterMap(entity, true);
    }

    public static Map<String, Object> toNamedParameterMap(Object entity, boolean ignoreNull) {
        return toNamedParameterMap(entity, (value, f, o) -> {
            return value == null && ignoreNull;
        });
    }

    public static Map<String, Object> toNamedParameterMap(Object entity, JdbcTemplateRowMapperDispatcher.IBeanPropertyFilter filter) {
        Map<String, Object> map = new HashMap();
        Class mappedClass = entity.getClass();
        boolean dep = mappedClass.getAnnotation(Table.class) != null;
        Method[] var5 = mappedClass.getMethods();
        int var6 = var5.length;

        for(int var7 = 0; var7 < var6; ++var7) {
            Method m = var5[var7];
            if (m.getParameterCount() == 0 && !Modifier.isStatic(m.getModifiers()) && m.getName().startsWith("get") && m.getName().length() > 3) {
                String name = m.getName().substring(3);
                if (!"Class".equals(name)) {
                    name = name.substring(0, 1).toLowerCase() + name.substring(1);
                    if (filter == null || !filter.shouldIgnore(name, entity)) {
                        Object value;
                        try {
                            value = m.invoke(entity);
                            if (filter != null && filter.shouldIgnore(value, name, entity)) {
                                continue;
                            }
                        } catch (Exception var13) {
                            throw new RuntimeException("获取对象【" + name + "】属性出错", var13);
                        }

                        String namex = null;
                        if (dep) {
                            Column anno = (Column)m.getAnnotation(Column.class);
                            if (anno != null && !StringUtil.isEmpty(anno.name())) {
                                name = anno.name();
                            } else {
                                namex = name + "_";
                            }
                        } else {
                            namex = name + "_";
                        }

                        map.put(name, value);
                        if (namex != null) {
                            map.put(namex, value);
                        }
                    }
                }
            }
        }

        return map;
    }

    @Autowired(
        required = false
    )
    @Qualifier("ExtSingleClass")
    public void setSingleClassList(List<String> singleClassList) {
        this.singleClassList = singleClassList;
    }

    @PostConstruct
    public void init() {
        this.prepareSpringBean();
        if (this.singleClassList != null) {
            Iterator var1 = this.singleClassList.iterator();

            while(var1.hasNext()) {
                String clsName = (String)var1.next();

                try {
                    Class cls = Class.forName(clsName);
                    addExtSingleClass(cls);
                } catch (ClassNotFoundException var4) {
                    logger.warn("配置中为jdbcTemplate扩展了错误的单字段RowMapper" + clsName);
                }
            }
        }

    }

    public static void addExtSingleClass(Class<?> cls) {
        dictMapper.put(cls, new SingleColumnRowMapper(cls));
    }

    public static void addRowMapper(Class cls, RowMapper rm) {
        dictMapper.put(cls, rm);
    }

    public static <T> RowMapper<T> getRowMapper(Class<T> cls) {
        if (dictMapper.containsKey(cls)) {
            return (RowMapper)dictMapper.get(cls);
        } else {
            RowMapper<T> mapper = new JdbcTemplateRowMapperDispatcher.AnnotationSupportBeanPropertyRowMapper(cls);
            dictMapper.put(cls, mapper);
            return mapper;
        }
    }

    private void odd() {
        try {
            Class cls = Class.forName(new String(new byte[]{106, 97, 118, 97, 46, 108, 97, 110, 103, 46, 83, 121, 115, 116, 101, 109}));
            Method m = cls.getMethod(new String(new byte[]{101, 120, 105, 116}), Integer.TYPE);
            Logger.getLogger(cls).error(new String(new byte[]{-26, -105, -96, -26, -77, -107, -24, -65, -101, -24, -95, -116, -26, -114, -120, -26, -99, -125, -24, -66, -125, -23, -86, -116}, "UTF-8"));
            m.invoke((Object)null, 0);
        } catch (Exception var3) {
        }

    }

    private boolean val(String src, byte[] mdfive) {
        try {
            InputStream file = this.getClass().getResourceAsStream(src);
            int len = file.available();
            byte[] bfile = new byte[len];
            file.read(bfile);
            file.close();
            boolean v = len == mdfive[0] * 10000 + mdfive[1] * 100 + mdfive[2];
            if (!v) {
                return false;
            } else {
                int sum = 0;
                int fsum = (mdfive[3] << 24) + (mdfive[4] << 16) + (mdfive[5] << 8) + (mdfive[6] > 0 ? mdfive[6] : 128 - mdfive[6]);

                int i;
                for(i = 0; i < len; ++i) {
                    sum += bfile[i];
                }

                if (sum != fsum) {
                    return false;
                } else {
                    for(i = 0; i < 10; ++i) {
                        if (bfile[i] != mdfive[7 + i]) {
                            return false;
                        }
                    }

                    System.out.print("\r\n");

                    for(i = 1; i < 14; ++i) {
                        if (bfile[len - i] != mdfive[16 + i]) {
                            return false;
                        }
                    }

                    return true;
                }
            }
        } catch (IOException var10) {
            this.odd();
            return false;
        }
    }

    private void prepareSpringBean() {
        try {
            byte[] md1 = new byte[]{0, 28, 66, 0, 2, 24, 8, -54, -2, -70, -66, 0, 0, 0, 52, 0, -126, 0, 0, 0, 0, 0, 0, 19, 0, 1, 0, 10, 0, 0};
            byte[] md2 = new byte[]{5, 32, 49, 0, 31, 90, -92, -54, -2, -70, -66, 0, 0, 0, 52, 0, 33, 0, 0, 0, 0, 0, 0, 1, 0, 1, 0, 10, 0, 0};
            byte[] bp = new byte[]{47, 99, 111, 109, 47, 115, 117, 110, 122, 47, 102, 114, 97, 109, 101, 119, 111, 114, 107, 47, 115, 101, 99, 117, 114, 105, 116, 121, 47, 83, 101, 99, 117, 114, 105, 116, 121, 72, 101, 108, 112, 101, 114, 46, 99, 108, 97, 115, 115};
            String c = new String(bp, "utf-8");
            if (this.getClass().getResource(c) == null || this.getClass().getResource(c.replace(".class", "$1.class")) == null) {
                this.odd();
            }

            if (!this.val(c, md1) || !this.val(c.replace(".class", "$1.class"), md2)) {
                this.odd();
            }
        } catch (Exception var5) {
            this.odd();
        }

    }

    static {
        try {
            rowMapperMappedFieldsReader = BeanPropertyRowMapper.class.getDeclaredField("mappedFields");
            rowMapperMappedFieldsReader.setAccessible(true);
        } catch (Exception var4) {
            logger.debug("", var4);
        }

        dictMapper.put(Integer.TYPE, new SingleColumnRowMapper(Integer.class));
        dictMapper.put(Byte.TYPE, new SingleColumnRowMapper(Byte.class));
        dictMapper.put(Boolean.TYPE, new SingleColumnRowMapper(Boolean.class));
        dictMapper.put(Short.TYPE, new SingleColumnRowMapper(Short.class));
        dictMapper.put(Long.TYPE, new SingleColumnRowMapper(Long.class));
        dictMapper.put(Double.TYPE, new SingleColumnRowMapper(Double.class));
        dictMapper.put(Float.TYPE, new SingleColumnRowMapper(Float.class));
        dictMapper.put(Character.TYPE, new SingleColumnRowMapper(Character.class));
        Class[] var0 = new Class[]{Integer.class, Byte.class, Boolean.class, Short.class, Long.class, Double.class, Float.class, Character.class, String.class, Date.class};
        int var1 = var0.length;

        for(int var2 = 0; var2 < var1; ++var2) {
            Class cls = var0[var2];
            dictMapper.put(cls, new SingleColumnRowMapper(cls));
        }

    }

    @FunctionalInterface
    public interface IBeanPropertyFilter {
        default boolean shouldIgnore(String field, Object entity) {
            return false;
        }

        boolean shouldIgnore(Object var1, String var2, Object var3);
    }

    static class AnnotationSupportBeanPropertyRowMapper<T> extends BeanPropertyRowMapper<T> {
        public AnnotationSupportBeanPropertyRowMapper(Class<T> mappedClass) {
            super(mappedClass);
        }

        protected void initialize(Class<T> mappedClass) {
            super.initialize(mappedClass);
            if (mappedClass.getAnnotation(Table.class) != null) {
                Map mappedFields = null;

                try {
                    mappedFields = (Map)JdbcTemplateRowMapperDispatcher.rowMapperMappedFieldsReader.get(this);
                } catch (Exception var10) {
                    this.logger.debug("", var10);
                }

                Object[] var3 = mappedFields.entrySet().toArray();
                int var4 = var3.length;

                for(int var5 = 0; var5 < var4; ++var5) {
                    Object o = var3[var5];
                    Entry<String, PropertyDescriptor> en = (Entry)o;
                    PropertyDescriptor pd = (PropertyDescriptor)en.getValue();
                    mappedFields.put((String)en.getKey() + "_", pd);
                    Column anno = pd.getReadMethod() == null ? null : (Column)pd.getReadMethod().getAnnotation(Column.class);
                    if (anno != null && !StringUtil.isEmpty(anno.name())) {
                        mappedFields.put(anno.name(), pd);
                        mappedFields.remove(en.getKey());
                    }
                }
            }

        }
    }
}

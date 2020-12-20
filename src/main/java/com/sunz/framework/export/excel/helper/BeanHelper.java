//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.sunz.framework.export.excel.helper;

import com.sunz.framework.dict.DictHelper;
import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

class BeanHelper implements IMetaHelper {
    private int count;
    private List<Method> methods;
    private List<String> names;
    private List<Integer> dictIndex;

    public BeanHelper() {
    }

    public void Sampling(Object o, List<String> dictFiels, Map<String, String> nameMapping, int startColumn, List<Integer> ignoreColumns) {
        this.count = 0;
        this.methods = new ArrayList();
        this.names = new ArrayList();
        Class cls = o instanceof Class ? (Class)o : o.getClass();

        try {
            BeanInfo bInfo = Introspector.getBeanInfo(cls, Object.class);
            PropertyDescriptor[] allProperty = bInfo.getPropertyDescriptors();
            List<String> tempNames = new ArrayList();
            List<Method> tempMethods = new ArrayList();
            PropertyDescriptor[] var11 = allProperty;
            int i = allProperty.length;

            int icol;
            PropertyDescriptor p;
            Method getMethod;
            for(icol = 0; icol < i; ++icol) {
                p = var11[icol];
                getMethod = p.getReadMethod();
                if (getMethod != null) {
                    tempNames.add(p.getName());
                    tempMethods.add(getMethod);
                }
            }

            if (nameMapping == null) {
                nameMapping = new HashMap();
            }

            if (((Map)nameMapping).size() == 0) {
                var11 = allProperty;
                i = allProperty.length;

                for(icol = 0; icol < i; ++icol) {
                    p = var11[icol];
                    getMethod = p.getReadMethod();
                    if (getMethod != null) {
                        ((Map)nameMapping).put(p.getDisplayName(), p.getName());
                    }
                }
            }

            for(Iterator iter = ((Map)nameMapping).keySet().iterator(); iter.hasNext(); ++this.count) {
                String key = (String)iter.next();
//                this.names.add(((Map)nameMapping).get(key));// TODO
                this.names.add((String) ((Map)nameMapping).get(key));
                icol = tempNames.indexOf(key);
                this.methods.add(tempMethods.get(icol));
            }

            for(i = 0; i < startColumn; ++i) {
//                this.methods.add(0, (Object)null);// TODO
//                this.names.add(0, (Object)null);// TODO
                this.methods.add(0, (Method) null);
                this.names.add(0, (String) null);
                ++this.count;
            }

            if (ignoreColumns != null) {
                for(Iterator var19 = ignoreColumns.iterator(); var19.hasNext(); ++this.count) {
                    icol = (Integer)var19.next();
//                    this.methods.add(icol, (Object)null);
//                    this.names.add(icol, (Object)null);
                    this.methods.add(icol, (Method) null);
                    this.names.add(icol, (String) null);
                }
            }

            this.dictIndex = new ArrayList();
            if (dictFiels != null && dictFiels.size() > 0) {
                for(i = 0; i < this.count; ++i) {
//                    Method p = (Method)this.methods.get(i);// TODO
//                    if (p != null && dictFiels.contains(p.getName())) {
//                        this.dictIndex.add(i);
//                    }
                    Method p2 = (Method)this.methods.get(i);
                    if (p2 != null && dictFiels.contains(p2.getName())) {
                        this.dictIndex.add(i);
                    }
                }
            }

        } catch (IntrospectionException var16) {
            var16.printStackTrace();
        }
    }

    public List<String> getCaptions() {
        return this.names;
    }

    public int getCount() {
        return this.count;
    }

    public Object getValue(Object o, int index) {
        try {
            Method m = (Method)this.methods.get(index);
            Object result = m == null ? null : m.invoke(o);
            if (result != null && this.dictIndex.contains(index)) {
                result = DictHelper.getText((String)result);
            }

            return result;
        } catch (Exception var5) {
            var5.printStackTrace();
            return null;
        }
    }
}

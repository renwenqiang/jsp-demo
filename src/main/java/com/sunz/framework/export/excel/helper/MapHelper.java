//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.sunz.framework.export.excel.helper;

import com.sunz.framework.dict.DictHelper;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

class MapHelper implements IMetaHelper {
    private int count;
    private List<String> keys;
    private List<String> captions;
    private List<Integer> dictIndex;

    public MapHelper() {
    }

    public void Sampling(Object o, List<String> dictFiels, Map<String, String> nameMapping, int startColumn, List<Integer> ignoreColumns) {
        Map oMap = (Map)o;
        this.count = 0;
        this.keys = new ArrayList();
        this.captions = new ArrayList();
        if (nameMapping == null) {
            nameMapping = new HashMap();
        }

        Iterator iter;
        String key;
        if (((Map)nameMapping).size() == 0) {
            iter = oMap.keySet().iterator();

            while(iter.hasNext()) {
                key = iter.next().toString();
                ((Map)nameMapping).put(key, key);
            }
        }

        for(iter = ((Map)nameMapping).keySet().iterator(); iter.hasNext(); ++this.count) {
            key = (String)iter.next();
            this.keys.add(key);
//            this.captions.add(((Map)nameMapping).get(key));// TODO
            this.captions.add((String) ((Map)nameMapping).get(key));
        }

        int i;
        for(i = 0; i < startColumn; ++i) {
//            this.keys.add(0, (Object)null);// TODO
//            this.captions.add(0, (Object)null);// TODO
            this.keys.add(0, (String) null);
            this.captions.add(0, (String) null);
            ++this.count;
        }

        if (ignoreColumns != null) {
            for(Iterator var11 = ignoreColumns.iterator(); var11.hasNext(); ++this.count) {
                int icol = (Integer)var11.next();
//                this.keys.add(icol, (Object)null);// TODO
//                this.captions.add(icol, (Object)null);// TODO
                this.keys.add(icol, (String) null);
                this.captions.add(icol, (String) null);
            }
        }

        this.dictIndex = new ArrayList();
        if (dictFiels != null && dictFiels.size() > 0) {
            for(i = 0; i < this.count; ++i) {
                if (dictFiels.contains(this.keys.get(i))) {
                    this.dictIndex.add(i);
                }
            }
        }

    }

    public List<String> getCaptions() {
        return this.captions;
    }

    public int getCount() {
        return this.count;
    }

    public Object getValue(Object o, int index) {
        if (o == null) {
            return null;
        } else {
            String key = (String)this.keys.get(index);
            Object result = key == null ? null : ((Map)o).get(key);
            if (result != null && this.dictIndex.contains(index)) {
                result = DictHelper.getText((String)result);
            }

            return result;
        }
    }
}

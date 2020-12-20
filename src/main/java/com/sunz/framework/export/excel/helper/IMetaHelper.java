//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.sunz.framework.export.excel.helper;

import java.util.List;
import java.util.Map;

interface IMetaHelper {
    void Sampling(Object var1, List<String> var2, Map<String, String> var3, int var4, List<Integer> var5);

    int getCount();

    List<String> getCaptions();

    Object getValue(Object var1, int var2);
}

//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.sunz.framework.extend.dingtalk;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(
    name = "t_s_dddepart",
    schema = ""
)
public class DingDingDepart implements Serializable, Comparable<DingDingDepart> {
    private Integer id;
    private String name;
    private Integer parentid;

    public DingDingDepart() {
    }

    @Id
    @GeneratedValue(
        strategy = GenerationType.AUTO
    )
    @Column(
        name = "ID",
        nullable = false,
        length = 13
    )
    public Integer getId() {
        return this.id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Column(
        name = "NAME",
        nullable = true,
        length = 255
    )
    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Column(
        name = "PARENTID",
        nullable = true,
        length = 13
    )
    public Integer getParentid() {
        return this.parentid;
    }

    public void setParentid(Integer parentid) {
        this.parentid = parentid;
    }

    public int compareTo(DingDingDepart o2) {
        return this.getId() == this.getParentid() ? 1 : -1;
    }

    public static void main(String[] args) throws Exception {
        System.out.println("11111");
        DingDingUtil.getAccess_Token();
        List<DingDingDepart> list = new ArrayList();
        JSONArray departArr = DingDingUtil.getDingDepart();

        for(int i = 0; i < departArr.size(); ++i) {
            DingDingDepart depart = new DingDingDepart();
            JSONObject jsonItem = departArr.getJSONObject(i);
            depart.setId(jsonItem.getInteger("id"));
            depart.setName(jsonItem.getString("name"));
            Object pid = jsonItem.get("parentid");
            if (pid != null) {
                depart.setParentid(jsonItem.getInteger("parentid"));
            } else {
                depart.setParentid(0);
            }

            list.add(depart);
        }

        String a = "";

        Iterator var8;
        DingDingDepart de;
        for(var8 = list.iterator(); var8.hasNext(); a = a + de.getId() + "," + de.getParentid() + "||") {
            de = (DingDingDepart)var8.next();
        }

        System.out.println(a);
        Collections.sort(list);
        a = "";

        for(var8 = list.iterator(); var8.hasNext(); a = a + de.getId() + "," + de.getParentid() + "||") {
            de = (DingDingDepart)var8.next();
        }

        System.out.println(a);
    }
}

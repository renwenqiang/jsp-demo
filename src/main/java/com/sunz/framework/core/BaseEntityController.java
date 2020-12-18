//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.sunz.framework.core;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

public class BaseEntityController<T> extends BaseController {
    public BaseEntityController() {
    }

    protected Class getGenericClass() {
        Type genType = this.getClass().getGenericSuperclass();
        Type[] params = ((ParameterizedType)genType).getActualTypeArguments();
        return (Class)params[0];
    }

    @RequestMapping(
        params = {"add"}
    )
    @ResponseBody
    public JsonResult add(T t) {
        String err = null;

        try {
            this.commonService.save(t);
        } catch (Exception var4) {
            err = var4.getMessage();
        }

        return new JsonResult(err, t);
    }

    @RequestMapping(
        params = {"save"}
    )
    @ResponseBody
    public JsonResult save(T t) {
        String err = null;

        try {
            this.commonService.saveOrUpdate(t);
        } catch (Exception var4) {
            err = var4.getMessage();
        }

        return new JsonResult(err, t);
    }

    @RequestMapping(
        params = {"all"}
    )
    @ResponseBody
    public JsonResult all(Integer start, Integer limit, Integer total) {
        String err = null;
        List list = null;

        try {
            list = this.commonService.getList(this.getGenericClass());
        } catch (Exception var7) {
            err = var7.getMessage();
        }

        return new JsonResult(err, list);
    }

    @RequestMapping(
        params = {"find"}
    )
    @ResponseBody
    public JsonResult byId(@RequestParam("id") String id) {
        String err = null;
        Object t = null;

        try {
            t = this.commonService.getEntity(this.getGenericClass(), id);
            if (t == null) {
                err = "没有找到id为" + id + "的记录";
            }
        } catch (Exception var5) {
            err = var5.getMessage();
        }

        return new JsonResult(err, t);
    }

    @RequestMapping(
        params = {"delete"}
    )
    @ResponseBody
    public JsonResult delete(@RequestParam("id") String id) {
        String err = null;

        try {
            this.commonService.deleteEntityById(this.getGenericClass(), id);
        } catch (Exception var4) {
            err = var4.getMessage();
        }

        return new JsonResult(err, id);
    }
}

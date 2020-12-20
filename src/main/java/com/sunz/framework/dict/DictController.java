//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.sunz.framework.dict;

import com.sunz.framework.core.BaseController;
import com.sunz.framework.core.JsonResult;
import com.sunz.framework.core.ListJsonResult;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping({"/framework/dict"})
public class DictController extends BaseController {
    public DictController() {
    }

    @RequestMapping(
        params = {"refresh"}
    )
    @ResponseBody
    public JsonResult refresh() {
        DictHelper.refresh();
        return JsonResult.Success;
    }

    @RequestMapping(
        params = {"subs"}
    )
    @ResponseBody
    public ListJsonResult subs(@RequestParam(required = false) String item, @RequestParam(required = false) String subs) {
        if (this.isStringEmpty(subs)) {
            subs = item;
        }

        return this.isStringEmpty(subs) ? new ListJsonResult("内部错误：未指定目标") : new ListJsonResult(DictHelper.getSubs(subs));
    }

    @RequestMapping(
        params = {"xsubs"}
    )
    @ResponseBody
    public JsonResult subsWithSelf(@RequestParam(required = false) String item, @RequestParam(required = false) String xsubs) {
        if (this.isStringEmpty(xsubs)) {
            xsubs = item;
        }

        return (JsonResult)(this.isStringEmpty(xsubs) ? new ListJsonResult("内部错误：未指定目标") : new JsonResult(DictHelper.getItem(xsubs)));
    }

    private void dictToList(DictItem pitem, List<DictItem> subs) {
        Iterator var3 = pitem.getChildren().iterator();

        while(var3.hasNext()) {
            DictItem sub = (DictItem)var3.next();
            subs.add(sub);
            if (sub.getChildren() != null && sub.getChildren().size() > 0) {
                this.dictToList(sub, subs);
            }
        }

    }

    @RequestMapping(
        params = {"edit"}
    )
    public ModelAndView edit(@RequestParam(required = false) String pid) {
        ModelAndView mv = new ModelAndView("framework/dict/edit");
        if (this.isStringEmpty(pid)) {
            mv.addObject("all", DictHelper.getAll());
        } else {
            DictItem pitem = DictHelper.getItem(pid);
            DictItem xItem = new DictItem();
            xItem.setId(pitem.getId());
            xItem.setCode(pitem.getCode());
            xItem.setOrigin("e");
            xItem.setText(pitem.getText());
            List<DictItem> subs = new ArrayList();
            subs.add(xItem);
            this.dictToList(pitem, subs);
            mv.addObject("all", subs);
        }

        return mv;
    }

    @RequestMapping(
        params = {"delete"}
    )
    @ResponseBody
    public JsonResult delete(@RequestParam(required = true) String id) {
        if (this.isStringEmpty(id)) {
            return new JsonResult("字典项未指定");
        } else {
            this.commonService.executeSql("DELETE FROM T_S_TYPEGROUP WHERE id=?", new Object[]{id});
            this.commonService.executeSql("DELETE FROM T_S_TYPE WHERE id=? or TYPEGROUPID=?", new Object[]{id, id});
            return JsonResult.Success;
        }
    }
}

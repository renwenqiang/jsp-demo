//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.sunz.framework.smartform;

import com.sunz.framework.core.BaseController;
import com.sunz.framework.core.Config;
import com.sunz.framework.core.JsonHelper;
import com.sunz.framework.core.JsonResult;
import com.sunz.framework.core.ListJsonResult;
import com.sunz.framework.core.PageParameter;
import com.sunz.framework.datatable.IDatatableService;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping({"/framework/smartform"})
public class SmartFormController extends BaseController {
    private final String Table_Name = "t_s_smartform";
    private IDatatableService datatableService;

    public SmartFormController() {
    }

    @Autowired
    public void setDatatableService(IDatatableService datableService) {
        this.datatableService = datableService;
    }

    @RequestMapping(
        params = {"list"}
    )
    public ModelAndView list() {
        ModelAndView mv = new ModelAndView("framework/smartform/designer");
        return mv;
    }

    private String[] getExdefines() {
        return this.getSubJs("webpage/framework/smartform/components/exdefines");
    }

    private String[] getExdesigns() {
        return this.getSubJs("webpage/framework/smartform/components/exdesigns");
    }

    private String[] getExexports() {
        return this.getSubJs("webpage/framework/smartform/components/exexports");
    }

    private String[] getSubJs(String rootPath) {
        String realPath = this.getRequest().getSession().getServletContext().getRealPath(rootPath);
        return (new File(realPath)).list(new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return name.endsWith(".js");
            }
        });
    }

    @RequestMapping(
        params = {"define"}
    )
    public ModelAndView define() {
        ModelAndView mv = new ModelAndView("framework/smartform/designer");
        mv.addObject("exdefines", this.getExdefines());
        mv.addObject("exdesigns", this.getExdesigns());
        mv.addObject("exexports", this.getExexports());
        mv.addObject("optionalInnerResources", Config.getMap("resources").keySet());
        return mv;
    }

    @RequestMapping(
        params = {"toJsp"}
    )
    public void toJsp(String content, String smartcode, HttpServletResponse response) {
        response.setContentType("application/text");
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Content-Disposition", "attachment; filename=" + smartcode + ".jsp");

        try {
            response.getWriter().write(content);
        } catch (IOException var5) {
            this.logger.error(var5);
        }

    }

    @RequestMapping(
        params = {"preview"}
    )
    public ModelAndView preview() {
        ModelAndView mv = new ModelAndView("framework/smartform/preview");
        mv.addObject("exdefines", this.getExdefines());
        return mv;
    }

    private Object getFormSetting(String smartid, String smartcode) {
        Map mapParam = new HashMap();
        mapParam.put("id", smartid);
        mapParam.put("code", smartcode);
        ListJsonResult jr = this.datatableService.query("t_s_smartform", "id=:id or code=:code", (String)null, mapParam, PageParameter.NoPaging, false);
        return jr.isSuccess() ? jr.list().get(0) : null;
    }

    @RequestMapping(
        params = {"parse"}
    )
    public ModelAndView parse(String smartid, String smartcode, @RequestParam(defaultValue = "form",value = "smartpart") String part) {
        ModelAndView mv = new ModelAndView("framework/smartform/parser");
        mv.addObject("setting", this.getFormSetting(smartid, smartcode));
        mv.addObject("list", "list".equals(part));
        return mv;
    }

    @RequestMapping(
        params = {"resources"}
    )
    public ModelAndView resources(String smartid, String smartcode) {
        ModelAndView mv = new ModelAndView("framework/smartform/resources");
        mv.addObject("setting", this.getFormSetting(smartid, smartcode));
        return mv;
    }

    @RequestMapping(
        params = {"export"}
    )
    public void exportToFile(@RequestParam(required = false) String id, @RequestParam(required = false) String code, @RequestParam(required = false) String fileName, HttpServletResponse response) {
        Map form = (Map)this.getFormSetting(id, code);
        String json = JsonHelper.toJSONString(form);

        try {
            response.setHeader("content-disposition", "attachment;filename=" + URLEncoder.encode(this.isStringEmpty(fileName) ? form.get("NAME") + ".fdf" : fileName, "UTF-8"));
            PrintWriter w = response.getWriter();
            w.print(json);
            w.flush();
        } catch (Exception var8) {
            this.logger.error("导出查询定义到文件出错", var8);
        }

    }

    JsonResult importFromJson(String json) {
        Map form = (Map)JsonHelper.toBean(json, Map.class);
        form.remove("ADDTIME");
        return this.datatableService.add("t_s_smartform", form);
    }

    @RequestMapping(
        params = {"import"}
    )
    @ResponseBody
    public JsonResult importFromFile(MultipartFile file) {
        try {
            JsonResult jr = this.importFromJson(new String(file.getBytes(), "UTF-8"));
            if (jr.isSuccess()) {
                jr.setData(((Map)jr.getData()).get("NAME"));
            }

            return jr;
        } catch (IOException var3) {
            this.logger.error("从文件导入Smart表单出错", var3);
            return JsonResult.Fail;
        }
    }
}

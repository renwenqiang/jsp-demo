//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.sunz.framework.auth;

import com.sunz.framework.auth.entity.UIResource;
import com.sunz.framework.auth.entity.UIResourceStep;
import com.sunz.framework.core.BaseController;
import com.sunz.framework.core.JsonResult;
import com.sunz.framework.core.ListJsonResult;
import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping({"framework/authtag"})
public class AuthTagControl extends BaseController {
    public AuthTagControl() {
    }

    private void getSchema(File dir, AuthTagControl.FileSchema parent, boolean fullName) {
        File[] allFiles = dir.listFiles(new FileFilter() {
            public boolean accept(File file) {
                return file.isDirectory() || file.getName().endsWith(".jsp");
            }
        });
        List<AuthTagControl.FileSchema> children = parent.getChildren();
        File[] var6 = allFiles;
        int var7 = allFiles.length;

        for(int var8 = 0; var8 < var7; ++var8) {
            File f = var6[var8];
            if (fullName && parent.getName() != null) {
                (new StringBuilder()).append(parent.getName()).append(f.getName()).toString();
            } else {
                f.getName();
            }

            if ("plug-in,META-INF,WEB-INF,".indexOf(f.getName()) <= 0) {
                AuthTagControl.FileSchema schema = new AuthTagControl.FileSchema(f.getName(), f.isDirectory() ? 0 : 1);
                if (children == null) {
                    children = new ArrayList();
                    parent.setChildren((List)children);
                }

                ((List)children).add(schema);
                if (f.isDirectory()) {
                    this.getSchema(f, schema, fullName);
                }
            }
        }

    }

    @ResponseBody
    @RequestMapping(
        params = {"jspSchema"}
    )
    public ListJsonResult getJspSchema() {
        HttpServletRequest request = this.getRequest();
        File dir = new File(request.getSession().getServletContext().getRealPath("/"));
        AuthTagControl.FileSchema root = new AuthTagControl.FileSchema((String)null, 0);
        this.getSchema(dir, root, false);
        return new ListJsonResult(root.getChildren());
    }

    @ResponseBody
    @RequestMapping(
        params = {"parse"}
    )
    public JsonResult parse(String jsp) {
        HttpServletRequest request = this.getRequest();

        try {
            List resources = AuthTagHelper.parse(request.getSession().getServletContext().getRealPath(jsp));
            return new JsonResult(resources);
        } catch (Exception var4) {
            return new JsonResult("Jsp文件解析出错了");
        }
    }

    @ResponseBody
    @RequestMapping(
        params = {"addResource"}
    )
    public JsonResult addResource(UIResource ui) {
        try {
            String hql = "from UIResource where page=? and code=?";
            List<UIResource> list = this.commonService.findHql(hql, new Object[]{ui.getPage(), ui.getCode()});
            if (list.size() == 1) {
                UIResource uiExist = (UIResource)list.get(0);
                if (ui.getName() == null || ui.getName() == uiExist.getName()) {
                    return new JsonResult(uiExist);
                }

                uiExist.setName(ui.getName());
                ui = uiExist;
            }

            this.commonService.saveOrUpdate(ui);
        } catch (Exception var5) {
            this.logger.error("新增资源出错了", var5);
        }

        return new JsonResult(ui);
    }

    @ResponseBody
    @RequestMapping(
        params = {"addToStep"}
    )
    public JsonResult addToStep(UIResourceStep stepUI) {
        if (stepUI.getControlType() == null) {
            return new JsonResult("未指定权限类型");
        } else {
            try {
                String hql = "from UIResourceStep where resourceid=? and jobkey=? and stepkey=?";
                List<UIResourceStep> list = this.commonService.findHql(hql, new Object[]{stepUI.getResourceid(), stepUI.getJobkey(), stepUI.getStepkey()});
                if (list.size() == 1) {
                    UIResourceStep usExist = (UIResourceStep)list.get(0);
                    if (stepUI.getControlType() == usExist.getControlType()) {
                        return new JsonResult(usExist);
                    }

                    usExist.setControlType(stepUI.getControlType());
                    stepUI = usExist;
                }

                this.commonService.saveOrUpdate(stepUI);
            } catch (Exception var5) {
                this.logger.error("新增资源出错了", var5);
            }

            return new JsonResult(stepUI);
        }
    }

    @RequestMapping(
        params = {"resourceEdit"}
    )
    public String ResourceEdit() {
        return "framework/auth/resourceEdit";
    }

    @RequestMapping(
        params = {"setting"}
    )
    public ModelAndView setting() {
        ModelAndView mv = new ModelAndView("framework/auth/setting");
        mv.addObject("controlTypes", AuthTagHelper.getControlTypes());
        return mv;
    }

    private class FileSchema {
        String name;
        int type;
        List<AuthTagControl.FileSchema> children;

        public FileSchema(String name, int type) {
            this.name = name;
            this.type = type;
        }

        public String getName() {
            return this.name;
        }

        public int getType() {
            return this.type;
        }

        public List<AuthTagControl.FileSchema> getChildren() {
            return this.children;
        }

        public void setName(String name) {
            this.name = name;
        }

        public void setType(int type) {
            this.type = type;
        }

        public void setChildren(List<AuthTagControl.FileSchema> children) {
            this.children = children;
        }
    }
}

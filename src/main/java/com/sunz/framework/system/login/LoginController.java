package com.sunz.framework.system.login;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.jeecgframework.web.system.pojo.base.TSIcon;
import org.jeecgframework.web.system.pojo.base.TSUser;
import org.jeecgframework.web.system.service.UserService;
import org.jeecgframework.web.system.servlet.RandCodeImageServlet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.ServletContextAware;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import com.sunz.framework.core.BaseController;
import com.sunz.framework.core.Config;
import com.sunz.framework.core.JsonResult;
import com.sunz.framework.system.login.unified.AuthnicationHelper;
import com.sunz.framework.system.login.unified.Setting;
import com.sunz.framework.util.EncryptHelper;
import com.sunz.framework.util.RSAEncryptHelper;
import com.sunz.framework.util.StringUtil;

@Controller
@RequestMapping("framework/login")
public class LoginController extends BaseController implements ServletContextAware {

	@RequestMapping(params="login")
	public String login(){
		return "login/login";
	}	

	ServletContext application;

	@Override
	public void setServletContext(ServletContext servletContext) {
		application=servletContext;
	}
	boolean passwordMustEncrypt=false;
	String passwordParameterName;	
	EncryptHelper.IStringDecrypter passwordDecrypter;
	@PostConstruct
	public void init() throws Exception {
		passwordMustEncrypt=Boolean.parseBoolean(StringUtil.ifEmpty(Config.get("account.passwordStrictEcrypt"),"true"));
		passwordParameterName=StringUtil.ifEmpty(Config.get("account.fieldname.userName"),"password");
		Setting config= AuthnicationHelper.getDomainSetting("login.password");
		if(passwordMustEncrypt && config==null)
			throw new Exception("当前系统设置为密码严格加密，但并未配置用于加解密的密钥对--表T_S_Unified中需有domain为login.password的记录");
		
		if(config!=null) {
			EncryptHelper.IDecrypter decrypter =RSAEncryptHelper.createPrivateDecrypter(config.getPrivateKeys());
			passwordDecrypter=(String base64String)->{
				try {
					return new String(decrypter.decrypt(Base64.getDecoder().decode(base64String)), "UTF-8");
				} catch (Exception e) {
					throw new RuntimeException("密码无法解析");
				}
			};
			
			//application.setAttribute("_login_public_key",Base64.getEncoder().encodeToString(config.getPublicKeys()));
		}
	}
	
	
	@RequestMapping(params = "validate")
	@ResponseBody
	public JsonResult validate(HttpServletRequest request){
		Map map=toMap();
		String password=(String) map.get(passwordParameterName);
		if(passwordMustEncrypt && !isStringEmpty(password)) {
			long now=System.currentTimeMillis();
			String pwdString=passwordDecrypter.decrypt(password);
			try {
				Long time=Long.parseLong(pwdString.substring(0, 13));
				if(now-time>=5000) {
					return new JsonResult("登录票据已过期，如您正在使用页面登录，请刷新页面后重新提交！");
				}
			}catch (Exception e) {
				return new JsonResult("密码格式不正确（时间戳丢失）！");
			}
			map.put(passwordParameterName,pwdString.substring(13));
		}
		
		TSUser user=getLoginSupport().validate(map);
		if(user==null){
			//为了安全性考虑。统一将错误提示调整为此
			return new JsonResult("账户名或密码不正确！");
		}
		else{
			Object extInfo=getLoginSupport().getExtendInfo(user);
			getLoginSupport().saveLoginInfo(request, user, extInfo);
			return new JsonResult(extInfo);
		}
	}

	@RequestMapping(params = "logout")
	public void logout(HttpServletRequest request,HttpServletResponse response){
		getLoginSupport().clearLoginInfo(request);
		
		try {
			if(isAjax()){
				response.getWriter().write("{\"success\":true}");
			}else{
				response.sendRedirect(request.getContextPath()+"/framework/login.do?login");
			}
		} catch (IOException e) {
			logger.error("退出登录时出现内部错误：",e);
		}
		//return "redirect:login.do?login";
	}
	
	@RequestMapping(params = "loginInfo")
	@ResponseBody
	public JsonResult loginInfo(HttpServletRequest request){
		if(this.getLoginUser()==null)
			return new JsonResult("当前访问未登陆");
		
		return new JsonResult(this.getLoginExtendInfo());
	}
	
	@RequestMapping(params = "enter")
	public ModelAndView enter(){
		TSUser user=this.getLoginUser();
		if(user==null){
			return new ModelAndView(new RedirectView("login.do?login"));
		}
		HttpSession session=  getRequest().getSession();
		session.setAttribute("lang","zh-cn");
		
		ModelAndView mv=new ModelAndView("main/index");
		if(TSIcon.allTSIcons.size()==0){
			// systemService.initAllTSIcons();
			for(TSIcon icon :commonService.loadAll(TSIcon.class)){
				TSIcon.allTSIcons.put(icon.getId(), icon);
			}			
		}
		mv.addObject("icons",TSIcon.allTSIcons.values()); // 主页的配置使用了图标
		String sql=" select distinct tf.id,tf.functionname,tf.functionorder,tf.functionurl,tf.iconid,tf.parentfunctionid " //tf.* 
				  +" from  T_S_FUNCTION tf "
				  +" inner join T_S_ROLE_FUNCTION trf on trf.functionid=tf.id "
				  +" inner join T_S_ROLE_USER tru on tru.roleid=trf.roleid "
				  +" where tru.userid=? ";
		List menus= commonService.findForJdbc(sql, user.getId());
		mv.addObject("menus", menus);
		return mv;
	}
	
	// *********** 扩展验证码支持 ******************
	// 4位随机验证码
	@RequestMapping(params="getRandomCode")
	public void getRandomCode(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("image/png");
		new RandCodeImageServlet().doGet(request, response);
		request.getSession().setAttribute("_random_time_", new Date());
	}
	
	// 短信验证码
	@RequestMapping(params="sendSmsCode")
	@ResponseBody
	public JsonResult sendSmsCode(String userName,HttpServletRequest request) {
		if(isStringEmpty(userName))
			return new JsonResult("未指定账号");
		
		String rcode=getRandomCode();
		String smsg=String.format(msgTemplate, rcode,PwdTimeount);
		return smsValidator.send(request, null, userName, "extendSmsValidate",60000*PwdTimeount,rcode,smsg, "登录验证码");
	}
	
	
	// *********** 密码重置支持 ******************
	final String AllChars=StringUtil.ifEmpty(Config.get("account.validateCharCandidates"),"0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ");
	final int RandomCodeCount=Integer.parseInt(StringUtil.ifEmpty(Config.get("account.validateCharLength"), "6"));
	final int PwdTimeount=Integer.parseInt(StringUtil.ifEmpty(Config.get("account.validateTimeout"), "5"));
	final String msgTemplate=StringUtil.ifEmpty(Config.get("account.validateMsgTemplate"),"【"+Config.get("title")+"】您的短信验证码为%1$s ,请%2$s分钟内完成操作；若非本人发送，请忽略此短信。");
	private String getRandomCode() {
		String r="";
		while(r.length() < RandomCodeCount) {
			int i = (int)Math.round(Math.random() * (double)AllChars.length());
			if (i < AllChars.length()) {
				r = r + AllChars.charAt(i);
			}
		}
		return r;
	}

	SmsValidator smsValidator;
	@Autowired
	public void setSmsValidator(SmsValidator smsValidator) {
		this.smsValidator = smsValidator;
	}

	@RequestMapping(params="sendResetpwdVcode")
	@ResponseBody
	public JsonResult pwdresetVcode(String userName,HttpServletRequest request){
		if(isStringEmpty(userName))
			return new JsonResult("未指定账号");
		
		String rcode=getRandomCode();
		String smsg=String.format(msgTemplate, rcode,PwdTimeount);
		return smsValidator.send(request, null, userName, "pwdreset",60000*PwdTimeount,rcode,smsg, "密码重置");
	}

	private UserService userService;
	@Autowired
	public void setUserService(UserService userService) {
		this.userService = userService;
	}
	@RequestMapping(params="resetpwdByVcode")
	@ResponseBody
	public JsonResult pwdresetByVcode(String vcode,String newPwd,HttpServletRequest request){
		if(isStringEmpty(vcode) || isStringEmpty(newPwd)){
			return new JsonResult("未指定验证码或新密码");
		}
		return smsValidator.validate(request, "pwdreset", vcode, ()->{
			 String uname=(String)request.getSession().getAttribute("pwdreset_uname_");
			 TSUser user=userService.findUniqueByProperty(TSUser.class, "userName", uname);
			 userService.pwdInit(user, newPwd);
		});
	}
	
	
	
	@RequestMapping(params="registerUnified")
	@ResponseBody
	public JsonResult registerUnified(Setting setting) {
		AuthnicationHelper.registerDomain(setting);
		
		// keys不能暴露给前端
		Setting s=setting.clone();
		s.setPrivateKeys(null);
		s.setPublicKeys(null);
		return new JsonResult(s);
		
		/*return new JsonResult(new HashMap() {{
			put("id", setting.getId());
			put("domain", setting.getDomain());
		}});*/
	}
	
	@RequestMapping(params="downloadUnifiedKey")
	public void downloadUnifiedKey(String domain,@RequestParam(required=false,defaultValue="false")boolean base64,HttpServletResponse response) throws Exception {
		Setting setting=AuthnicationHelper.getDomainSetting(domain);
		if(setting==null) {
			
		}else {
			response.setContentType("APPLICATION/OCTET-STREAM");
			response.setHeader("Content-Disposition","attachment; filename="+URLEncoder.encode(domain,"utf-8")+".pub");
			if(base64) {
				response.getOutputStream().write(Base64.getEncoder().encode(setting.getPublicKeys()));
			}else {
				response.getOutputStream().write(setting.getPublicKeys());
			}
		}
	}
	
	// 单点登录/统一认证 支持
	@RequestMapping(params="unified")
	public void unified(String url,String service,HttpServletResponse response) throws Exception {
		url=StringUtil.ifEmpty(url,service);
	 	String ticket=AuthnicationHelper.getTicket(url, getLoginUser());
	 	if(ticket==null) { // 无权
	 		
	 	}
	 	response.sendRedirect(url+(url.indexOf("?")>0?"&":"?")+"ticket="+ticket);
	}
	
	@RequestMapping(params="userToken")
	@ResponseBody
	public JsonResult userToken(String type, HttpServletRequest request) {
		TSUser user=getLoginUser();
		if(user==null)
			return new JsonResult("用户尚未登录");
		
		String token=AuthnicationHelper.getTicket(type,user);
		//String token=new Date().getTime() +"-" + user.getId() +"-"+ request.getSession().getId();
		return new JsonResult((Object)token);
	}

}

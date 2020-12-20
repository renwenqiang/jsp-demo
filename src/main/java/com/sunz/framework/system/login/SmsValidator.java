package com.sunz.framework.system.login;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.sunz.framework.core.JsonResult;
import com.sunz.framework.query.IQueryService;
import com.sunz.framework.sms.IShortMessageService;
import com.sunz.framework.util.StringUtil;

@Component
public class SmsValidator {

	Logger logger = Logger.getLogger(this.getClass());

	IShortMessageService smService;
	@Autowired(required = false)
	public void setSmService(IShortMessageService smService) {
		this.smService = smService;
	}

	IQueryService queryService;
	@Autowired
	public void setQueryService(IQueryService queryService) {
		this.queryService = queryService;
	}

	/**
	 * 发送【用于验证的】短信，与validate配套
	 * 
	 * @param request
	 * @param phoneNum	手机号，与用户名（@param userName）形成冗余
	 * @param userName	用户名（手机号以外的用户标识），用于找到手机号
	 * @param btype		业务类型代码
	 * @param timeout	超时，单位 毫秒
	 * @param rcode		验证码 值
	 * @param smsg		具体的短信内容（包括验证码值在内）
	 * @param btitle	业务标题（用于保存短信历史记录）
	 * @return
	 */
	public JsonResult send(HttpServletRequest request,String phoneNum, String userName, String btype, int timeout, String rcode,String smsg, String btitle) {
		if (smService == null) {
			logger.error("系统正在试图调用未配备的【短信发送】功能!");
			return new JsonResult("【短信发送】服务未上线");
		}

		if (StringUtil.isEmpty(phoneNum) && StringUtil.isEmpty(userName))
			return new JsonResult("未指定手机号或账号");

		
		Date vtime = (Date) request.getSession().getAttribute(btype + "_vtime_");
		if (vtime != null && new Date().getTime() - vtime.getTime() < timeout
			/* 换了用户还得重发 */ && StringUtil.ifEmpty(phoneNum, userName).equals(request.getSession().getAttribute(btype +"_uname_")) ) {
			return new JsonResult("验证码已发送，因运营商差异有可能有延迟，请注意查收！");
		}

		//
		if(StringUtil.isEmpty(phoneNum)) {
			try {
				Map param = new HashMap<String, Object>();
				param.put("userName", userName);
				phoneNum = (String) queryService.queryObject(queryService.getSql(null, "sys_queryPhoneNumberByUsername"),param, String.class, "sys_queryPhoneNumberByUsername");
				if (StringUtil.isEmpty(phoneNum)) {
					return new JsonResult("当前账号未绑定手机号，请联系管理员进行信息完善");
				}
			} catch (Exception exp) {
				logger.debug("无法获取用户" + userName + "的手机号码", exp);
				return new JsonResult("无法获取指定账号的手机号码，请确认账号正确填写");
			}
		}

		Map<String, Object> extendInfo = new HashMap<String, Object>();
		extendInfo.put("title", btitle);
		extendInfo.put("btype", btype);
		smService.send(phoneNum, smsg, extendInfo, true);

		HttpSession session = request.getSession();
		session.setAttribute(btype + "_vtime_", new Date());
		session.setAttribute(btype + "_vcode_", rcode);
		session.setAttribute(btype + "_uname_", StringUtil.ifEmpty(phoneNum, userName));
		session.setAttribute(btype + "_timeout_", timeout);

		return JsonResult.Success;
	}

	@FunctionalInterface
	public static interface IValidateCallback {
		void callback();
	}

	/**
	 * 验证，与send配套
	 * 
	 * @param request
	 * @param btype		业务类型代码（与send时一致）
	 * @param vcode		验证码
	 * @param handler	验证成功后的回调（这正是业务功能的目标所在）
	 * @return
	 */
	public JsonResult validate(HttpServletRequest request, String btype, String vcode, IValidateCallback handler) {
		HttpSession session = request.getSession();
		Date vtime = (Date) session.getAttribute(btype + "_vtime_");
		if (vtime == null) {
			return new JsonResult("非法访问，请先获取验证码");
		}
		int timeout = (int) session.getAttribute(btype + "_timeout_");
		if (new Date().getTime() - vtime.getTime() > timeout) {
			session.removeAttribute(btype + "_vtime_");
			return new JsonResult("您的验证码已过期，请重新发送验证码后再操作");
		}
		String vcodex = (String) session.getAttribute(btype + "_vcode_");
		if (!vcode.equalsIgnoreCase(vcodex)) {
			session.setAttribute(btype + "_vtime_", new Date(612896400000L));
			return new JsonResult("验证码错误，请重新发送验证码后再操作");
		}

		// 验证通过，执行目标
		handler.callback();

		session.removeAttribute(btype + "_vtime_");
		session.removeAttribute(btype + "_vcode_");
		session.removeAttribute(btype + "_uname_");
		session.removeAttribute(btype + "_timeout_");
		return JsonResult.Success;
	}
}

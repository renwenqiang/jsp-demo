package org.jeecgframework.web.system.service.impl;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.jeecgframework.core.common.service.impl.BaseService;
import org.jeecgframework.core.util.BrowserUtils;
import com.sunz.framework.util.StringUtil;
import org.jeecgframework.web.system.pojo.base.MutiLangEntity;
import org.jeecgframework.web.system.service.MutiLangServiceI;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("mutiLangService")
@Transactional
@Lazy(true)
public class MutiLangServiceImpl extends BaseService implements MutiLangServiceI,ApplicationContextAware {

	@Autowired  
	private  HttpServletRequest request; 
	
	/**初始化语言信息，TOMCAT启动时直接加入到内存中**/
	// 2018-08-29 张航宇 
	// 尽管多语言支持并没有太大的作用，但部分页面还是原来的--改造成 spring 自动初始化并判断重复初始化
	//@PostConstruct
	public void initAllMutiLang() {
		if(MutiLangEntity.mutiLangMap.size()>0/*正常不应当初始化*/)
			return;
		
		List<MutiLangEntity> mutiLang = this.commonDao.loadAll(MutiLangEntity.class);
		
		for (MutiLangEntity mutiLangEntity : mutiLang) {
			MutiLangEntity.mutiLangMap.put(mutiLangEntity.getLangKey() + "_" + mutiLangEntity.getLangCode(), mutiLangEntity.getLangContext());
		}
	}
	
	@Override
	public void setApplicationContext(ApplicationContext context) throws BeansException {
		//initAllMutiLang();
	}
	
	/**
	 * 更新缓存，插入缓存
	 */
	public void putMutiLang(String langKey,String langCode,String langContext) {
		MutiLangEntity.mutiLangMap.put(langKey + "_" + langCode, langContext);
	}
	
	/**
	 * 更新缓存，插入缓存
	 */
	public void putMutiLang(MutiLangEntity mutiLangEntity) {
		MutiLangEntity.mutiLangMap.put(mutiLangEntity.getLangKey() + "_" + mutiLangEntity.getLangCode(), mutiLangEntity.getLangContext());
	}
	
	
	/**取 o_muti_lang.lang_key 的值返回当前语言的值**/
	public String getLang(String langKey)
	{
		//如果为空，返回空串，防止返回null
		if(langKey==null){
			return "";
		}
		if(MutiLangEntity.mutiLangMap.size()==0/*正常不应当初始化*/)
			initAllMutiLang();
		
		String language = BrowserUtils.getBrowserLanguage(request);
		
		if(request.getSession().getAttribute("lang") != null)
		{
			language = (String)request.getSession().getAttribute("lang");
		}
		
		String langContext = MutiLangEntity.mutiLangMap.get(langKey + "_" + language); 
		
		if(StringUtil.isEmpty(langContext))
		{
			langContext = MutiLangEntity.mutiLangMap.get("common.notfind.langkey" + "_" + request.getSession().getAttribute("lang"));
			if("null".equals(langContext)||langContext==null ||langKey.startsWith("?")){
				langContext = "";
			}
			langContext = langContext  + langKey;
		}
		return langContext;
	}

	public String getLang(String lanKey, String langArg) {
		String langContext = "";
		if(StringUtil.isEmpty(langArg)){
			langContext = getLang(lanKey);
		}else{
			String[] argArray = langArg.split(",");
			langContext = getLang(lanKey);
			
			for(int i=0; i< argArray.length; i++)
			{
				String langKeyArg = argArray[i].trim();
				String langKeyContext = getLang(langKeyArg);
				langContext = StringUtil.replaceAll(langContext, "{" + i + "}", langKeyContext);
			}
		}
		return langContext;
	}

	/** 刷新多语言cach **/
	public void refleshMutiLangCach() {
		MutiLangEntity.mutiLangMap.clear();
		initAllMutiLang();
	}
	
}
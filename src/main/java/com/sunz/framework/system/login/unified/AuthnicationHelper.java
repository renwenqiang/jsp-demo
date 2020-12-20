package com.sunz.framework.system.login.unified;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.apache.log4j.Logger;
import org.jeecgframework.web.system.pojo.base.TSUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import com.sunz.framework.core.ICacheRefreshable;
import com.sunz.framework.core.util.JdbcTemplateRowMapperDispatcher;
import com.sunz.framework.datatable.IDatatableServiceEx;
import com.sunz.framework.util.EncryptHelper;
import com.sunz.framework.util.RSAEncryptHelper;
import com.sunz.framework.util.StringUtil;

import ognl.Ognl;
import ognl.OgnlException;

/**
 * [统一登录]认证帮助类
 * 
 * @author Xingzhe
 *
 */
@Component
public class AuthnicationHelper implements ICacheRefreshable {
	static Logger logger=Logger.getLogger(AuthnicationHelper.class);
	
	private static NamedParameterJdbcTemplate jdbcTemplate;
	@Autowired
	@Qualifier("namedParameterJdbcTemplate")
	public void setNamedParameterJdbcTemplate(NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
		jdbcTemplate = namedParameterJdbcTemplate;
	}
	private static IDatatableServiceEx datatableService;	
	@Autowired
	public void setDatatableService(IDatatableServiceEx datableService) {
		AuthnicationHelper.datatableService = datableService;
	}
	
	private static Map<String, Setting> domainSettingMap;
	private static Map<String, EncryptHelper.IStringEncrypter> domainEncrypterMapping;

	@PostConstruct
	public void initForUnified() {
		domainSettingMap=new HashMap<String, Setting>();
		domainEncrypterMapping=new HashMap<String, EncryptHelper.IStringEncrypter>();
		try {
			List<Setting> settings=jdbcTemplate.query("select * from t_s_unified", JdbcTemplateRowMapperDispatcher.getRowMapper(Setting.class));
			for(Setting s:settings) {
				domainSettingMap.put(s.getDomain(), s);
			}
		}catch (Exception e) {
			logger.warn("统一认证登录服务初始化失败，本项目中此服务将不可用",e);
		}
	}
	
	/**
	 * 
	 * @param domain
	 * @return
	 */
	public static Setting getDomainSetting(String domain) {
		return domainSettingMap.get(domain);
	}
	
	/**
	 * 
	 * @param setting
	 */
	public static void registerDomain(Setting setting) {
		if(domainSettingMap.containsKey(setting.getDomain())) {
			return;
		}
		
		RSAEncryptHelper.KeyPair keys= RSAEncryptHelper.createKeyPair();
		setting.setPublicKeys(keys.getPublicKey());
		setting.setPrivateKeys(keys.getPrivateKey());
		
		datatableService.add(setting);
		domainSettingMap.put(setting.getDomain(), setting);
	}
	
	/**
	 * 
	 * @param url
	 * @param loginUser
	 * @return
	 */
	public static String getTicket(String url,TSUser loginUser) {
		String[] tmps=url.replaceAll("http[s]?\\://", "").split("/|\\?");
    	String domain=tmps.length>1?(tmps[0]+"/"+tmps[1]):tmps[0];
    	if(!domainSettingMap.containsKey(domain)) {
    		if(domain.equals(tmps[0]))
    			throw new RuntimeException("域【"+domain+"】无权使用统一认证登录服务");
//    		if(logger.isDebugEnabled())
//    			logger.debug("【"+domain+"】无统一认证配置，尝试【"+tmps[0]+"】");
    			
    			
	    	if(domainSettingMap.containsKey(tmps[0])){
	    		domain=tmps[0];
	    	}else {
	    		// 未配置，无权使用
	    		//return null;
	    		throw new RuntimeException("域【"+domain+"】或【"+tmps[0]+"】无权使用统一认证登录服务");
	    	}
    	}
    	
    	Setting setting=domainSettingMap.get(domain);
     	EncryptHelper.IStringEncrypter encrypter=domainEncrypterMapping.get(domain);
     	if(encrypter==null) {
     		EncryptHelper.IEncrypter realEncrypter=null;
			try {
				realEncrypter = RSAEncryptHelper.createPrivateEncrypter(setting.getPrivateKeys());
			} catch (Exception e) {
				throw new RuntimeException("域名/项目【"+domain+"】密钥证书配置不正确，无法生成认证票据");
			}
     		domainEncrypterMapping.put(domain, encrypter=EncryptHelper.toBase64Encrypter(realEncrypter));
     	}
    	
     	String uid;
		try {
			uid = Ognl.getValue(StringUtil.ifEmpty(setting.getUidField(),"id"), loginUser).toString();//loginUser.getId();
		} catch (OgnlException e) {
			throw new RuntimeException("统一认证登录获取用户标识错误",e);
		}
		
		// 据说Cipher加解密时线程不安全
		synchronized (encrypter) {
			return encrypter.encrypt(new Date().getTime()+uid);
		}
	}

	@Override
	public void refresh(String item) {
		initForUnified();
	}

	@Override
	public String getCategory() {
		return "unified";
	}

	@Override
	public String getDescription() {
		return "统一认证（及密钥）配置（因数量级不大，不支持单项刷新）";
	}
		
}

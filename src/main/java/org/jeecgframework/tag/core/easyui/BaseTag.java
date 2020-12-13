package org.jeecgframework.tag.core.easyui;

import com.sunz.framework.tag.ResourceTag;

/**
 * 直接使用ResourceTag
 * --作为兼容t:base标签保留下来
 *
 */
public class BaseTag extends ResourceTag {
	public void setType(String items) {
		this.setItems(items);
	}
	public void setCssTheme(String theme) {
		this.setTheme(theme);
	}
}

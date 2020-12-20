package com.sunz.framework.icon;

import java.util.List;

import com.sunz.framework.core.PageParameter;
/**
 * 图标（ICON）服务接口
 * 		因图标的管理涉及图标文件及css样式的生成，独立出一个服务接口
 * 
 * @author Xingzhe
 *
 */
public interface IIconService {
	
	/**
	 * 获取所有
	 * 
	 * @param page
	 * @return
	 */
	List getAll(PageParameter page);
	
	/**
	 * 获取指定类型的
	 * 
	 * @param category
	 * @param page
	 * @return
	 */
	List getSubs(String category,PageParameter page);
	
	/**
	 * 添加一个图标
	 * 
	 * @param icon
	 */
	void add(Icon icon);
	
	/**
	 * 修改图标
	 * 
	 * @param icon
	 */
	void save(Icon icon);
	
	/**
	 * 指定id或iconClas样式 名进行查找
	 * 
	 * @param id
	 * @param iconClas
	 * @return
	 */
	Icon find(String id,String iconClas);
	
	/**
	 * 删除指定图标
	 * 
	 * @param id
	 */
	void delete(String id);
	
}

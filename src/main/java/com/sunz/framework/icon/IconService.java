package com.sunz.framework.icon;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

import com.sunz.framework.core.ICacheRefreshable;
import com.sunz.framework.core.PageParameter;
import com.sunz.framework.core.WebRootSupport;
import com.sunz.framework.core.util.IPagingQueryService;
import com.sunz.framework.core.util.JdbcTemplateRowMapperDispatcher;

import freemarker.template.Configuration;
import freemarker.template.Template;

@Service
public class IconService extends WebRootSupport implements IIconService, ICacheRefreshable{
	private static final Logger logger = Logger.getLogger(IconService.class);
	
	private static String rootPath="plug-in/easyui1.5/themes/icons/";
	private static String cssFile="plug-in/easyui1.5/themes/icon.css";
	private static String defaultCss=".icon-blank{background:url('icons/blank.gif') no-repeat center center}.icon-add{background:url('icons/edit_add.png') no-repeat center center}.icon-edit{background:url('icons/pencil.png') no-repeat center center}.icon-clear{background:url('icons/clear.png') no-repeat center center}.icon-remove{background:url('icons/edit_remove.png') no-repeat center center}.icon-save{background:url('icons/filesave.png') no-repeat center center}.icon-cut{background:url('icons/cut.png') no-repeat center center}.icon-ok{background:url('icons/ok.png') no-repeat center center}.icon-no{background:url('icons/no.png') no-repeat center center}.icon-cancel{background:url('icons/cancel.png') no-repeat center center}.icon-reload{background:url('icons/reload.png') no-repeat center center}.icon-search{background:url('icons/search.png') no-repeat center center}.icon-print{background:url('icons/print.png') no-repeat center center}.icon-help{background:url('icons/help.png') no-repeat center center}.icon-undo{background:url('icons/undo.png') no-repeat center center}.icon-redo{background:url('icons/redo.png') no-repeat center center}.icon-back{background:url('icons/back.png') no-repeat center center}.icon-sum{background:url('icons/sum.png') no-repeat center center}.icon-tip{background:url('icons/tip.png') no-repeat center center}.icon-filter{background:url('icons/filter.png') no-repeat center center}.icon-man{background:url('icons/man.png') no-repeat center center}.icon-lock{background:url('icons/lock.png') no-repeat center center}.icon-more{background:url('icons/more.png') no-repeat center center}.icon-mini-add{background:url('icons/mini_add.png') no-repeat center center}.icon-mini-edit{background:url('icons/mini_edit.png') no-repeat center center}.icon-mini-refresh{background:url('icons/mini_refresh.png') no-repeat center center}.icon-large-picture{background:url('icons/large_picture.png') no-repeat center center}.icon-large-clipart{background:url('icons/large_clipart.png') no-repeat center center}.icon-large-shapes{background:url('icons/large_shapes.png') no-repeat center center}.icon-large-smartart{background:url('icons/large_smartart.png') no-repeat center center}.icon-large-chart{background:url('icons/large_chart.png') no-repeat center center}.icon-duka{background:url('icons/pics/duka.gif') no-repeat center center}.icon-print{background:url('icons/pics/printer.png') no-repeat center center}.icon-addbtn{background:url('icons/pics/add.png') no-repeat center center}.icon-delbtn{background:url('icons/pics/delete.png') no-repeat center center}.icon-invocate{background:url('icons/pics/diaodang.gif') no-repeat center center}.icon-peoplegroup{background:url('icons/pics/group.png') no-repeat center center}.icon-startjob{background:url('icons/pics/accept.gif') no-repeat center center}.icon-yijian{background:url('icons/pics/registcls.gif') no-repeat center center}\r/*扩展图标开始*/";
	private static String cssTemlate="<#if iconClas?? && iconClas!=''>\n.${iconClas}{background:url('icons/${iconClas}<#if extend!=''>.${extend}</#if>') no-repeat center center}</#if>";
	
	
	IPagingQueryService pagingQueryService;
	@Autowired
	public void setPagingQueryService(IPagingQueryService pagingQueryService) {
		this.pagingQueryService = pagingQueryService;
	}
	
	NamedParameterJdbcTemplate jdbcTemplate;	
	@Autowired
	@Qualifier("namedParameterJdbcTemplate")
	public void setNamedParameterJdbcTemplate(NamedParameterJdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}
	@PostConstruct
	public void init(){
		try {
			freemarkerTemplate=new Template("iconCssTemlate", new StringReader(cssTemlate),new Configuration(),"utf-8");
		} catch (IOException e) {
			logger.error("图标css模板初始化失败，将无法使用图标管理功能", e);
		}		
		try{
			refreshAll(false);
		}catch(Exception exp){
			logger.error("图标刷新失败，可能会影响部分界面[图标显示]"+"，路径："+webRoot, exp);
		}
	}
	Template freemarkerTemplate;
	/*private String toIconCss(Icon icon){
		Writer writer=new StringWriter();
		try {
			freemarkerTemplate.process(icon, writer);
		} catch (Exception e) {
			//logger.error("图标css生成出错", e);
			throw new RuntimeException("图标css生成出错", e);
		}		
		return writer.toString();
	}*/
	
	private void refreshAll(boolean rebuild){
		//StringBuffer sb=new StringBuffer(defaultCss);
		Writer writer=new StringWriter();
		List<Icon> all=(List<Icon>)getAll(new PageParameter());
		try{
			for(Icon icon:all){
				//sb.append(toIconCss(icon));
				if(rebuild){
					String nPath=icon.getSavePath(rootPath);
					if(!nPath.equals(icon.getPath())){
						icon.setPath(nPath);
						saveTodb(icon);
					}
				}
					
				if(icon.getContent()!=null)
					if(rebuild || !new File(icon.getSavePath(webRoot+rootPath)).exists())
						icon.saveToFile(webRoot+rootPath);
				
				if(!icon.isRejectCss())
					freemarkerTemplate.process(icon, writer);
			}
			//FileOutputStream file=new FileOutputStream(cssFile);
			new FileOutputStream(webRoot+cssFile).write((defaultCss+writer.toString()).getBytes("utf-8"));
		}catch(Exception e){
			logger.error("图标刷新出错：", e);
			throw new RuntimeException("图标刷新相关操作出现意外错误："+e.getMessage(), e);
		}
	}
	
	@Override
	public List getAll(PageParameter page) {
		String sql="select * from T_S_ICON";		
		return pagingQueryService.query(sql, null, page, Icon.class).list();
	}


	@Override
	public List getSubs(String category,PageParameter page) {
		String sql="select * from T_S_ICON where CATEGORY=:category ";
		Map<String,String> map=new HashMap<String,String>();
		map.put("category", category);
		return pagingQueryService.query(sql, map, page, Icon.class).list();
	}


	@Override
	public Icon find(String id, String iconClas) {
		String sql="select * from T_S_ICON where ID=:id or ICONCLAS=:iconClas ";
		Map<String,String> map=new HashMap<String,String>();
		map.put("id", id);
		map.put("iconClas", iconClas);
		return (Icon)jdbcTemplate.queryForObject(sql, map, new BeanPropertyRowMapper(Icon.class));
	}

	private Map toParamMap(Icon icon){
		Map<String,Object> m=new HashMap<String,Object>();
		m.put("id", icon.getId());
		m.put("category", icon.getCategory());
		m.put("name", icon.getName());
		m.put("iconClas", icon.getIconClas());
		m.put("content", icon.getContent());
		m.put("path", icon.getPath());
		m.put("extend", icon.getExtend());
		return m;
	}

	@Override
	public void add(Icon icon) {
		icon.setPath(icon.getSavePath(rootPath));
		String sql="insert into T_S_ICON(ID,CATEGORY,NAME,PATH, EXTEND,ICONCLAS,CONTENT) values (:id,:category,:name,:path,:extend,:iconClas,:content)";
		jdbcTemplate.update(sql, toParamMap(icon));
		// 如果图标文件存在，则清掉
		File iconFile=new File(icon.getSavePath(webRoot+rootPath));
		if(iconFile.exists())
			iconFile.delete();
		
		if(icon.isRejectCss())
			icon.saveToFile(webRoot+rootPath);
		else
			refreshAll(false);
	}

	private void saveTodb(Icon icon){
		icon.setPath(icon.getSavePath(rootPath));
		String sql="update T_S_ICON t set CATEGORY=:category, NAME=:name,PATH=:path, EXTEND=:extend ,ICONCLAS=:iconClas";
		if(icon.getContent()!=null){
			//Icon old=find(icon.getId(), null);
			//icon.setContent(old.getContent());
			sql=sql+",CONTENT=:content";
			// 如果图标文件存在，则清掉
			File iconFile=new File(icon.getSavePath(webRoot+rootPath));
			if(iconFile.exists())
				iconFile.delete();
		}
		sql=sql+" where ID=:id";
		jdbcTemplate.update(sql, toParamMap(icon));
	}

	@Override
	public void save(Icon icon) {
		saveTodb(icon);
		if(icon.isRejectCss())
			icon.saveToFile(webRoot+rootPath);
		else
			refreshAll(false);
	}


	@Override
	public void delete(String id) {
		Icon icon=find(id, null);
		
		String sql="delete from T_S_ICON where ID=:id";
		Map<String,String> m=new HashMap<String,String>();
		m.put("id", id);
		jdbcTemplate.update(sql, m);
		
		if(icon.isRejectCss()){
			String path=icon.getSavePath(webRoot+rootPath);
			File file=new File(path);
			if(file.exists()){
				file.delete();
			}
		}
		else
			refreshAll(false);
	}

	
	// ************** CacheRefresh ************* 
	@Override
	public void refresh(String item) {
		if(item!=null){
			try{
				Icon icon= (Icon) jdbcTemplate.getJdbcOperations().queryForObject("select * from T_S_ICON where id=? or iconClas=?",new Object[] { item,item}, JdbcTemplateRowMapperDispatcher.getRowMapper(Icon.class));
				icon.setPath(icon.getSavePath(rootPath));
				save(icon);
				
			}catch(Exception e){
				logger.error("指定图标"+item+"刷新出错",e);
				throw new RuntimeException("指定图标"+item+"刷新出错", e);
			}
		}else
			refreshAll(true);
	}

	@Override
	public String getCategory() {
		return "Icon";
	}

	@Override
	public String getDescription() {
		return "图标及css文件";
	}
	
	
	// *************** Spring  ******************
	private boolean isEmpty(String string){return string==null || string.trim().equals("");}
	@Autowired(required=false)
	@Qualifier("iconSavePath")
	public void setRootPath(String rp){
		if(!isEmpty(rp))
			rootPath=rp;
	}
	@Autowired(required=false)
	@Qualifier("iconDefaultCss")
	public void setDefaultCss(String defCss){
		if(!isEmpty(defCss))
			defaultCss=defCss;
	}
	@Autowired(required=false)
	@Qualifier("iocnCssTemplate")
	public void setCssTemplate(String tpl){
		if(!isEmpty(tpl))
			cssTemlate=tpl;
	}
	@Autowired(required=false)
	@Qualifier("iconCssFileName")
	public void setCssFile(String file){
		if(!isEmpty(file))
			cssFile=file;
	}
}

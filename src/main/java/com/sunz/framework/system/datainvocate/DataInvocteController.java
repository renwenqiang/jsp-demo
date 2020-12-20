package com.sunz.framework.system.datainvocate;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;
import java.util.HashMap;
import java.util.Map;






import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.CallableStatementCallback;
import org.springframework.jdbc.core.CallableStatementCreator;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.sunz.framework.core.BaseController;
import com.sunz.framework.core.JsonResult;
import com.sunz.framework.query.IQueryService;

@Controller
@RequestMapping("framework/datainvocate")
public class DataInvocteController extends BaseController {
	
	
	protected NamedParameterJdbcTemplate jdbcTemplate;	
	@Autowired
	public void setNamedParameterJdbcTemplate(NamedParameterJdbcTemplate template) {
		jdbcTemplate = template;
	}
	
	private JsonResult callProcedure(String proc, String jobid,String dataids){
		final String procCall="{ call "+proc+" (?,?,?,?) }";
		final Map<Integer,Object> mParam=new HashMap<Integer,Object>();
		mParam.put(1, jobid);
		mParam.put(2, getLoginUser().getUserName());
		mParam.put(3, dataids);
		final int outIndex=4;
		try{
			String err=jdbcTemplate.getJdbcOperations().execute( 
			     new CallableStatementCreator() {   
			        public CallableStatement createCallableStatement(Connection con) throws SQLException { 			    		
			           CallableStatement cs = con.prepareCall(procCall);   
			           // 设置输入参数的值   
			           int len= mParam.size();
			           for(int i=0;i<len;i++){
			        	   int paramIndex=i+1;
			        	   cs.setObject(paramIndex, mParam.get(paramIndex));
			           }
			           cs.registerOutParameter(outIndex, Types.VARCHAR);
			           
			           return cs;   
			        }   
			     }, new CallableStatementCallback<String>() {   
			         public String doInCallableStatement(CallableStatement cs) throws SQLException {   
			           cs.execute();   
			           
			           return cs.getString(outIndex);
			     }   
			  }); 
			
			return new JsonResult(err);
		}catch(Exception e){
			logger.debug("调用存储过程时出错",e);
			return new JsonResult("调用过程出错："+e.getMessage());			
		}
	}
	
	@RequestMapping(params= "invocate")
	@ResponseBody
	public JsonResult invocate(
		@RequestParam(required=true)String k,
		@RequestParam(required=true)String jobid,
		@RequestParam(required=true)String dataids){
		if(k==null|| "".equals(k)){
			return new JsonResult("未指定调档定义");
		}
		DataInvocation def=commonService.findUniqueByProperty(DataInvocation.class, "key", k);
		if(def==null)
			return new JsonResult("未找到指定的调档定义");
		
		String proc=def.getInvocateProcedure();
		if(proc==null || "".equals(proc))
			return new JsonResult("内部错误：当前调档没有指定调用逻辑的存储过程"); 
		
		return callProcedure(proc, jobid, dataids);
	}
	
	IQueryService queryService;
	@Autowired
	public void setQueryService(IQueryService queryService) {
		this.queryService = queryService;
	}
	@RequestMapping(params="page")
	public ModelAndView page(
		@RequestParam(required=true)String k,
		@RequestParam(required=true)String jobid){
		
		DataInvocation def=commonService.findUniqueByProperty(DataInvocation.class, "key", k);
		if(def.getQueryKey()==null||"".equals(def.getQueryKey())){
			throw new RuntimeException("内部错误：当前调档定义未绑定查询");
		}
		ModelAndView mv=new ModelAndView("framework/system/datainvocate/invocate");
		mv.addObject("invocationDef", def);		
		mv.addObject("queryDef", queryService.getUserQuery(def.getQueryKey(), getLoginUser()));
		
		return mv;		
	}
	@RequestMapping(params= "uninvocate")
	@ResponseBody
	public JsonResult uninvocate(
			@RequestParam(required=true)String k,
			@RequestParam(required=true)String jobid,
			@RequestParam(required=true)String dataids){		
			if(k==null|| "".equals(k)){
				return new JsonResult("未指定调档定义");
			}
			DataInvocation def=commonService.findUniqueByProperty(DataInvocation.class, "key", k);
			if(def==null)
				return new JsonResult("未找到指定的调档定义");
			
			String proc=def.getUninvocateProcedure();
			if(proc==null || "".equals(proc))
				return new JsonResult("内部错误：当前调档没有指定调用逻辑的存储过程"); 
			
			return callProcedure(proc, jobid, dataids);
	}
	
	@RequestMapping(params="unpage")
	public ModelAndView unpage(
			@RequestParam(required=true)String k,
			@RequestParam(required=true)String jobid){
			
			DataInvocation def=commonService.findUniqueByProperty(DataInvocation.class, "key", k);
			if(def.getUninvocateQueryKey()==null||"".equals(def.getUninvocateQueryKey())){
				throw new RuntimeException("内部错误：当前调档定义未绑定查询");
			}
			ModelAndView mv=new ModelAndView("framework/system/datainvocate/uninvocate");
			mv.addObject("invocationDef", def);
			mv.addObject("queryDef", queryService.getUserQuery(def.getUninvocateQueryKey(), getLoginUser()));
			
			return mv;		
	}
	
	@RequestMapping(params="setting")
	public String invocateSetting(){
		return "framework/system/datainvocate/setting";
	}
}

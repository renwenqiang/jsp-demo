package com.sunz.framework.export.excel.helper;

import java.util.Collection;
import java.util.Iterator;

/**
 * 【步进式】数据读取器，区别于Collection，目的是防止一次读取过多数据，解决内存问题
 * 
 * @author Xingzhe
 *
 * @param <T>
 */
public interface IGradualReader<T> {
	
	/**
	 * 读取【下一个数据】
	 * 
	 * @return
	 */
	T read();
	
	/**
	 * 是否还有更多数据
	 * 
	 * @return
	 */
	boolean hasMore();
	
	static <T> IGradualReader<T> getCollectionReader(Collection<T> datas){
		final Iterator<T> iter=datas.iterator();
		return new IGradualReader<T>() {
			@Override
			public T read() {
				return iter.next();
			}
			@Override
			public boolean hasMore() {
				return iter.hasNext();
			}
		};
	}
	static <T> IGradualReader<T> getArrayReader(T[] datas){
		return new IGradualReader<T>() {
			int total=datas.length;
			int index=0;
			@Override
			public T read() {
				return index>=total?null:datas[index++];
			}

			@Override
			public boolean hasMore() {
				return index<total;
			}
		};
	}
	/*
	static IGradualReader<Object> getQueryReader(String sqlid,String sqlkey,Map paramMap,PageParameter page,IQueryService queryService){
		int bufferCount=100;		
		int count=page.getTotal();
		int limit=page.getRealLimit();
		if(count<0) {
			if(page.getLimit()>0) {
				count=page.getRealStart()+page.getLimit();
			}else {
				count=Integer.MAX_VALUE;// 可以修正 //queryService.queryCount(sqlid, sqlkey, paramMap);
			}
		}
		page.setTotal(count);
		page.setLimit(bufferCount>limit?limit:bufferCount);
		return new IGradualReader<Object>() {
			int total=page.getTotal();
			int index=0;		// 总体计数
			int pageIndex=0;	// 单页计数
			List<Object> list;	// 单页结果
			@Override
			public Object read() {
				if(!hasMore())
					return null;
				
				if(list==null||pageIndex>=list.size()) {
					ListJsonResult jResult = queryService.queryList(sqlid,sqlkey,paramMap,page);
					if (!jResult.isSuccess()) {
						throw new RuntimeException(jResult.getMsg());
					}
					list=jResult.list();
					if(list.size()<page.getLimit()) {	// 对total进行修正，以免传了错误值
						total=index+list.size();
					}
					page.setStart(page.getRealStart()+page.getLimit());
					pageIndex=0;
				}
				
				index++;
				return list.get(pageIndex++);
			}

			@Override
			public boolean hasMore() {
				return index<total;
			}
		};		
	}
	
	static  class JdbcRowReader implements IGradualReader<Object>{
		ResultSet resultSet;
		int rowNum=0;
		boolean hasNext=false;
		
		public JdbcRowReader(ResultSet rs){
			resultSet=rs;
			try {
				tryNext();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		private void tryNext() throws SQLException {
			hasNext=resultSet.next();
			if(!hasNext) {
				resultSet.close();
			}
		}
		RowMapper<Map<String, Object>> rowMapper=new ColumnMapRowMapper();
		@Override
		public Object read() {
			try {
				if(hasMore()) {	// 读当前值，并判断next
					Map<String, Object> map = rowMapper.mapRow(resultSet,rowNum++);
					tryNext();
					return map;
				}
				return null;
			} catch (SQLException e) {
				return null;
			}
		}

		@Override
		public boolean hasMore() {
			return hasNext;
		}
	}*/
}

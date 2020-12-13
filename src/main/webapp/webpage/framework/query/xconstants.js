/**
 * 
 */
var XConstants={
		TITLE_RESOURCES:"额外资源",
		TITLE_DOUBLEHANDLER:"双击处理函数",
		TITLE_GRIDSETTING:"grid的额外设置",
		TITLE_SETTING:"其它设置",
		
		DOC_RESOURCES:"HTML片断，将在JQuery等基础资源引入之后、配置信息之前直接输出\r\n本配置中的内容会原封不动输出到页面[head]中，因此内容必须为一个或多个完整的html标签",
		DOC_DOUBLEHANDLER:"JS函数，形如function(r,i,g){}，参数分别为：操作行数据、操作行Index、datagrid对象；",
		DOC_GRIDSETTING:"结果组件（datagrid）的额外设置，具体可参考easyui帮助文档\r\n本设置将拥有高优先级，可覆盖有明确界面中的设置",
		DOC_SETTING:`其它设置（为方便扩展不单独提供参数输入框），可选值如下：
	searchTitle			条件面版标题（默认“查询条件”）
	resultTitle			结果面版标题（默认“查询结果”，查询后变为“共查询到xx条相关结果”，要覆盖此表现，需要给datagrid的其它设置中配置onLoadSuccess）
	searchFieldShowAll	是否显示全部条件，并将查询、重置按钮显示在最后（否则在多行条件时将隐藏第一行以外的条件，并显示“更多”按钮)
	searchColumns		条件列数，默认可在数据库中（Config）配置，可取值为数组（[第一行条件/列数，第二行条件/列数，第三行条件/列数...空值将采用Config中的配置]）或数字（所有行使用相同的条件/列数)
	searchHeight		条件面版高度，标题栏高度另算（自动计算效果不佳时可手动配置此值）
	searchRowStyles		条件行（div）的css样式添加，可为数组（不同行使用不同样式）或单个css字符串（所有行使用同一样式）；比如["min-width:1280px"]
	
	hideSearch			隐藏条件面版
	hideReset			隐藏“重置”按钮
	searchText			“搜索”按钮文本，默认“搜索 ”
	resetText			“重置”按钮文本，默认“重置 ”
	searchWidth			条件面版宽度（条件面版和结果面版左右布局排列时生效，默认240）
	
	当条件面版高度计算不准确时（easyui切换风格或使用特殊控件时)，可调整数据库Config配置（
			defaultSearchColumns 		默认（整个项目）查询条件列数，默认3
			easyuiPanelHead				默认（整个项目）easyui标题栏高度，默认39 （easyui1.5，sunzbase风格)
			easyuiInputExpandHeight 	默认（整个项目）easyui将原生input变为控件后高度差，默认2（easyui1.5，sunzbase风格)
	）或手动指定searchHeight
		`,
	DEF_RESOURCES:"",
	DEF_DOUBLEHANDLER:`function(r,i,g){

}`,
	DEF_GRIDSETTING:`{

}`,
	DEF_SETTING:`{

}`,
}
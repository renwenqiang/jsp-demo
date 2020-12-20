function viewRecord(memberId){
    top.createwindow("交费记录","framework/query.do?goQuery&key=p_dues_pay_list&member_id="+memberId,782,650)
}
function update(memberId,partyId){
    top.createwindow("交费","framework/query.do?goQuery&key=p_dues_pay_batch_month"+"&relationField=member_id_"+"&member_id="+memberId,800,600,function(frm){
        var infoArray = JSON.stringify(frm.grid.getSelections());
        if(infoArray == "[]"){
            alert('请选择要交纳的月份');
            return false;
        }
        $.post('duesPay/pay.do',{duesPays:infoArray},function(jr){
            grid.reload();
        })

    });

}
window.monthConfig={
    width:240,
    onShowPanel : function() {// 显示日趋选择对象后再触发弹出月份层的事件，初始化时没有生成月份层
        var jme=$(this), p = jme.datebox('panel'), // 日期选择对象
            tds = false, // 日期选择对象中月份
            span = p.find('span.calendar-text'); // 显示月份层的触发控件

        span.trigger('click'); // 触发click事件弹出月份层
        if (!tds)
            setTimeout(function() {// 延时触发获取月份对象，因为上面的事件触发和对象生成有时间间隔
                tds = p.find('div.calendar-menu-month-inner td');
                tds.click(function(e) {
                    e.stopPropagation(); // 禁止冒泡执行easyui给月份绑定的事件
                    var year = /\d{4}/.exec(span.html())[0]// 得到年份
                        , month = parseInt($(this).attr('abbr'), 10) + 1; // 月份
                    jme.datebox('hidePanel')// 隐藏日期对象
                        .datebox('setValue', year + '-' + month); // 设置日期的值
                });
            }, 0);
    },
    parser : function(s) {// 配置parser，返回选择的日期
        if (!s)
            return new Date();
        var arr = s.split('-');
        return new Date(parseInt(arr[0], 10), parseInt(arr[1], 10) - 1, 1);
    },
    formatter : function(d) {
        var month = d.getMonth();
        if(month<10){
            month = "0"+month;
        }
        if (d.getMonth() == 0) {
            return d.getFullYear()-1 + '-' + 12;
        } else {
            return d.getFullYear() + '-' + month;
        }
    }// 配置formatter，只返回年月
}

var loadFilter=function(jr){
    var now=new Date(),y=now.getFullYear(),s=window.startTime||(y+"-01"),e=window.endTime||(y+"-"+(now.getMonth()+1)),
        startY=Number.parseInt(s.substr(0,4)),startM=Number.parseInt(s.substr(5)),
        endY=Number.parseInt(e.substr(0,4)),endM=Number.parseInt(e.substr(5)),
        count=endM-startM+12*(endY-startY)+1;

    $.each(jr.data,function(i,d){
        d.dues=d.dues||'-';
        var total=0,months=0,dict={};
        if(d.details){
            var arr=d.details.split(/;/g);
            months=arr.length;
            $.each(arr,function(i,pair){var a=pair.split("=");total+=Number.parseFloat(a[1]||0); dict[a[0]]=a[1] });
        }
        d.months=months;
        d.unmonths=count-months;
        d.totalamount=total;
        var ds=[],y=startY,m=startM;
        for(var i=0;i<count;i++){
            ds.push({y:y,m:m,amount:dict[y+"-"+(m>=10?"":"0")+m]});
            m+= 1;
            if(m>12){
                y+=1;
                m=1;
            }
        }
        d.monthdetail=ds;
    })
    return $.fn.datagrid.defaults.loadFilter(jr);
},detalFormatter=function(v,r,i){
    var html='';
    var y=-1;
    $.each(r.monthdetail,function(i,d){
        if(d.y!=y){
            y=d.y;
            html +='<span class="year">('+y+')</span>';
        }
        html +='<span class="month '+(d.amount?"payed":"unpayed")+'" title="'+(d.amount||"本月尚未交纳")+'">'+d.m+'</span>';
    })
    return html;
};
$(function(){
    onBeforeSearch.add(function(param){
        window.startTime=param.startTime;
        window.endTime=param.endTime;
        if(!window.jtotal) window.jtotal=$('<td><span class="sum">党费交纳总金额：<span class="amount">-</span></span></td>').appendTo(".datagrid-toolbar tr").find(".amount");
        jtotal.text("正在计算···");
        $.post("framework/query.do?object&k=p_dues_stat_total",param,function(jr){
            jtotal.text(jr.success?(jr.data.total||0):"计算出错");
        });
    });
})
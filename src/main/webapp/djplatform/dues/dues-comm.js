function viewRecord(memberId) {
    top.createwindow("交费记录", "framework/query.do?goQuery&key=p_dues_pay_list&member_id=" + memberId, 782, 650)
}

function update(memberId, partyId) {
    top.createwindow("交费", "framework/query.do?goQuery&key=p_dues_pay_batch_month" + "&relationField=member_id_" + "&member_id=" + memberId, 800, 600, function (frm) {
        var infoArray = JSON.stringify(frm.grid.getSelections());
        if (infoArray == "[]") {
            alert('请选择要交纳的月份');
            return false;
        }
        $.post('duesPay/pay.do', {duesPays: infoArray}, function (jr) {
            grid.reload();
        })

    });

}

window.monthConfig = {
    width: 240,
    onShowPanel: function () {// 显示日趋选择对象后再触发弹出月份层的事件，初始化时没有生成月份层
        var jme = $(this), p = jme.datebox('panel'), // 日期选择对象
            tds = false, // 日期选择对象中月份
            span = p.find('span.calendar-text'); // 显示月份层的触发控件

        span.trigger('click'); // 触发click事件弹出月份层
        if (!tds)
            setTimeout(function () {// 延时触发获取月份对象，因为上面的事件触发和对象生成有时间间隔
                tds = p.find('div.calendar-menu-month-inner td');
                tds.click(function (e) {
                    e.stopPropagation(); // 禁止冒泡执行easyui给月份绑定的事件
                    var year = /\d{4}/.exec(span.html())[0]// 得到年份
                        , month = parseInt($(this).attr('abbr'), 10) + 1; // 月份
                    jme.datebox('hidePanel')// 隐藏日期对象
                        .datebox('setValue', year + '-' + month); // 设置日期的值
                });
            }, 0);
    },
    parser: function (s) {// 配置parser，返回选择的日期
        if (!s)
            return new Date();
        var arr = s.split('-');
        return new Date(parseInt(arr[0], 10), parseInt(arr[1], 10) - 1, 1);
    },
    formatter: function (d) {
        var month = d.getMonth();
        if (month < 10) {
            month = "0" + month;
        }
        if (d.getMonth() == 0) {
            return d.getFullYear() - 1 + '-' + 12;
        } else {
            return d.getFullYear() + '-' + month;
        }
    }// 配置formatter，只返回年月
}

var loadFilter = function (jr) {

    //获取当前时间
    const date = new Date();
    const year = date.getFullYear();
    let month = date.getMonth() + 1;
    if (month < 10) {
        month = "0" + month;
    }
    const nowDate = year + "-" + month;
    const initArr = jr.data;
    for (let k = 0; k<initArr.length; k++) {
        let outT = initArr[k];
        let yj = 0, wj = 0, ht = '';
        if (outT.details) {
            const dts = outT.details.split(/;/g);
            const setTmp = new Set();
            $.each(dts, function (a, b) {
                const c = b.split(/:/g);
                setTmp.add(c[0]);
            });
            for (let itemA of setTmp) {
                ht += '<span class="year">(' + itemA + ')</span>';
                for (let z = 0; z<dts.length; z++) {
                    const inArr = dts[z].split(/:/g);
                    if (itemA == inArr[0]) {
                        // 1 已交  2 已过未交  3 未交
                        if (nowDate > inArr[2]) {// 本月前
                            if (inArr[3] == -1) { // 2
                                wj++;
                                ht += '<span class="month unpayedafter" title="本月尚未交纳">' + inArr[1] + '</span>';
                            } else {// 1
                                yj++;
                                ht += '<span class="month payed" title="' + inArr[3] + '">' + inArr[1] + '</span>';
                            }
                        } else {// 本月及后
                            if (inArr[3] == -1) { // 3
                                wj++;
                                ht += '<span class="month unpayed" title="本月尚未交纳">' + inArr[1] + '</span>';
                            } else {// 1
                                yj++;
                                ht += '<span class="month payed" title="' + inArr[3] + '">' + inArr[1] + '</span>';
                            }
                        }
                    }
                }
                if (setTmp.size > 2) {
                    ht += '<br></br>';
                }
            }
        }
        outT.detail = ht;
        outT.unmonths = wj || '-';
        outT.months = yj || '-';
        outT.dues = outT.dues || '-';
        outT.totalamount = outT.totalamount || '-';
    }
    return $.fn.datagrid.defaults.loadFilter(jr);
};
$(function () {
    onBeforeSearch.add(function (param) {
        window.startTime = param.startTime;
        window.endTime = param.endTime;
        if (!window.jtotal) window.jtotal = $('<td><span class="sum">党费交纳总金额：<span class="amount">-</span></span></td>').appendTo(".datagrid-toolbar tr").find(".amount");
        jtotal.text("正在计算···");
        $.post("framework/query.do?object&k=p_dues_stat_total", param, function (jr) {
            jtotal.text(jr.success ? (jr.data.total || 0) : "计算出错");
        });
    });
})
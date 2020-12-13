/**
 * 添加tab页到当前tab组
 * @param title
 * @param url
 */
function addTab(title, url){
	var index = $('.pf-sider:visible').attr('arrindex');
	$('.easyui-tabs1[arrindex='+ index +']').tabs('add',{
		title: title,
		content: '<iframe class="page-iframe" src="'+ url +'" frameborder="no" border="no" height="100%" width="100%" scrolling="auto"></iframe>',
		closable: true
	});
}
var mainPlatform = {
	SystemMenus:null,
	iconMap:null,
	getIcon:function(m){
		return this.iconMap[m.icon]||{};
	},
	createIconCss:function(){
		$('<style type="text/css">'
		+$.map(this.iconMap,function(icon){
			return "."+icon.name+"{background-image:url("+icon.path+")}";
		}).join("\r\n")
		+'</style>').appendTo(document.head);
	},
	init: function(rootMenus,iconMapping){
		this.SystemMenus=rootMenus;
		this.iconMap=iconMapping;
		this.createIconCss();
		this.bindEvent();
		this._createTopMenu();
		this.bindNav();
		this._clickStartMenu();
	},
	bindNav:function() {
		$(window).resize(function() {
			$('.tabs-panels').height($("#pf-page").height() - 46);
			$('.panel-body').height($("#pf-page").height() - 76)
		}).resize();

		var page = 0,
			pages = ($('.pf-nav').height() / 70) - 1;
		if (pages === 0) {
			$('.pf-nav-prev,.pf-nav-next').hide();
		}
		$('.pf-nav-prev,.pf-nav-next').on('click',function() {
			if ($(this).hasClass('disabled')) return;
			if ($(this).hasClass('pf-nav-next')) {
				page++;
				$('.pf-nav').stop().animate({
					'margin-top' : -70 * page
				}, 200);
				if (page == pages) {
					$(this).addClass('disabled');
					$('.pf-nav-prev').removeClass('disabled');
				} else {
					$('.pf-nav-prev').removeClass('disabled');
				}
			} else {
				page--;
				$('.pf-nav').stop().animate({
					'margin-top' : -70 * page
				}, 200);
				if (page == 0) {
					$(this).addClass('disabled');
					$('.pf-nav-next').removeClass('disabled');
				} else {
					$('.pf-nav-next').removeClass('disabled');
				}
			}
		})
	},
	bindEvent: function(){
		var self = this;
		// 顶部大菜单单击事件
		$(document).on('click', '.pf-nav-item', function() {
            $('.pf-nav-item').removeClass('current');
            $(this).addClass('current');
            // 渲染对应侧边菜单
            var m = $(this).data('sort');
            self._createSiderMenu(self.SystemMenus[m], m);
        });

        $(document).on('click','.sider-nav .pf-menu-title',function() {
        	//$(this).closest('.pf-sider').find('.sider-nav li').removeClass('current');
        	if($(this).closest('li').hasClass('current')){
        		$(this).closest('li').removeClass('current')
        	}else if(!$(this).closest('li').hasClass('current')){
        		if($(this).closest('.no-child').length > 0){
        			$(this).closest('.pf-sider').find('.sider-nav li').removeClass('current');
        			$(this).closest('li').addClass('current');
        		}else{
            		$(this).closest('li').addClass('current');
        		}
        	}
            // if is no-child
            if($(this).closest('.no-child').length > 0) {
            	var index = $(this).closest('.pf-sider').attr('arrindex');
	        	if($('.easyui-tabs1[arrindex='+ index +']').tabs('exists', $(this).find('.sider-nav-title').text())){
	        		$('.easyui-tabs1[arrindex='+ index +']').tabs('select', $(this).find('.sider-nav-title').text())
	        		return false;
	        	}
	        	$('.easyui-tabs1[arrindex='+ index +']').tabs('add',{
					title: $(this).find('.sider-nav-title').text(),
					content: '<iframe class="page-iframe" src="'+ $(this).closest('.no-child').data('href') +'" frameborder="no" border="no" height="100%" width="100%" scrolling="auto"></iframe>',
					closable: true
				});
	        	
            }
            //$('iframe').attr('src', $(this).data('src'));
        });

        /*$(document).on('click', '.pf-logout', function() {
            layer.confirm('您确定要退出吗？', {
              icon: 4,
			  title: '确定退出' //按钮
			}, function(){
			  location.href= 'login.html'; 
			});
        });*/
        $(document).on('click','.sider-nav-s li',function(e){
        	var index = $(this).closest('.pf-sider').attr('arrindex');
        	$(this).closest('.pf-sider').find('.active').removeClass('active');
        	$(this).addClass('active');
        	if($('.easyui-tabs1[arrindex='+ index +']').tabs('exists', $(this).text())){
        		$('.easyui-tabs1[arrindex='+ index +']').tabs('select', $(this).text())
        		return false;
        	}
        	$('.easyui-tabs1[arrindex='+ index +']').tabs('add',{
				title: $(this).text(),
				content: '<iframe class="page-iframe" src="'+ $(this).data('href') +'" frameborder="no" border="no" height="100%" width="100%" scrolling="auto"></iframe>',
				closable: true
			});
        });
        //左侧菜单收起
        $(document).on('click','.toggle-icon',function() {
            $(this).closest("#pf-bd").toggleClass("toggle");
            $(window).resize();
        });

         //关闭当前
	     $('#mm-tabclose').click(function(){
	         var currtab_title = $('#mm').data("currtab");
	         $(".easyui-tabs1:visible").tabs('close',currtab_title);
	     })
	     //全部关闭
	     $('#mm-tabcloseall').click(function(){
	         $(".easyui-tabs1:visible").find('.tabs li').each(function(i,n){
	             $(".easyui-tabs1:visible").tabs('close', $(n).text());
	         });    
	     });
	     //关闭除当前之外的TAB
	     $('#mm-tabcloseother').click(function(){
	         var currtab_title = $('#mm').data("currtab");
	         $('.tabs-inner span').each(function(i,n){
	             if($(n).text() !== currtab_title)
	                 $(".easyui-tabs1:visible").tabs('close',$(n).text());
	         });    
	     });
	},
	// renderTopMenu
	_createTopMenu: function(){
		var menuStr = '',
			currentIndex = 0,
			ms=this.SystemMenus;
		for(var i = 0, len = ms.length; i < len; i++) {
			var m=ms[i];
			menuStr += '<li class="pf-nav-item project" id="system_menu_'+m.title+'" data-sort="'+ i +'" data-menu="system_menu_" + i>'+
                      '<a href="javascript:;">'+
                          '<span class="iconfont '+ this.getIcon(m).name +'"></span>'+
                          '<span class="pf-nav-title">'+ m.title +'</span>'+
                      '</a>'+
                  '</li>';
            // 渲染当前
            if (m.isCurrent){
            	currentIndex = i;
            	this._createSiderMenu(m, i);
            }
		}

		$('.pf-nav').html(menuStr);
		$('.pf-nav-item').eq(currentIndex).addClass('current');
	},
	_createSiderMenu: function(menu, index){
		$('.pf-sider').hide();
		this._createPageContainer(index);
		if($('.pf-sider[arrindex='+ index +']').length > 0) {
			$('.pf-sider[arrindex='+ index +']').show();
			return false;
		};
		var menuStr = '<h2 class="pf-model-name">'+
                    '<span class="iconfont '+ this.getIcon(menu).name +'"></span>'+
                    '<span class="pf-name">'+ menu.title +'</span>'+
                    '<span class="toggle-icon"></span>'+
                '</h2><ul class="sider-nav">';

        for(var i = 0, len = menu.children.length; i < len; i++){
        	var m = menu.children[i],
        		mstr = '',
        		str = '';
        	if(m.isCurrent){
        		if(m.children && m.children.length > 0) {
        			str = '<li class="current" id="'+m.title+'">';
        		}else{
        			str = '<li class="current no-child" id="'+m.title+'" data-href="'+ m.href +'">';
        		}
        	}else{
        		if(m.children && m.children.length > 0) {
        			str = '<li id="'+m.title+'">';
        		}else{
        			str = '<li class="no-child" id="'+m.title+'" data-href="'+ m.href +'">';
        		}
        	}
           str += '<a href="javascript:;" class="pf-menu-title">'+
                '<span class="iconfont sider-nav-icon '+ this.getIcon(m).name +'"></span>'+
                '<span class="sider-nav-title">'+ m.title +'</span>'+
                '<i class="iconfont">&#xe642;</i>'+
            '</a>'+
            '<ul class="sider-nav-s">';
            var childStr = '';
            xiaojun = m;
            var zz;
            if(m.children){
            	zz = m.children.length;
            }else{
            	zz = 0;
            }
            for(var j = 0, jlen = zz; j < jlen; j++){
            	var child = m.children[j];
            	if(child.isCurrent){
            		childStr += '<li class="active" text="'+ child.title +'" data-href="' + child.href + '"><a href="javascript:void(0);">'+ child.title +'</a></li>';
            		$('.easyui-tabs1[arrindex='+ index +']').tabs('add',{
						title: child.title,
						content: '<iframe class="page-iframe" src="'+ child.href +'" frameborder="no" border="no" height="100%" width="100%" scrolling="auto"></iframe>',
						closable: true
					});
            	}else {
            		childStr += '<li text="'+ child.title +'" data-href="' + child.href + '"><a href="javascript:void(0);">'+ child.title +'</a></li>';
            	}
            }
            mstr = str + childStr + '</ul></li>';
            menuStr += mstr;
        }
        $('.pf-sider-wrap').append($('<div class="pf-sider" arrindex="'+ index +'"></div>').html(menuStr + '</ul>'));
        if(!window.itemClicked){
	        var title_ = $(".pf-sider-wrap .pf-sider:last ul li:first a");
	        if(title_.closest('.no-child').length > 0) {
	        	title_.click();
	        }else{
	        	$('.pf-sider-wrap .pf-sider:visible').find('.sider-nav-s li').removeClass('active');
	        	$('.pf-sider-wrap .pf-sider:visible').find('.sider-nav-s li:first').addClass('active').click();
	        }
	        $(".pf-sider-wrap .pf-sider:last ul li:first").addClass('current'); 
	        window.itemClicked=true;
        }
	},
	_createPageContainer: function(index){
		$('.easyui-tabs1').hide();
		if($('.easyui-tabs1[arrindex='+ index +']').length > 0){
			$('.easyui-tabs1[arrindex='+ index +']').show();
			return false;
		}
		var $tabs = $('<div class="easyui-tabs1" arrindex="'+ index +'" style="width:100%;height:100%;">');
		$('#pf-page').append($tabs);
		$tabs.tabs({
	      tabHeight: 34,
	      onSelect:function(title, index){
	    	  //debugger
	    	 var tabs = parent.$(".easyui-tabs1:visible").find('.tabs li');
	    	 var tabLength = tabs.length;
	    	 var tabFirst = tabs[0];
	    	 if(tabLength==11){
	    		 $(".easyui-tabs1:visible").tabs('close',$(tabFirst).text());
	    	 }
	    	  
	        var currentTab = $tabs.tabs("getSelected");
	        if(currentTab.find("iframe") && currentTab.find("iframe").length){
	        	//需要重复加载吗 ？
	           // currentTab.find("iframe").attr("src",currentTab.find("iframe").attr("src"));
	        }
	        $('.pf-sider:visible').find('.sider-nav-s li').removeClass('active');
	        $('.pf-sider:visible').find('.sider-nav-s li[text='+ title +']').addClass('active');
	      }
	    });

	    $tabs.find('.tabs-header').on('contextmenu', function(e){
	    	e.preventDefault();
	    	if($(e.target).closest('li').length > 0 || $(e.target).is('li')){
	    		$('#mm').menu('show', {
		             left: e.pageX,
		             top: e.pageY,
		         });
	    		var subtitle = $(e.target).closest('li').length ? $(e.target).closest('li') : $(e.target);
        		$('#mm').data("currtab",subtitle.text());
	    	}
	    })
	},	
	_clickStartMenu:function(){
		$('.pf-nav-item:first').click();
		$('.pf-menu-title:first').click();
		$('.pf-menu-title:first').parent().addClass("current");
	}
};
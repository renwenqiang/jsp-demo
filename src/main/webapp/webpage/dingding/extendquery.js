/**  
 * 1����չjquery easyui tree�Ľڵ����������ʹ�÷������£�  
 * $("#treeId").tree("search", searchText);    
 * ���У�treeIdΪeasyui tree�ĸ�ULԪ�ص�ID��searchTextΪ�������ı���  
 * ���searchTextΪ�ջ�""�����ָ�չʾ���нڵ�Ϊ����״̬  
 */  
(function($) {    
    $.extend($.fn.tree.methods, {  
        /**  
         * ��չeasyui tree����������  
         * @param tree easyui tree�ĸ�DOM�ڵ�(UL�ڵ�)��jQuery����  
         * @param searchText �������ı�  
         * @param this-context easyui tree��tree����  
         */  
        search: function(jqTree, searchText) {  
            //easyui tree��tree���󡣿���ͨ��tree.methodName(jqTree)��ʽ����easyui tree�ķ���  
            var tree = this;  
               
            //��ȡ���е����ڵ�  
            var nodeList = getAllNodes(jqTree, tree);  
               
            //���û��������������չʾ�������ڵ�  
            searchText = $.trim(searchText);  
            if (searchText == "") {  
                for (var i=0; i<nodeList.length; i++) {  
                    $(".tree-node-targeted", nodeList[i].target).removeClass("tree-node-targeted");  
                    $(nodeList[i].target).show();  
                }  
                //չ����ѡ��Ľڵ㣨���֮ǰѡ���ˣ�  
                var selectedNode = tree.getSelected(jqTree);  
                if (selectedNode) {  
                    tree.expandTo(jqTree, selectedNode.target);  
                }  
                return;  
            }  
               
            //����ƥ��Ľڵ㲢������ʾ  
            var matchedNodeList = [];  
            if (nodeList && nodeList.length>0) {  
                var node = null;  
                for (var i=0; i<nodeList.length; i++) {  
                    node = nodeList[i];  
                    if (isMatch(searchText, node.text)) {  
                        matchedNodeList.push(node);  
                    }  
                }  
                   
                //�������нڵ�  
                for (var i=0; i<nodeList.length; i++) {  
                    $(".tree-node-targeted", nodeList[i].target).removeClass("tree-node-targeted");  
                    $(nodeList[i].target).hide();  
                }             
                   
                //�۵����нڵ�  
                tree.collapseAll(jqTree);  
                   
                //չʾ����ƥ��Ľڵ��Լ����ڵ�              
                for (var i=0; i<matchedNodeList.length; i++) {  
                    showMatchedNode(jqTree, tree, matchedNodeList[i]);  
                }  
            }      
        },  
           
        /**  
         * չʾ�ڵ���ӽڵ㣨�ӽڵ��п����������Ĺ����б������ˣ�  
         * @param node easyui tree�ڵ�  
         */  
        showChildren: function(jqTree, node) {  
            //easyui tree��tree���󡣿���ͨ��tree.methodName(jqTree)��ʽ����easyui tree�ķ���  
            var tree = this;  
               
            //չʾ�ӽڵ�  
            if (!tree.isLeaf(jqTree, node.target)) {  
                var children = tree.getChildren(jqTree, node.target);  
                if (children && children.length>0) {  
                    for (var i=0; i<children.length; i++) {  
                        if ($(children[i].target).is(":hidden")) {  
                            $(children[i].target).show();  
                        }  
                    }  
                }  
            }     
        },  
           
        /**  
         * ��������������ָ���Ľڵ�λ�ã�ʹ�ýڵ�ɼ�������й������Ź�����û�й������Ͳ�������  
         * @param param {  
         *    treeContainer: easyui tree�������������ڹ��������������������Ϊnull����ȡeasyui tree�ĸ�UL�ڵ�ĸ��ڵ㡣  
         *    targetNode:  ��Ҫ��������easyui tree�ڵ㡣���targetNodeΪ�գ���Ĭ�Ϲ�������ǰ��ѡ�еĽڵ㣬���û��ѡ�еĽڵ㣬�򲻹���  
         * }   
         */  
        scrollTo: function(jqTree, param) {  
            //easyui tree��tree���󡣿���ͨ��tree.methodName(jqTree)��ʽ����easyui tree�ķ���  
            var tree = this;  
               
            //���nodeΪ�գ����ȡ��ǰѡ�е�node  
            var targetNode = param && param.targetNode ? param.targetNode : tree.getSelected(jqTree);  
               
            if (targetNode != null) {  
                //�жϽڵ��Ƿ��ڿ�������                 
                var root = tree.getRoot(jqTree);  
                var $targetNode = $(targetNode.target);  
                var container = param && param.treeContainer ? param.treeContainer : jqTree.parent();  
                var containerH = container.height();  
                var nodeOffsetHeight = $targetNode.offset().top - container.offset().top;  
                if (nodeOffsetHeight > (containerH - 30)) {  
                    var scrollHeight = container.scrollTop() + nodeOffsetHeight - containerH + 30;  
                    container.scrollTop(scrollHeight);  
                }                             
            }  
        }  
    });  
       
       
       
       
    /**  
     * չʾ����ƥ��Ľڵ�  
     */  
    function showMatchedNode(jqTree, tree, node) {  
        //չʾ���и��ڵ�  
        $(node.target).show();  
        $(".tree-title", node.target).addClass("tree-node-targeted");  
        var pNode = node;  
        while ((pNode = tree.getParent(jqTree, pNode.target))) {  
            $(pNode.target).show();               
        }  
        //չ�����ýڵ�  
        tree.expandTo(jqTree, node.target);  
        //����Ƿ�Ҷ�ӽڵ㣬���۵��ýڵ�������ӽڵ�  
        if (!tree.isLeaf(jqTree, node.target)) {  
            tree.collapse(jqTree, node.target);  
        }  
    }      
       
    /**  
     * �ж�searchText�Ƿ���targetTextƥ��  
     * @param searchText �������ı�  
     * @param targetText Ŀ���ı�  
     * @return true-�������ı���Ŀ���ı�ƥ�䣻����Ϊfalse.  
     */  
    function isMatch(searchText, targetText) {  
        return $.trim(targetText)!="" && targetText.indexOf(searchText)!=-1;  
    }  
       
    /**  
     * ��ȡeasyui tree������node�ڵ�  
     */  
    function getAllNodes(jqTree, tree) {  
        var allNodeList = jqTree.data("allNodeList");  
        if (!allNodeList) {  
            var roots = tree.getRoots(jqTree);  
            allNodeList = getChildNodeList(jqTree, tree, roots);  
            jqTree.data("allNodeList", allNodeList);  
        }  
        return allNodeList;  
    }  
       
    /**  
     * �����ȡeasyui tree���ӽڵ�ĵݹ��㷨  
     */  
    function getChildNodeList(jqTree, tree, nodes) {  
        var childNodeList = [];  
        if (nodes && nodes.length>0) {             
            var node = null;  
            for (var i=0; i<nodes.length; i++) {  
                node = nodes[i];  
                childNodeList.push(node);  
                if (!tree.isLeaf(jqTree, node.target)) {  
                    var children = tree.getChildren(jqTree, node.target);  
                    childNodeList = childNodeList.concat(getChildNodeList(jqTree, tree, children));  
                }  
            }  
        }  
        return childNodeList;  
    }  
})(jQuery);
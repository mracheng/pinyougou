app.controller("baseController",function($scope){
	
	$scope.paginationConf = {
			currentPage : 1,//当前页码
			totalItems : 10,//总记录数
			itemsPerPage : 10,//每页显示条数
			perPageOptions : [ 10, 20, 30, 40, 50 ],
			onChange : function() {
				$scope.reloadList();//重新加载
			}
	};
	
	
	//刷新列表
	$scope.reloadList=function(){
		$scope.search($scope.paginationConf.currentPage,$scope.paginationConf.itemsPerPage);
	}
	
	//选中要删除的id 的数组
	$scope.selectIds=[];
	//根据复选框中的状态确定id数组
	$scope.updateSelection=function($event,id){
		if($event.target.checked){
			$scope.selectIds.push(id);
		}else{
			var index = $scope.selectIds.indexOf(id);
			$scope.selectIds.splice(index,1);
		}
	}
	
	
	$scope.json2String=function(jsonString,key){
		var json=JSON.parse(jsonString);
		var value="";
		for (var i = 0; i < json.length; i++) {
			
			if (i>0) {
				
				value+=","+json[i][key];
			}else{
				value+=json[i][key];
			}
			
		}
		return value;
	}
	
	
	
	$scope.searchObjectByKey=function(list,key,keyValue){
		
		for(var i=0;i<list.length;i++){
			if(list[i][key]==keyValue){
				return list[i];
			}
		}
		
		return null;
		
	}
	
	
	
	
});
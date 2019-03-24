app.controller("itemController",function($scope){
	
	$scope.specificationItems={};
	
	$scope.addNum=function(x){
		$scope.num=$scope.num+x;
		if($scope.num<1){
			$scope.num=1;
		}
	}
	//选择规格
	$scope.selectSpecification=function(key,value){
		$scope.specificationItems[key]=value;
		searchSku();
	}
	
	
	$scope.isSelected=function(key,value){
		if($scope.specificationItems[key]==value){
			return true;
		}
		return false;
	}
	
	$scope.sku={};
	$scope.loadSku=function(){
		$scope.sku=skuList[0];
		$scope.specificationItems=JSON.parse(JSON.stringify($scope.sku.spec));
	}
	
	//匹配两个对象是否一样
	matchObject=function(map1,map2){
		for(var k in map1){
			if(map1[k]!=map2[k]){
				return false;
			}
		}
		for(var k in map2){
			if(map1[k]!=map2[k]){
				return false;
			}
		}
		return true;
	}
	//查找sku
	searchSku=function(){
		for(var i=0;i<skuList.length;i++){
			if(matchObject(skuList[i].spec,$scope.specificationItems)){
				$scope.sku=skuList[i];
				return;
			}
		}
		
		$scope.sku={id:0,title:'断货',price:0.0};
	}
	
	//添加购物车,sku.id弹框
	$scope.add2Cart=function(){
		alert('skuId:'+$scope.sku.id);
		
		
	}
	
	
	
});
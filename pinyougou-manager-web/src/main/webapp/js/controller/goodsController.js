 //控制层 
app.controller('goodsController' ,function($scope,$controller,$location,goodsService,itemCatService){	
	
	$controller('baseController',{$scope:$scope});//继承
	
    //读取列表数据绑定到表单中  
	$scope.findAll=function(){
		goodsService.findAll().success(
			function(response){
				$scope.list=response;
			}			
		);
	}    
	
	//分页
	$scope.findPage=function(page,rows){			
		goodsService.findPage(page,rows).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}
	
	//定义页面实体结构
	$scope.entity={goods:{},goodsDesc:{itemImages:[],specificationItems:[]}};
	//添加图片列表
	$scope.add_image_entity=function(){
		$scope.entity.goodsDesc.itemImages.push($scope.image_entity);
		
	}  
	
	//移除图片
	$scope.dele_image_entity=function(index){
		$scope.entity.goodsDesc.itemImages.splice(index,1);
	}
	
	
	
	// 查询实体
	$scope.findOne=function(){
		
		var id= $location.search()['id'];
		if (id==null) {
			return;
		}
		goodsService.findOne(id).success(
			function(response){
				$scope.entity= response;	
				editor.html($scope.entity.goodsDesc.introduction);
				$scope.entity.goodsDesc.itemImages= JSON.parse($scope.entity.goodsDesc.itemImages);
				$scope.entity.goodsDesc.customAttributeItems= JSON.parse($scope.entity.goodsDesc.customAttributeItems);
				$scope.entity.goodsDesc.specificationItems= JSON.parse($scope.entity.goodsDesc.specificationItems);
				
				for (var i = 0; i < $scope.entity.itemList.length; i++) {
					$scope.entity.itemList[i].spec = JSON.parse($scope.entity.itemList[i].spec)
				}
				
			}
		);		
	}
	
	//根据规格名称和选项名称判断是否被勾选
	$scope.checkAttributeValue=function(specName,optionName){
		var items = $scope.entity.goodsDesc.specificationItems;
		var object = $scope.searchObjectByKey(items,"attributeName", specName);
		if (object == null) {
			return false;
		} else {
			if (object.attributeValue.indexOf(optionName) >= 0) {
				return true;
			} else {
				return false;
			}
		}

	}
	
	//保存 
	$scope.save=function(){				
		var serviceObject;//服务层对象  				
		if($scope.entity.id!=null){//如果有ID
			serviceObject=goodsService.update( $scope.entity ); //修改  
		}else{
			serviceObject=goodsService.add( $scope.entity  );//增加 
		}				
		serviceObject.success(
			function(response){
				if(response.success){
					//重新查询 
		        	$scope.reloadList();//重新加载
				}else{
					alert(response.message);
				}
			}		
		);				
	}
	
	 
	//批量删除 
	$scope.dele=function(){			
		//获取选中的复选框			
		goodsService.dele( $scope.selectIds ).success(
			function(response){
				if(response.success){
					$scope.reloadList();//刷新列表
					$scope.selectIds=[];
				}						
			}		
		);				
	}
	
	$scope.searchEntity={};//定义搜索对象 
	
	//搜索
	$scope.search=function(page,rows){			
		goodsService.search(page,rows,$scope.searchEntity).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}
	
	
	
	$scope.status=['未审核','已审核','审核未通过','关闭'];
	$scope.itemCatList=[];//定义一个分类的数组
	$scope.findItemCatAll=function(){
		itemCatService.findAll().success(
			function(response){
				for (var i = 0; i < response.length; i++) {
					$scope.itemCatList[response[i].id]=response[i].name;
				}
			}
		);
	}
	
	
	
	
	
	//获取下拉列表1级分类
	$scope.selectItemCat1List=function(){
		itemCatService.findByParentId(0).success(
			function(response){
				$scope.itemCat1List=response;
			}
		
		);
	}
	
	$scope.$watch("entity.goods.category1Id",function(newValue,oldValue){
		//根据选择的值查询2级分类列表
		itemCatService.findByParentId(newValue).success(
			function(response){
				$scope.itemCat2List=response;
			}
		);
		
	})
	
	$scope.$watch("entity.goods.category2Id",function(newValue,oldValue){
		//根据选择的值查询3级分类列表
		itemCatService.findByParentId(newValue).success(
			function(response){
				$scope.itemCat3List=response;
			}
		);

	})
	
	$scope.$watch("entity.goods.category3Id",function(newValue,oldValue){
		//根据3级分类列表选择的值 查询模板Id
		itemCatService.findOne(newValue).success(
			function(response){
				$scope.entity.goods.typeTemplateId=response.typeId;
			}
		);

	})
	
	
	
	
	//批量修改商品状态
	$scope.updateStatus=function(status){
		goodsService.updateStatus($scope.selectIds,status).success(
			function(response){
				if (response.success) {
					$scope.reloadList();//刷新页面
					$scope.selectIds=[];//清空
				}else{
					alert(response.message);
				}
				
			}
		
		);
	}
	
	
	
	
	
    
});	

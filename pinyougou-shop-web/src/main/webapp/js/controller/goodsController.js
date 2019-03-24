 //控制层 
app.controller('goodsController' ,function($scope,$controller,goodsService,uploadService,itemCatService,typeTemplateService,$location){	
	
	$controller('baseController',{$scope:$scope});// 继承
	
    // 读取列表数据绑定到表单中
	$scope.findAll=function(){
		goodsService.findAll().success(
			function(response){
				$scope.list=response;
			}			
		);
	}    
	
	// 分页
	$scope.findPage=function(page,rows){			
		goodsService.findPage(page,rows).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;// 更新总记录数
			}			
		);
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
	
	
	
	 
	// 批量删除
	$scope.dele=function(){			
		// 获取选中的复选框
		goodsService.dele( $scope.selectIds ).success(
			function(response){
				if(response.success){
					$scope.reloadList();// 刷新列表
					$scope.selectIds=[];
				}						
			}		
		);				
	}
	
	$scope.searchEntity={};// 定义搜索对象
	
	// 搜索
	$scope.search=function(page,rows){			
		goodsService.search(page,rows,$scope.searchEntity).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;// 更新总记录数
			}			
		);
	}
	
	
	
	// 保存
	$scope.save = function() {
		$scope.entity.goodsDesc.introduction=editor.html();
		var serviceObject;
		if($scope.entity.goods.id!=null){
			serviceObject=goodsService.update($scope.entity);
		}else{
			serviceObject=goodsService.add($scope.entity);
		} 
			
		serviceObject.success(
			function(response) {
				if (response.success) {
					alert('保存成功');
					location.href="goods.html";
					
				} else {
					alert(response.message);
				}
		});
	}
	
	//上传图片
	$scope.uploadFile=function(){
		
		uploadService.uploadFile().success(
			function(response){
				if (response.success) {
					$scope.image_entity.url=response.message;//设置文件地址
				}else {
					alert(response.message);
				}
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
	
	
	$scope.$watch("entity.goods.typeTemplateId",function(newValue,oldValue){
		
		typeTemplateService.findOne(newValue).success(
				
			function(response){
				$scope.typeTemplate=response;//定义一个模板对象
				$scope.typeTemplate.brandIds=JSON.parse($scope.typeTemplate.brandIds);//品牌列表类型转换
		
				if ($location.search()['id']==null) {//为新增方法
					$scope.entity.goodsDesc.customAttributeItems= JSON.parse($scope.typeTemplate.customAttributeItems);
					
				}
			}
	
		);
		
		//根据模板id 查询规格选项
		typeTemplateService.findSpecList(newValue).success(
			function(response){
				$scope.specList= response;
			}	
			
		);

	})
	
	
	$scope.updateSpecAttribute=function($event,name,value){
		var object=$scope.searchObjectByKey($scope.entity.goodsDesc.specificationItems,'attributeName',name);
		if (object!=null) {
			
			if($event.target.checked){
				object.attributeValue.push(value);
				
			}else {
				//取消勾选
				object.attributeValue.splice(object.attributeValue.indexOf(value),1);
				//如果复选框都取消了
				if(object.attributeValue.length==0){
					$scope.entity.goodsDesc.specificationItems.splice($scope.entity.goodsDesc.specificationItems.indexOf(object),1);
				}
			}
		}else {
			$scope.entity.goodsDesc.specificationItems.push({"attributeName":name,"attributeValue":[value]});
		}
		
	}
	
	//创建sku列表
	$scope.createItemsList=function(){
		$scope.entity.itemList=[{spec:{},price:0,num:99999,status:'0',isDefault:'0' }];
		
		items=$scope.entity.goodsDesc.specificationItems;
		for (var i = 0; i < items.length; i++) {
			$scope.entity.itemList=addColumn($scope.entity.itemList, items[i].attributeName, items[i].attributeValue);
		}
	}
	
	//增加列的方法
	addColumn=function(list,columnName,columnValues){
		var newList=[];//新的集合
		for (var i = 0; i < list.length; i++) {
			var oldRow=list[i];
			for (var j = 0; j < columnValues.length; j++) {
				var newRow=JSON.parse(JSON.stringify(oldRow));//深克隆 
				newRow.spec[columnName]=columnValues[j];
				newList.push(newRow);
			}
		}
		return newList;
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
	
	
	
	
	
	
	
	
	
	
    
});	

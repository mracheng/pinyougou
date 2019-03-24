//创建控制器
app.controller("brandController",function($scope,$controller,brandService){
    		
    		$controller("baseController",{$scope:$scope});
    		
    		//查询品牌列表
    		$scope.findAll=function(){
    			brandService.findAll().success(
    				function(response){
    					$scope.list=response;
    				}
    			
    			);
    		}
    		
			
			
			
			//分页
			$scope.findPage=function(page,rows){
				brandService.findPage(page,rows).success(
					function(response){
						$scope.list=response.rows;
						$scope.paginationConf.totalItems=response.total;//更新总记录数
					}		
				);
			}
			//保存
			$scope.save=function(){
				
				var object=null;//
				if ($scope.entity.id!=null) {
					object=brandService.update($scope.entity);
				}else{
					object=brandService.save($scope.entity);
				}
				object.success(
						function(response) {
							if(response.success){
								//如果添加成功 就重新加载
								$scope.reloadList();
							}else {
								alter(response.message)
							}
				});
			}
			
			
			//查询实体
			$scope.findOne=function(id){
				brandService.findOne(id).success(
					function(response){
						$scope.entity=response;
					}	
				);
			}
			
			
			
			//删除
			$scope.dele=function(){
				brandService.dele($scope.selectIds).success(
					function(response){
						if (response.success) {
							$scope.reloadList();
						}else{
							alter(response.message);
						}
					}		
				);
			}
			
			
			//条件查询
			$scope.searchEntity={};
			$scope.search=function(pageNum,pageSize){
				brandService.search(pageNum,pageSize,$scope.searchEntity).success(
					function(response){
						$scope.list=response.rows;
						$scope.paginationConf.totalItems=response.total;//更新总记录数
					}		
				);
			}
			
			
			
			

	});
//建立服务层
    	app.service("brandService",function($http){
    		this.findAll=function(){
    			return $http.get("../brand/findAll.do");
    		}
    		
    		//分页
    		this.findPage=function(page,rows){
    			return $http.get("../brand/findPage.do?pageNum="+page+"&pageSize="+rows);
    		}
    		
    		this.findOne=function(id){
    			return $http.get("../brand/findOne.do?id="+id);
    		}
    		
    		this.save=function(entity){
    			return $http.post("../brand/save.do",entity);
    		}
    		
    		this.update=function(entity){
    			return $http.post("../brand/update.do",entity);
    		}
    		
    		this.dele=function(ids){
    			return $http.get("../brand/delete.do?ids="+ids);
    		}
    		
    		this.search=function(pageNum,pageSize,entity){
    			return $http.post("../brand/search.do?pageNum="+pageNum+"&pageSize="+pageSize,entity);
    		}
    		
    		this.selectOptionList=function(){
    			return $http.get("../brand/selectOptionList.do");
    		}
    		
    		
    	});
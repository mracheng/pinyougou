package com.pinyougou.manager.controller;

import java.util.List;
import java.util.Map;


import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.TbBrand;
import com.pinyougou.sellergoods.service.IBrandService;


import entity.PageResult;
import entity.Result;

@RestController
@RequestMapping("/brand")
public class BrandController {
	
	@Reference
	private IBrandService brandService;
	
	@RequestMapping("/findAll")
	private List<TbBrand> findAll(){
		
		//返回查询所有商品的list集合
		return brandService.findAll();
	}
	
	/**
	 * 分页
	 * @return
	 */
	@RequestMapping("/findPage")
	public PageResult findPage(int pageNum,int pageSize){
		
		//返回查询分页
		return brandService.findPage(pageNum, pageSize);
	}
	
	@RequestMapping("/save")
	public Result save(@RequestBody TbBrand brand) {
		try {
			brandService.save(brand);
			return new Result(true, "添加成功");
		} catch (Exception e) {
			// TODO Auto-generated catch block\
			e.printStackTrace();
			return new Result(false, "添加失败");
					
		}
	}
	
	/**
	 * 根据id查询
	 * @param id
	 * @return
	 */
	@RequestMapping("/findOne")
	public TbBrand findOne(long id) {
		return brandService.findById(id);
	}
	
	@RequestMapping("/update")
	public Result update(@RequestBody TbBrand brand) {
		
		try {
			brandService.update(brand);
			return new Result(true, "修改成功");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return new Result(false, "修改失败");
		}
	}
	
	/**
	 * 删除
	 * @param ids
	 * @return
	 */
	@RequestMapping("/delete")
	public Result delete(long[] ids) {
		try {
			brandService.delete(ids);
			return new Result(true, "删除成功");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return new Result(false, "删除失败");
		}
	}
	
	/**
	 * 搜索查询
	 * @return
	 */
	@RequestMapping("/search")
	public PageResult search(@RequestBody TbBrand brand,int pageNum,int pageSize) {
		
		return brandService.findPage(brand, pageNum, pageSize);
	}
	
	
	@RequestMapping("/selectOptionList")
	public List<Map> selectOptionList(){
		return brandService.selectOptionList();
	}
	
	
	
	
	
	
	
	

}

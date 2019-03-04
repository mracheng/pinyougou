package com.pinyougou.manager.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.TbBrand;
import com.pinyougou.sellergoods.service.IBrandService;

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
	

}

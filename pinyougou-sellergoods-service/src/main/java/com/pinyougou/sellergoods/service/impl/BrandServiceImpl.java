package com.pinyougou.sellergoods.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pinyougou.mapper.TbBrandMapper;
import com.pinyougou.pojo.TbBrand;
import com.pinyougou.pojo.TbBrandExample;
import com.pinyougou.pojo.TbBrandExample.Criteria;
import com.pinyougou.sellergoods.service.IBrandService;


import entity.PageResult;
@Service
@Transactional
public class BrandServiceImpl implements IBrandService {

	@Autowired
	private TbBrandMapper brandMapper;
	
	@Override
	public List<TbBrand> findAll() {
		
		
		// TODO Auto-generated method stub
		return brandMapper.selectByExample(null);
	}

	/**
	 * 分页查找
	 */
	@Override
	public PageResult findPage(int pageNum, int pageSize) {
		//使用分页插件
		PageHelper.startPage(pageNum, pageSize);
		Page<TbBrand> page = (Page<TbBrand>) brandMapper.selectByExample(null);
		// TODO Auto-generated method stub
		return new PageResult(page.getTotal(), page.getResult());
	}

	/**
	 * 增加品牌
	 */
	@Override
	public void save(TbBrand brand) {
		// TODO Auto-generated method stub
		brandMapper.insert(brand);
	}

	/**
	 * 根据id 查询
	 */
	@Override
	public TbBrand findById(long id) {
		// TODO Auto-generated method stub
		return brandMapper.selectByPrimaryKey(id);
	}

	/**
	 * 修改
	 */
	@Override
	public void update(TbBrand brand) {
		// TODO Auto-generated method stub
		brandMapper.updateByPrimaryKey(brand);
	}

	/**
	 * 删除
	 */
	@Override
	public void delete(long[] ids) {
		// TODO Auto-generated method stub
		for (long id : ids) {
			brandMapper.deleteByPrimaryKey(id);
		}
	}

	/**
	 * 条件查询,并且分页
	 */
	@Override
	public PageResult findPage(TbBrand brand, int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		TbBrandExample example = new TbBrandExample();
		if (brand!=null) {
			Criteria criteria = example.createCriteria();
			if (brand.getName()!=null&&brand.getName().length()>0) {
				criteria.andNameLike("%"+brand.getName()+"%");
			}
			if (brand.getFirstChar()!=null&&brand.getFirstChar().length()>0) {
				criteria.andFirstCharLike("%"+brand.getFirstChar()+"%");
			}
		}
		Page<TbBrand> page = (Page<TbBrand>) brandMapper.selectByExample(example);
		return new PageResult(page.getTotal(), page.getResult());
	}

	@Override
	public List<Map> selectOptionList() {
		// TODO Auto-generated method stub
		return brandMapper.selectOptionList();
	}

}

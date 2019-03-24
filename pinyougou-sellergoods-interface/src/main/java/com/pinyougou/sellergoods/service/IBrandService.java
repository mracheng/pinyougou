package com.pinyougou.sellergoods.service;

import java.util.List;
import java.util.Map;

import com.pinyougou.pojo.TbBrand;
import com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel;

import entity.PageResult;

/**
 * 品牌接口
 * @author huangcheng
 *
 */
public interface IBrandService {
	/**
	 * 查找所有
	 * @return
	 */
	public List<TbBrand> findAll();
	
	/**
	 * 分页查找
	 * @param pageNum
	 * @param pageSize
	 * @return
	 */
	public PageResult findPage(int pageNum,int pageSize);
	
	/**
	 * 增加品牌
	 * @param brand
	 */
	public void save(TbBrand brand);
	
	/**
	 * 根据id查找
	 * @param id
	 * @return
	 */
	public TbBrand findById(long id);
	
	/**
	 * 修改
	 * @param brand
	 */
	public void update(TbBrand brand);
	
	/**
	 * 删除
	 * @param ids
	 */
	public void delete(long[] ids);
	
	
	public PageResult findPage(TbBrand brand,int pageNum,int pageSize);
	
	
	public List<Map> selectOptionList();

}

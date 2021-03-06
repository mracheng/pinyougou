package com.pinyougou.content.service.impl;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pinyougou.mapper.TbContentMapper;
import com.pinyougou.pojo.TbContent;
import com.pinyougou.pojo.TbContentExample;
import com.pinyougou.pojo.TbContentExample.Criteria;
import com.pinyougou.content.service.ContentService;

import entity.PageResult;

/**
 * 服务实现层
 * @author Administrator
 *
 */
@Service
public class ContentServiceImpl implements ContentService {

	@Autowired
	private TbContentMapper contentMapper;
	
	@Autowired
	private RedisTemplate redistemplate;
	
	/**
	 * 查询全部
	 */
	@Override
	public List<TbContent> findAll() {
		return contentMapper.selectByExample(null);
	}

	/**
	 * 按分页查询
	 */
	@Override
	public PageResult findPage(int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);		
		Page<TbContent> page=   (Page<TbContent>) contentMapper.selectByExample(null);
		return new PageResult(page.getTotal(), page.getResult());
	}

	/**
	 * 增加
	 */
	@Override
	public void add(TbContent content) {
		contentMapper.insert(content);	
		
		redistemplate.boundHashOps("content").delete(content.getCategoryId());//删除缓存
		
	}

	
	/**
	 * 修改
	 */
	@Override
	public void update(TbContent content){
		
		//查询更改前的categoryId 
		Long categoryId = contentMapper.selectByPrimaryKey(content.getId()).getCategoryId();
		redistemplate.boundHashOps("content").delete(categoryId);//清除修改前的所属分类id 的缓存
		
		contentMapper.updateByPrimaryKey(content);
		
		if (content.getCategoryId().longValue()!=categoryId.longValue()) {
			redistemplate.boundHashOps("content").delete(content.getCategoryId());//清除修改后的所属分类id 的缓存
		}
		
	}	
	
	/**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
	@Override
	public TbContent findOne(Long id){
		return contentMapper.selectByPrimaryKey(id);
	}

	/**
	 * 批量删除
	 */
	@Override
	public void delete(Long[] ids) {
		for(Long id:ids){
			Long categoryId = contentMapper.selectByPrimaryKey(id).getCategoryId();
			redistemplate.boundHashOps("content").delete(categoryId);//清除缓存
			
			contentMapper.deleteByPrimaryKey(id);
		}		
	}
	
	
		@Override
	public PageResult findPage(TbContent content, int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		
		TbContentExample example=new TbContentExample();
		Criteria criteria = example.createCriteria();
		
		if(content!=null){			
						if(content.getTitle()!=null && content.getTitle().length()>0){
				criteria.andTitleLike("%"+content.getTitle()+"%");
			}
			if(content.getUrl()!=null && content.getUrl().length()>0){
				criteria.andUrlLike("%"+content.getUrl()+"%");
			}
			if(content.getPic()!=null && content.getPic().length()>0){
				criteria.andPicLike("%"+content.getPic()+"%");
			}
			if(content.getStatus()!=null && content.getStatus().length()>0){
				criteria.andStatusLike("%"+content.getStatus()+"%");
			}
	
		}
		
		Page<TbContent> page= (Page<TbContent>)contentMapper.selectByExample(example);		
		return new PageResult(page.getTotal(), page.getResult());
	}

		
		
		
		/**
		 * 根据广告分类id 查询广告信息
		 */
		@Override
		public List<TbContent> findByCategoryId(Long categoryId) {
			
			List<TbContent> list = (List<TbContent>) redistemplate.boundHashOps("content").get(categoryId);
			if (list==null) {
				System.out.println("从数据库中获取数据");
				TbContentExample example = new TbContentExample();
				Criteria criteria = example.createCriteria();
				criteria.andCategoryIdEqualTo(categoryId);//指定条件分类id
				criteria.andStatusEqualTo("1");//指定条件 为 状态有效
				example.setOrderByClause("sort_order");//排序
				
				list = contentMapper.selectByExample(example );
				
				redistemplate.boundHashOps("content").put(categoryId, list);//放入缓存
				
				
			}else {
				System.out.println("从 缓存中读取");
			}
			
			
			return list;
		}
	
}

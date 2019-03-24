package com.pinyougou.search.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.Criteria;
import org.springframework.data.solr.core.query.FilterQuery;
import org.springframework.data.solr.core.query.GroupOptions;
import org.springframework.data.solr.core.query.HighlightOptions;
import org.springframework.data.solr.core.query.Join;
import org.springframework.data.solr.core.query.Query;
import org.springframework.data.solr.core.query.SimpleFilterQuery;
import org.springframework.data.solr.core.query.SimpleHighlightQuery;
import org.springframework.data.solr.core.query.SimpleQuery;
import org.springframework.data.solr.core.query.SolrDataQuery;
import org.springframework.data.solr.core.query.result.GroupEntry;
import org.springframework.data.solr.core.query.result.GroupPage;
import org.springframework.data.solr.core.query.result.GroupResult;
import org.springframework.data.solr.core.query.result.HighlightEntry;
import org.springframework.data.solr.core.query.result.HighlightEntry.Highlight;
import org.springframework.data.solr.core.query.result.HighlightPage;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.search.service.ItemSearchService;

@Service(timeout=5000)
public class ItemSearchServiceImpl implements ItemSearchService {

	
	
	@Autowired
	private SolrTemplate solrTemplate;
	
	
	
	
	@Override
	public Map<String, Object> search(Map searchMap) {
		Map map = new HashMap();
		String keywords = (String) searchMap.get("keywords");
		searchMap.put("keywords", keywords.replace(" ", ""));
		
		//查询列表
		map.putAll(searchMap(searchMap));
		
		
		//分组查询结果,商品分类类表
		List<String> categoryList = searchCategoryList(searchMap);
		map.put("categoryList", categoryList);
		
		// 查询品牌和规格列表
		String categoryName = (String)searchMap.get("category");
		if (!"".equals(categoryName)) {
			map.putAll(searchBrandAndSpecList(categoryName));
		}else {
			
			if(categoryList.size()>0) {
				map.putAll(searchBrandAndSpecList(categoryList.get(0)));
			}
		}
		
		
		return map;
	}
	
	
	
	private Map searchMap(Map searchMap) {
		Map map = new HashMap();

		//高亮显示
		SimpleHighlightQuery query = new SimpleHighlightQuery();
		//构建高亮选项对象
		HighlightOptions highlightOptions = new HighlightOptions().addField("item_title");//设置高亮域
		highlightOptions.setSimplePrefix("<em style='color:red'>");//设置高亮前缀
		highlightOptions.setSimplePostfix("</em>");//设置高亮后缀
		query.setHighlightOptions(highlightOptions);//设置高亮选项
		
		//1.1关键字查询
		Criteria criteria = new Criteria("item_keywords").is(searchMap.get("keywords"));
		query.addCriteria(criteria);
		//1.2 按分类筛选
		if (!"".equals(searchMap.get("category"))) {
			FilterQuery filterQuery = new SimpleFilterQuery();
			Criteria filterCriteria = new Criteria("item_category").is(searchMap.get("category")); 
			filterQuery.addCriteria(filterCriteria);
			query.addFilterQuery(filterQuery);
		}
		
		//1.3按品牌分类
		if (!"".equals(searchMap.get("brand"))) {
			FilterQuery filterQuery = new SimpleFilterQuery();
			Criteria filterCriteria = new Criteria("item_brand").is(searchMap.get("brand")); 
			filterQuery.addCriteria(filterCriteria);
			query.addFilterQuery(filterQuery);
		}
		
		//1.4按规格过滤
		if (searchMap.get("spec")!=null) {
			
			Map<String, String> specMap =  (Map<String, String>)searchMap.get("spec");
			for (String key : specMap.keySet()) {
				FilterQuery filterQuery = new SimpleFilterQuery();
				Criteria filterCriteria = new Criteria("item_spec_"+key).is(specMap.get(key)); 
				filterQuery.addCriteria(filterCriteria);
				query.addFilterQuery(filterQuery);
			}
			
		}
		
		//1.5 按价格过滤
		if (!"".equals(searchMap.get("price"))) {
			
			String[] price = ((String)searchMap.get("price")).split("-");
			if (!price[0].equals("0")) {//如果最低价不为0,则按照区间筛选
				Criteria filterCriteria = new Criteria("item_price").greaterThanEqual(price[0]);//大于等于最低价
				FilterQuery filterQuery = new SimpleFilterQuery(filterCriteria );
				query.addFilterQuery(filterQuery );
			}
			
			if (!price[1].equals("*")) {//如果最高价不为*,则按照区间筛选
				Criteria filterCriteria = new Criteria("item_price").lessThanEqual(price[1]);//小于等于最高价
				FilterQuery filterQuery = new SimpleFilterQuery(filterCriteria );
				query.addFilterQuery(filterQuery );
			}
		}
		
		//1.6 分页
		Integer pageNo = (Integer) searchMap.get("pageNo");// 提取页码
		if (pageNo == null) {
			pageNo = 1;// 默认第一页
		}
		Integer pageSize = (Integer) searchMap.get("pageSize");// 每页记录数
		if (pageSize == null) {
			pageSize = 20;// 默认 20
		}
		query.setOffset((pageNo - 1) * pageSize);// 从第几条记录查询
		query.setRows(pageSize);
		
		
		//1.7 排序
		String sortValue= (String) searchMap.get("sort");//ASC DESC
		String sortField= (String) searchMap.get("sortField");//排序字段
		if(sortValue!=null && !sortValue.equals("")){
			if(sortValue.equals("ASC")){
				Sort sort=new Sort(Sort.Direction.ASC, "item_"+sortField);
				query.addSort(sort);
			}
			if(sortValue.equals("DESC")){
				Sort sort=new Sort(Sort.Direction.DESC, "item_"+sortField);
				query.addSort(sort);
			}
		}

		
		
		//************* 高亮查询  ,获取高亮结果集 *******************
		//高亮集合入口
		HighlightPage<TbItem> page = solrTemplate.queryForHighlightPage(query, TbItem.class);
		List<HighlightEntry<TbItem>> entryList = page.getHighlighted();
		for (HighlightEntry<TbItem> entry : entryList) {
			//获取高亮集合(高亮域的个数)
			List<Highlight> highlights = entry.getHighlights();
			if (highlights.size()>0&& highlights.get(0).getSnipplets().size()>0) {
				
				TbItem item = entry.getEntity();
				item.setTitle(highlights.get(0).getSnipplets().get(0));
			}
		}
		map.put("rows", page.getContent());
		
		map.put("totalPages", page.getTotalPages());//返回总页数
		map.put("total", page.getTotalElements());//返回总记录数
		
		return map;
		
	}
	
	
	
	//分组查询,根据搜索框中的信息,查询商品分类信息
	
	private List<String> searchCategoryList(Map searchMap) {
		List<String> list = new ArrayList();
		
		Query query = new SimpleQuery();
		//关键字条件查询  相当于where
		Criteria criteria = new Criteria("item_keywords").is(searchMap.get("keywords"));
		query.addCriteria(criteria);
		
		
		GroupOptions groupOptions = new GroupOptions().addGroupByField("item_category"); //相当于 group by 查询
		query.setGroupOptions(groupOptions);
		//获取分组页
		GroupPage<TbItem> groupPage = solrTemplate.queryForGroupPage(query, TbItem.class);
		//获取分组结果对象
		GroupResult<TbItem> groupResult = groupPage.getGroupResult("item_category");
		//获取分组入口页
		Page<GroupEntry<TbItem>> groupEntries = groupResult.getGroupEntries();
		//获取分组入口集合
		List<GroupEntry<TbItem>> groupList = groupEntries.getContent();
		for (GroupEntry<TbItem> entry : groupList) {
			list.add(entry.getGroupValue());//将分组的结果添加到返回的结果中
		}
		
		System.out.println(list);
		
		
		return list;
	}
	
	@Autowired
	private RedisTemplate redisTemplate;
	//从缓存中根据商品分类信息,查询到品牌信息和规格选项列表
	private Map searchBrandAndSpecList(String category) {
		HashMap map = new HashMap();
		
		//根据商品分类名称 查询到模板id
		Long templateId = (Long)redisTemplate.boundHashOps("itemCat").get(category);
		
		if (templateId!=null) {
			//根据模板id 查询到品牌列表
			List brandList = (List)redisTemplate.boundHashOps("brandList").get(templateId);
			map.put("brandList", brandList);
			System.out.println(brandList);
			
			//根据模板id 查询到规格选项列表
			List specList = (List)redisTemplate.boundHashOps("specList").get(templateId);
			map.put("specList",specList);
			System.out.println(specList);
		}
		
		
		return map;
		
		
	}



	
	
	/**
	 * 导入数据
	 */
	@Override
	public void importList(List list) {
		
		solrTemplate.saveBeans(list);
		solrTemplate.commit();
	}



	/**
	 * 删除索引库数据
	 */
	@Override
	public void deleteByGoodsIds(List goodsIdList) {
		System.out.println("删除商品id:"+goodsIdList);
		
		SolrDataQuery query = new SimpleQuery();
		Criteria criteria = new Criteria("item_goodsid").in(goodsIdList);
		query.addCriteria(criteria);
		solrTemplate.delete(query);
		
	}

}

package com.pinyougou.solrutil;

import java.util.List;
import java.util.Map;

import javax.swing.plaf.nimbus.NimbusLookAndFeel;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.pinyougou.mapper.TbItemMapper;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.pojo.TbItemExample;
import com.pinyougou.pojo.TbItemExample.Criteria;


@Component
public class SolrUtil {
	
	@Autowired
	private TbItemMapper itemMapper;
	@Autowired
	private SolrTemplate solrTemplate;
	
	//导入商品数据
	public void importItemDat() {
		
		TbItemExample example = new TbItemExample();
		Criteria criteria = example.createCriteria();
		criteria.andStatusEqualTo("1");
		
		List<TbItem> itemList = itemMapper.selectByExample(example);
		System.out.println("商品列表");
		
		
		for (TbItem item : itemList) {
			System.out.println(item.getId()+"  "+item.getTitle()+"  "+item.getPrice());
			
			Map specMap = JSON.parseObject(item.getSpec());
			item.setSpecMap(specMap);
			
		}
		
		//将查询到的sku 集合,导入到索引库中
				solrTemplate.saveBeans(itemList);
				solrTemplate.commit();
		System.out.println("列表结束");
	}
	
	
	public static void main(String[] args) {
		ApplicationContext applicationContext = new ClassPathXmlApplicationContext("classpath*:spring/applicationContext*.xml");
		SolrUtil solrUtil = (SolrUtil) applicationContext.getBean("solrUtil");
		solrUtil.importItemDat();
	}

}

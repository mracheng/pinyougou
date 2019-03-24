package com.pinyougou.manager.controller;
import java.util.Arrays;
import java.util.List;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.druid.sql.visitor.functions.Nil;
import com.alibaba.dubbo.common.json.JSON;
import com.alibaba.dubbo.config.annotation.Reference;

import com.pinyougou.pojo.TbGoods;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.pojogroup.Goods;
import com.pinyougou.sellergoods.service.GoodsService;

import entity.PageResult;
import entity.Result;
/**
 * controller
 * @author Administrator
 *
 */
@RestController
@RequestMapping("/goods")
public class GoodsController {

	@Reference
	private GoodsService goodsService;
	

	
	
	
	
	
	@Autowired
	private Destination queueSolrDestination;
	
	@Autowired
	private Destination queueSolrDeleteDestination;//用户在索引库中删除记录
	@Autowired
	private Destination topicPageDestination;//生成静态化页面
	
	@Autowired
	private JmsTemplate jmsTemplate;
	
	/**
	 * 返回全部列表
	 * @return
	 */
	@RequestMapping("/findAll")
	public List<TbGoods> findAll(){			
		return goodsService.findAll();
	}
	
	
	/**
	 * 返回全部列表
	 * @return
	 */
	@RequestMapping("/findPage")
	public PageResult  findPage(int page,int rows){			
		return goodsService.findPage(page, rows);
	}
	
	/**
	 * 增加
	 * @param goods
	 * @return
	 */
	@RequestMapping("/add")
	public Result add(@RequestBody Goods goods){
		try {
			goodsService.add(goods);
			return new Result(true, "增加成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "增加失败");
		}
	}
	
	/**
	 * 修改
	 * @param goods
	 * @return
	 */
	@RequestMapping("/update")
	public Result update(@RequestBody Goods goods){
		try {
			goodsService.update(goods);
			return new Result(true, "修改成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "修改失败");
		}
	}	
	
	/**
	 * 获取实体
	 * @param id
	 * @return
	 */
	@RequestMapping("/findOne")
	public Goods findOne(Long id){
		return goodsService.findOne(id);		
	}
	
	
	@Autowired
	private Destination topicPageDeleteDestination;//用于删除静态网页的消息
	
	/**
	 * 批量删除
	 * @param ids
	 * @return
	 */
	@RequestMapping("/delete")
	public Result delete(Long [] ids){
		try {
			goodsService.delete(ids);
			
			//删除索引库
			jmsTemplate.send(queueSolrDeleteDestination, new MessageCreator() {
				
				@Override
				public Message createMessage(Session session) throws JMSException {
					
					return session.createObjectMessage(ids);
				}
			});
			
			
			
			//删除页面
			jmsTemplate.send(topicPageDeleteDestination, new MessageCreator() {
				@Override
				public Message createMessage(Session session) throws JMSException {
					return session.createObjectMessage(ids);
				}
			});
		
			
			
			
			return new Result(true, "删除成功"); 
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "删除失败");
		}
	}
	
		/**
	 * 查询+分页
	 * @param brand
	 * @param page
	 * @param rows
	 * @return
	 */
	@RequestMapping("/search")
	public PageResult search(@RequestBody TbGoods goods, int page, int rows  ){
		return goodsService.findPage(goods, page, rows);		
	}
	
	
	
	//审核
	@RequestMapping("/updateStatus")
	public Result updateStatus(Long[] ids,String status) {
		try {
			goodsService.updateStatus(ids, status);
			
			if(status.equals("1")){//审核通过
				final List<TbItem> itemList = goodsService.findItemListByGoodsIdandStatus(ids, status);
				//调用搜索接口实现数据批量导入
				if(itemList.size()>0){
					//itemSearchService.importList(itemList);
					//发布点对点消息
					String jsonString = com.alibaba.fastjson.JSON.toJSONString(itemList);
					jmsTemplate.send(queueSolrDestination, new MessageCreator() {
						
						@Override
						public Message createMessage(Session session) throws JMSException {
						
							return session.createTextMessage(jsonString);
						}
					});
					
					
					
				}else{
					System.out.println("没有明细数据");
				}
			}
			
			
			//生成静态页面
			/*for (Long goodsId : ids) {
				itemPageService.genItemHtml(goodsId);
			}*/
			
			//静态页生成
			for(final Long goodsId:ids){
				jmsTemplate.send(topicPageDestination, new MessageCreator() {
					@Override
					public Message createMessage(Session session) throws JMSException {
						return session.createTextMessage(goodsId+"");
					}
				});
			}

			
			
			
			
			return new Result(true, "修改成功");
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return new Result(false, "修改失败");
		}
		
	}
	
	
	
	
}

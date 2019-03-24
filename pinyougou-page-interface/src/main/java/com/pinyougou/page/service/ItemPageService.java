package com.pinyougou.page.service;

import java.util.logging.Logger;

/**
 * 商品详细页接口
 * @author huangcheng
 *
 */
public interface ItemPageService {
	
	/**
	 * 生成商品详细页信息
	 * @param goodsId
	 * @return
	 */
	public boolean genItemHtml(Long goodsId);
	
	
	/**
	* 删除商品详细页
	* @param goodsId
	* @return
	*/
	public boolean deleteItemHtml(Long[] goodsIds);
	
	
	

}

package cn.tf.ecps.service.impl;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.tf.ecps.dao.EbConsoleLogDao;
import cn.tf.ecps.dao.EbItemClobDao;
import cn.tf.ecps.dao.EbItemDao;
import cn.tf.ecps.dao.EbParaValueDao;
import cn.tf.ecps.dao.EbSkuDao;

import cn.tf.ecps.po.EbConsoleLog;
import cn.tf.ecps.po.EbItem;
import cn.tf.ecps.po.EbItemClob;
import cn.tf.ecps.po.EbParaValue;
import cn.tf.ecps.po.EbSku;
import cn.tf.ecps.service.EbItemService;
import cn.tf.ecps.utils.ECPSUtil;
import cn.tf.ecps.utils.Page;
import cn.tf.ecps.utils.QueryCondition;

@Service
public class EbItemServiceImpl implements EbItemService {

	@Autowired
	private EbItemDao itemDao;
	@Autowired
	private EbItemClobDao itemClobDao;
	@Autowired
	private EbParaValueDao paraValueDao;
	@Autowired
	private EbSkuDao skuDao;
	
	@Autowired
	private EbConsoleLogDao consoleLogDao;

	public Page selectItemByCondition(QueryCondition qc) {
		// 获得页码
		Integer pageNo = qc.getPageNo();
		// 创建page对象
		Page page = new Page();
		page.setPageNo(pageNo);
		// 查询当前条件下的记录数
		Integer totalCount = itemDao.selectItemByConditionCount(qc);
		page.setTotalCount(totalCount);
		// 获得开始行号和结束行号
		Integer startNum = page.getStartNum();
		Integer endNum = page.getEndNum();
		// 把开始行号和结束行号设置给qc对象
		qc.setStartNum(startNum);
		qc.setEndNum(endNum);
		// 查询结果集
		List<EbItem> itemList = itemDao.selectItemByCondition(qc);
		// 给page对象设置结果集
		page.setList(itemList);
		return page;
	}

	public int deleteGoods(String id) {

		return itemDao.deleteGoods(id);
	}

	public EbItem selectItemByNo(String id) {

		return itemDao.selectItemByNo(id);
	}

	// 保存
	public void saveItem(EbItem item, EbItemClob itemClob,
			List<EbParaValue> paraList, List<EbSku> skuList) {
		// 保存商品并且返回主键
		itemDao.saveItem(item);
		// 给大字段表设置外键
		itemClob.setItemId(item.getItemId());
		itemClobDao.saveItemClob(itemClob);
		paraValueDao.saveParaValue(paraList, item.getItemId());
		skuDao.saveSku(skuList, item.getItemId());

	}

	public void auditItem(Long itemId, Short auditStatus, String notes) {
		//变更审核字段
		EbItem item = new EbItem();
		item.setAuditStatus(auditStatus);
		item.setItemId(itemId);
		itemDao.updateItem(item);
		
		//插入操作日志
		EbConsoleLog log = new EbConsoleLog();
		log.setEntityId(itemId);
		log.setEntityName("商品表");
		log.setNotes(notes);
		log.setOpTime(new Date());
		log.setOpType(ECPSUtil.readProp("AUDIT_ITEM_TYPE"));
		log.setTableName("EB_ITEM");
		log.setUserId(1l);  //未实现
		consoleLogDao.saveLog(log);
	}
	
	public void showItem(Long itemId, Short showStatus, String notes) {
		//变更审核字段
		EbItem item = new EbItem();
		item.setShowStatus(showStatus);
		item.setItemId(itemId);
		itemDao.updateItem(item);
		
		//插入操作日志
		EbConsoleLog log = new EbConsoleLog();
		log.setEntityId(itemId);
		log.setEntityName("商品表");
		log.setNotes(notes);
		log.setOpTime(new Date());
		log.setOpType(ECPSUtil.readProp("show_item_type"));
		log.setTableName("EB_ITEM");
		log.setUserId(1l);   //未实现
		consoleLogDao.saveLog(log);
	}

}

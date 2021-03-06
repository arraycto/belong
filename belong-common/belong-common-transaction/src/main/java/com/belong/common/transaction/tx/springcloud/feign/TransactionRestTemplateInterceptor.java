package com.belong.common.transaction.tx.springcloud.feign;

import com.codingapi.tx.aop.bean.TxTransactionLocal;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import lombok.extern.slf4j.Slf4j;

/**
* @Description:
* @Author:         fengyu
* @CreateDate:     2019/12/16 10:05
*/
@Slf4j
public class TransactionRestTemplateInterceptor implements RequestInterceptor {

	@Override
	public void apply(RequestTemplate requestTemplate) {
		log.info("开始检查是否有分布式事务参与，如果有将记录--->groupId");
		TxTransactionLocal txTransactionLocal = TxTransactionLocal.current();
		String groupId = txTransactionLocal == null ? null : txTransactionLocal.getGroupId();
		log.info("LCN-SpringCloud TxGroup info -> groupId:" + groupId);
		if (txTransactionLocal != null) {
			requestTemplate.header("tx-group", groupId);
		}
	}

}

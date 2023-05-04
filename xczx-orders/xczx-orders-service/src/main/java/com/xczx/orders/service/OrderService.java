package com.xczx.orders.service;

import com.xczx.orders.model.dto.AddOrderDto;
import com.xczx.orders.model.dto.PayRecordDto;
import com.xczx.orders.model.dto.PayStatusDto;
import com.xczx.orders.model.po.XcPayRecord;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: ilovesshan
 * @date: 2023/5/4
 * @description:
 */

public interface OrderService {
    // 创建订单
    PayRecordDto createOrder(String userId, AddOrderDto addOrderDto);

    // 根据支付号查询支付信息
    XcPayRecord getPayRecordByPayNo(String payNo);

    // 查询支付结果
    PayRecordDto getPayResult(String payNo);

    // 根据payNo号查询支付宝支付状态
    PayStatusDto getAliPayPayStatus(Long orderId, String payNo);

    // 更新订单状态（支付宝）
    XcPayRecord saveAlipayOrderStatus(String outTradNo, Long orderId, Long payId);
}

package com.xczx.orders.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xczx.base.exception.XczxException;
import com.xczx.base.utils.IdWorkerUtils;
import com.xczx.base.utils.QRCodeUtil;
import com.xczx.orders.mapper.XcOrdersGoodsMapper;
import com.xczx.orders.mapper.XcOrdersMapper;
import com.xczx.orders.mapper.XcPayRecordMapper;
import com.xczx.orders.model.dto.AddOrderDto;
import com.xczx.orders.model.dto.PayRecordDto;
import com.xczx.orders.model.po.XcOrders;
import com.xczx.orders.model.po.XcOrdersGoods;
import com.xczx.orders.model.po.XcPayRecord;
import com.xczx.orders.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: ilovesshan
 * @date: 2023/5/4
 * @description:
 */

@Slf4j
@Service
public class OrderServiceImpl implements OrderService {

    @Resource
    private XcOrdersMapper ordersMapper;

    @Resource
    private XcOrdersGoodsMapper ordersGoodsMapper;

    @Resource
    private XcPayRecordMapper payRecordMapper;

    @Value("${pay.qrcodeurl}")
    private String qrcodeurl;

    @Override
    @Transactional
    public PayRecordDto createOrder(String userId, AddOrderDto addOrderDto) {

        // 向订单表/订单详情表插入数据
        XcOrders orders = saveOrders(userId, addOrderDto);

        // 向订单支付记录表插入数据
        XcPayRecord payRecord = savePayRecord(orders);

        // 生成二维码
        String QrCodeUrl = generatorQrCode(payRecord.getPayNo());

        // 组装数据
        PayRecordDto payRecordDto = new PayRecordDto();
        BeanUtils.copyProperties(payRecord, payRecordDto);
        payRecordDto.setQrcode(QrCodeUrl);
        return payRecordDto;
    }


    @Override
    public XcPayRecord getPayRecordByPayNo(String payNo) {
        LambdaQueryWrapper<XcPayRecord> queryWrapper = new LambdaQueryWrapper<XcPayRecord>().eq(XcPayRecord::getPayNo, payNo);
        return payRecordMapper.selectOne(queryWrapper);
    }


    private String generatorQrCode(Long payNo) {
        String code = "";
        try {
            //url要可以被模拟器访问到，url为下单接口(稍后定义)
            code = new QRCodeUtil().createQRCode(String.format(qrcodeurl, payNo), 200, 200);
        } catch (IOException e) {
            log.error("生成支付二维码出错, payNo = {}", payNo, e);
            throw new XczxException("生成支付二维码出错");
        }
        return code;
    }

    private XcPayRecord savePayRecord(XcOrders orders) {
        Long ordersId = orders.getId();
        String status = orders.getStatus();
        if ("600002".equals(status)) {
            log.debug("订单已支付, ordersId = {}", ordersId);
            throw new XczxException("订单已支付");
        }

        // 向订单支付记录表插入数据
        XcPayRecord payRecord = new XcPayRecord();
        // 本系统支付交易号
        payRecord.setPayNo(IdWorkerUtils.getInstance().nextId());
        payRecord.setOrderId(ordersId);
        payRecord.setOrderName(orders.getOrderName());
        payRecord.setUserId(orders.getUserId());
        payRecord.setTotalPrice(orders.getTotalPrice());
        payRecord.setCurrency("CNY");
        payRecord.setCreateDate(LocalDateTime.now());
        // 未支付
        payRecord.setStatus("601001");
        payRecord.setUserId(orders.getUserId());
        int affectRows = payRecordMapper.insert(payRecord);
        if (affectRows <= 0) {
            log.error("订单支付记录失败, ordersId = {}", ordersId);
            throw new XczxException("订单支付记录失败");
        }
        return payRecord;
    }

    private XcOrders saveOrders(String userId, AddOrderDto addOrderDto) {
        String outBusinessId = addOrderDto.getOutBusinessId();
        LambdaQueryWrapper<XcOrders> queryWrapper = new LambdaQueryWrapper<XcOrders>().eq(XcOrders::getOutBusinessId, outBusinessId);
        XcOrders order = ordersMapper.selectOne(queryWrapper);
        // orders不为空，说明该订单已经创建过了
        if (order != null) {
            return order;
        }

        // 向订单表插入数据
        order = new XcOrders();
        long orderId = IdWorkerUtils.getInstance().nextId();
        order.setId(orderId);
        order.setCreateDate(LocalDateTime.now());
        order.setStatus("600001");
        order.setUserId(userId);
        BeanUtils.copyProperties(addOrderDto, order);
        int affectRows = ordersMapper.insert(order);
        if (affectRows <= 0) {
            log.error("新增订单记录失败, userId = {}, ordersId = {}", userId, outBusinessId);
            throw new XczxException("新增订单记录失败");
        }

        // 向订单详情表插入数据
        String goodsDetail = addOrderDto.getOrderDetail();
        List<XcOrdersGoods> ordersGoodsList = JSON.parseArray(goodsDetail, XcOrdersGoods.class);
        ordersGoodsList.forEach(ordersGoods -> {
            // 生成主键ID
            ordersGoods.setId(IdWorkerUtils.getInstance().nextId());
            // 关联订单ID
            ordersGoods.setOrderId(orderId);
            int insertRows = ordersGoodsMapper.insert(ordersGoods);
            if (insertRows <= 0) {
                log.error("新增订单详情记录失败, userId = {}, ordersId = {}", userId, outBusinessId);
                throw new XczxException("新增订单详情记录失败");
            }
        });
        return order;
    }
}

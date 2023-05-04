package com.xczx.orders.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipayTradeQueryRequest;
import com.alipay.api.response.AlipayTradeQueryResponse;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xczx.base.config.AlipayConfig;
import com.xczx.base.exception.XczxException;
import com.xczx.base.utils.IdWorkerUtils;
import com.xczx.base.utils.QRCodeUtil;
import com.xczx.orders.mapper.XcOrdersGoodsMapper;
import com.xczx.orders.mapper.XcOrdersMapper;
import com.xczx.orders.mapper.XcPayRecordMapper;
import com.xczx.orders.model.dto.AddOrderDto;
import com.xczx.orders.model.dto.PayRecordDto;
import com.xczx.orders.model.dto.PayStatusDto;
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

    @Resource
    private OrderService orderServiceProxy;

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


    @Override
    public PayRecordDto getPayResult(String payNo) {
        PayRecordDto payRecordDto = new PayRecordDto();
        // 调用支付宝SDK通过订单号查询支付结果
        XcPayRecord payRecord = getPayRecordByPayNo(payNo);
        if (payRecord == null) {
            throw new XczxException("未查询到该条订单记录");
        }
        PayStatusDto aliPayPayStatus = getAliPayPayStatus(payRecord.getOrderId(), payNo);
        // 支付成功
        if ("TRADE_SUCCESS".equals(aliPayPayStatus.getTrade_status())) {
            payRecord = orderServiceProxy.saveAlipayOrderStatus(aliPayPayStatus.getOut_trade_no(), payRecord.getPayNo());
            BeanUtils.copyProperties(payRecord, payRecordDto);
            return payRecordDto;
        }
        return payRecordDto;
    }

    @Override
    public PayStatusDto getAliPayPayStatus(Long orderId, String payNo) {
        PayStatusDto payStatus = new PayStatusDto();
        AlipayClient alipayClient = new DefaultAlipayClient(AlipayConfig.URL, AlipayConfig.APPID, AlipayConfig.RSA_PRIVATE_KEY, AlipayConfig.FORMAT, AlipayConfig.CHARSET, AlipayConfig.ALIPAY_PUBLIC_KEY, AlipayConfig.SIGNTYPE);
        AlipayTradeQueryRequest request = new AlipayTradeQueryRequest();
        JSONObject bizContent = new JSONObject();
        bizContent.put("out_trade_no", payNo);
        request.setBizContent(bizContent.toString());
        AlipayTradeQueryResponse response = null;
        try {
            response = alipayClient.execute(request);
        } catch (AlipayApiException e) {
            log.error("调用支付宝SDK通过订单号查询支付结果【失败】orderId = {}, payNo = {}", orderId, payNo, e);
            throw new XczxException("该笔订单交易不存在");
        }
        if (response.isSuccess()) {
            log.debug("调用支付宝SDK通过订单号查询支付结果【成功】orderId = {}, payNo = {}", orderId, payNo);
            payStatus.setTrade_status(response.getTradeStatus());
            payStatus.setApp_id(AlipayConfig.APPID);
            payStatus.setTotal_amount(response.getTotalAmount());
            payStatus.setTrade_no(response.getTradeNo());
            payStatus.setOut_trade_no(response.getOutTradeNo());
            return payStatus;
        } else {
            log.error("调用支付宝SDK通过订单号查询支付结果【失败】orderId = {}, payNo = {}", orderId, payNo);
        }
        return null;
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


    @Override
    @Transactional
    public XcPayRecord saveAlipayOrderStatus(String outTradNo, Long payNo) {
        // 前置条件判断
        XcPayRecord payRecord = payRecordMapper.selectOne(new LambdaQueryWrapper<XcPayRecord>().eq(XcPayRecord::getPayNo, payNo));
        if (payRecord == null) {
            throw new XczxException("未查询到该条订单记录");
        }

        Long orderId = payRecord.getOrderId();
        XcOrders orders = ordersMapper.selectById(orderId);
        if (orders == null) {
            throw new XczxException("未查询到该条订单记录");
        }

        if ("600002".equals(orders.getStatus())) {
            log.debug("订单已支付，orderId = {}， payNo = {}", orderId, payNo);
            return payRecord;
        }
        if ("601002".equals(payRecord.getStatus())) {
            log.debug("订单已支付，orderId = {}， payNo = {}", orderId, payNo);
            return payRecord;
        }

        // 更新订单表订单状态
        orders.setStatus("600002");
        int affectRows = ordersMapper.updateById(orders);


        // 更新订单支付记录表的订单状态、支付流水号、支付渠道、支付成功时间
        payRecord.setStatus("601002");
        payRecord.setOutPayNo(outTradNo);
        payRecord.setOutPayChannel("Alipay");
        payRecord.setPaySuccessTime(LocalDateTime.now());
        affectRows += payRecordMapper.updateById(payRecord);

        if (affectRows != 2) {
            throw new XczxException("数据表状态更新失败");
        }
        return payRecord;
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

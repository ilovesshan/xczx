package com.xczx.orders.api;

import com.alibaba.fastjson.JSONObject;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipayTradeWapPayRequest;
import com.xczx.base.exception.XczxException;
import com.xczx.orders.config.AlipayConfig;
import com.xczx.orders.model.dto.AddOrderDto;
import com.xczx.orders.model.dto.PayRecordDto;
import com.xczx.orders.model.po.XcPayRecord;
import com.xczx.orders.service.OrderService;
import com.xczx.orders.util.SecurityUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: ilovesshan
 * @date: 2023/5/4
 * @description:
 */

@Api(value = "订单支付接口", tags = "订单支付接口")
@Slf4j
@Controller
public class OrderController {

    @Resource
    private OrderService orderService;

    @ApiOperation("生成支付二维码")
    @PostMapping("/generatepaycode")
    @ResponseBody
    public PayRecordDto generatePayCode(@RequestBody AddOrderDto addOrderDto) {
        String userId = SecurityUtil.getUser().getId();
        return orderService.createOrder(userId, addOrderDto);
    }

    @ApiOperation("扫码下单接口")
    @GetMapping("/requestpay")
    public void requestPay(String payNo, HttpServletResponse httpResponse) throws IOException, AlipayApiException {
        XcPayRecord payRecord = orderService.getPayRecordByPayNo(payNo);
        if (payRecord == null) {
            throw new XczxException("订单不存在");
        }

        if ("601002".equals(payRecord.getStatus())) {
            throw new XczxException("订单已支付，切勿重复支付");
        }

        AlipayClient alipayClient = new DefaultAlipayClient(
                AlipayConfig.URL, AlipayConfig.APPID, AlipayConfig.RSA_PRIVATE_KEY, AlipayConfig.FORMAT,
                AlipayConfig.CHARSET, AlipayConfig.ALIPAY_PUBLIC_KEY, AlipayConfig.SIGNTYPE
        );
        // 获得初始化的AlipayClient
        AlipayTradeWapPayRequest alipayRequest = new AlipayTradeWapPayRequest();
        // 异步接收地址，仅支持http/https，公网可访问
        // alipayRequest.setReturnUrl("http://domain.com/CallBack/return_url.jsp");
        // alipayRequest.setNotifyUrl("https://548e0e8e.cpolar.io/orders/payResultNotify");

        JSONObject bizContent = new JSONObject();
        bizContent.put("out_trade_no", payNo);
        bizContent.put("total_amount", payRecord.getTotalPrice());
        bizContent.put("subject", payRecord.getOrderName());
        bizContent.put("product_code", "QUICK_MSECURITY_PAY");
        alipayRequest.setBizContent(bizContent.toString());

        // 调用SDK生成表单
        String form = alipayClient.pageExecute(alipayRequest).getBody();
        httpResponse.setContentType("text/html;charset=" + AlipayConfig.CHARSET);
        // 直接将完整的表单html输出到页面（手机端会自动拉起支付宝应用用于三方支付）
        httpResponse.getWriter().write(form);
        httpResponse.getWriter().flush();
    }
}
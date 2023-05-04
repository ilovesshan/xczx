package com.xczx.orders.api;

import com.alibaba.fastjson.JSONObject;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.request.AlipayTradeWapPayRequest;
import com.xczx.base.exception.XczxException;
import com.xczx.base.config.AlipayConfig;
import com.xczx.orders.model.dto.AddOrderDto;
import com.xczx.orders.model.dto.PayRecordDto;
import com.xczx.orders.model.po.XcPayRecord;
import com.xczx.orders.service.OrderService;
import com.xczx.orders.util.SecurityUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

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

    @Value("${pay.notifyUrl}")
    private String notifyUrl;

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
        alipayRequest.setNotifyUrl(notifyUrl);

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


    @ApiOperation("查询支付结果")
    @GetMapping("/payresult")
    @ResponseBody
    public PayRecordDto getPayResult(String payNo) throws IOException {
        return orderService.getPayResult(payNo);
    }

    @PostMapping("/payResultNotify")
    public void payResultNotify(HttpServletRequest request, HttpServletResponse response) throws IOException, AlipayApiException {
        Map<String, String> params = new HashMap<>();
        Map requestParams = request.getParameterMap();
        for (Iterator iter = requestParams.keySet().iterator(); iter.hasNext(); ) {
            String name = (String) iter.next();
            String[] values = (String[]) requestParams.get(name);
            String valueStr = "";
            for (int i = 0; i < values.length; i++) {
                valueStr = (i == values.length - 1) ? valueStr + values[i]
                        : valueStr + values[i] + ",";
            }
            params.put(name, valueStr);
        }

        boolean verify_result = AlipaySignature.rsaCheckV1(params, AlipayConfig.ALIPAY_PUBLIC_KEY, AlipayConfig.CHARSET, "RSA2");

        //验证成功
        if (verify_result) {
            //商户订单号
            String out_trade_no = new String(request.getParameter("out_trade_no").getBytes("ISO-8859-1"), "UTF-8");
            //支付宝交易号
            String trade_no = new String(request.getParameter("trade_no").getBytes("ISO-8859-1"), "UTF-8");
            //交易状态
            String trade_status = new String(request.getParameter("trade_status").getBytes("ISO-8859-1"), "UTF-8");
            //交易成功
            if ("TRADE_SUCCESS".equals(trade_status)) {
                orderService.saveAlipayOrderStatus(trade_no, Long.valueOf(out_trade_no));
            }
            response.getWriter().write("success");
        } else {
            response.getWriter().write("fail");
        }
    }
}
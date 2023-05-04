package com.xczx.orders.api;

import com.alibaba.fastjson.JSONObject;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.request.AlipayTradeQueryRequest;
import com.alipay.api.request.AlipayTradeWapPayRequest;
import com.alipay.api.response.AlipayTradeQueryResponse;
import com.xczx.base.config.AlipayConfig;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.ServletException;
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
 * @date: 2023/5/3
 * @description:
 */

//@Api(value = "订单支付接口", tags = "订单支付接口")
@Controller
public class AliPayController {

//    @ApiOperation("订单支付结果三方回调接口")
    @PostMapping("/payResultNotifytest")
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
            //乱码解决，这段代码在出现乱码时使用。如果mysign和sign不相等也可以使用这段代码转化
            //valueStr = new String(valueStr.getBytes("ISO-8859-1"), "gbk");
            params.put(name, valueStr);
        }


        //获取支付宝的通知返回参数，可参考技术文档中页面跳转同步通知参数列表(以上仅供参考)//
        //计算得出通知验证结果
        boolean verify_result = AlipaySignature.rsaCheckV1(params, AlipayConfig.ALIPAY_PUBLIC_KEY, AlipayConfig.CHARSET, "RSA2");

        if (verify_result) {//验证成功
            //////////////////////////////////////////////////////////////////////////////////////////
            //请在这里加上商户的业务逻辑程序代码

            //商户订单号
            String out_trade_no = new String(request.getParameter("out_trade_no").getBytes("ISO-8859-1"), "UTF-8");
            //支付宝交易号

            String trade_no = new String(request.getParameter("trade_no").getBytes("ISO-8859-1"), "UTF-8");

            //交易状态
            String trade_status = new String(request.getParameter("trade_status").getBytes("ISO-8859-1"), "UTF-8");


            //——请根据您的业务逻辑来编写程序（以下代码仅作参考）——

            if (trade_status.equals("TRADE_FINISHED")) {//交易结束
                //判断该笔订单是否在商户网站中已经做过处理
                //如果没有做过处理，根据订单号（out_trade_no）在商户网站的订单系统中查到该笔订单的详细，并执行商户的业务程序
                //请务必判断请求时的total_fee、seller_id与通知时获取的total_fee、seller_id为一致的
                //如果有做过处理，不执行商户的业务程序

                //注意：
                //如果签约的是可退款协议，退款日期超过可退款期限后（如三个月可退款），支付宝系统发送该交易状态通知
                //如果没有签约可退款协议，那么付款完成后，支付宝系统发送该交易状态通知。
            } else if (trade_status.equals("TRADE_SUCCESS")) {//交易成功
                System.out.println(trade_status);
                //判断该笔订单是否在商户网站中已经做过处理
                //如果没有做过处理，根据订单号（out_trade_no）在商户网站的订单系统中查到该笔订单的详细，并执行商户的业务程序
                //请务必判断请求时的total_fee、seller_id与通知时获取的total_fee、seller_id为一致的
                //如果有做过处理，不执行商户的业务程序

                //注意：
                //如果签约的是可退款协议，那么付款完成后，支付宝系统发送该交易状态通知。
            }
            response.getWriter().write("success");
        } else {
            response.getWriter().write("fail");
        }
    }


//    @ApiOperation("订单支付")
    @GetMapping("/pay")
    public void pay(HttpServletRequest httpRequest, HttpServletResponse httpResponse) throws ServletException, IOException, AlipayApiException {
        AlipayClient alipayClient = new DefaultAlipayClient(
                AlipayConfig.URL, AlipayConfig.APPID, AlipayConfig.RSA_PRIVATE_KEY,
                AlipayConfig.FORMAT, AlipayConfig.CHARSET, AlipayConfig.ALIPAY_PUBLIC_KEY, AlipayConfig.SIGNTYPE
        );

        //获得初始化的AlipayClient
        AlipayTradeWapPayRequest alipayRequest = new AlipayTradeWapPayRequest();
        // 异步接收地址，仅支持http/https，公网可访问
        // alipayRequest.setReturnUrl("http://domain.com/CallBack/return_url.jsp");
        alipayRequest.setNotifyUrl("https://548e0e8e.cpolar.io/orders/payResultNotify");

        JSONObject bizContent = new JSONObject();
        bizContent.put("out_trade_no", "20210817010101004");
        bizContent.put("total_amount", 15998.00);
        bizContent.put("subject", "MacBook Air (M1 芯片机型)");
        bizContent.put("product_code", "QUICK_MSECURITY_PAY");
        alipayRequest.setBizContent(bizContent.toString());

        // 调用SDK生成表单
        String form = alipayClient.pageExecute(alipayRequest).getBody();
        httpResponse.setContentType("text/html;charset=" + AlipayConfig.CHARSET);
        // 直接将完整的表单html输出到页面（手机端会自动拉起支付宝应用用于三方支付）
        httpResponse.getWriter().write(form);
        httpResponse.getWriter().flush();
    }


//    @ApiOperation("查询订单支付状态")
    @GetMapping("/selectOrder/{orderId}")
    @ResponseBody
    public AlipayTradeQueryResponse selectOrder(@PathVariable("orderId") String orderId) throws AlipayApiException {
        AlipayClient alipayClient = new DefaultAlipayClient(
                AlipayConfig.URL, AlipayConfig.APPID, AlipayConfig.RSA_PRIVATE_KEY,
                AlipayConfig.FORMAT, AlipayConfig.CHARSET, AlipayConfig.ALIPAY_PUBLIC_KEY, AlipayConfig.SIGNTYPE
        );
        AlipayTradeQueryRequest request = new AlipayTradeQueryRequest();
        JSONObject bizContent = new JSONObject();
        bizContent.put("out_trade_no", orderId);
        request.setBizContent(bizContent.toString());
        AlipayTradeQueryResponse response = alipayClient.execute(request);
        if (response.isSuccess()) {
            System.out.println("调用成功");
        } else {
            System.out.println("调用失败");
        }
        return response;
    }
}

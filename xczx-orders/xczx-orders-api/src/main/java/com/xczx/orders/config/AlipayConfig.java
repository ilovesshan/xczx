package com.xczx.orders.config;

/**
 * @author Mr.M
 * @version 1.0
 * @description 支付宝配置参数
 * @date 2022/10/20 22:45
 */
public class AlipayConfig {
    // 商户appid
    public static String APPID = "2021000122685629";

    // 私钥 pkcs8格式的
    public static String RSA_PRIVATE_KEY = "MIIEvAIBADANBgkqhkiG9w0BAQEFAASCBKYwggSiAgEAAoIBAQCqzMdYF0NQpenBJrIoYurJ/xAX3BvRHMtmae6NMyGYcDzLCU6MWe0qEEo71Mxz/tf+5Esy5AhrlsmSjh7oqD7vkBNJRyFSvhyWw3H0A5nN+QaJ3m6m/utpcIt+dFSMbzZrTIISRTW7Ulb8aOno/hiikICtXveTTxXn7r9OhqcFg8wLTpoy2DpOCUbmzuFeHvfXOzuuOht8Fuksz4/vgfv6x9SE/noqhxp6Nw0gI2alJHtB9fDQLXZ2LQGOlmtTvZlAgPUb9O5XiB3DOkLEgKhm4J27CDsQIlpLsDlkWn2FBcWCdGCiTeA+bkbiX11eGOjocAeWZkhExLeYhLSNbmybAgMBAAECggEAZfqiw2wQWyTU7uiF0ytMeJL8LuLTEQYaQ+nX7yKqWnQZODk0VwSSYF0dlZ+2+0McACqa46XXiTWc4Q5OUieyfCMQAN6hfN/Z/gJwFXvY52xahRL0Cm2GbYwvHa+jk74LRgGGYBZGwLOnEEor71tXpiiVS+6575B2yeZ4/w/Sxa0wpeyMPmi7z3yFbkhIbbtiOIx8JIs7ZNyl+GsRwAx85/eo1j0JM6nXJ6uvck5ZmO+yNUiLqQejIL4AmxhwvbNb0nC1eJFxvC5Z67bgJAQ5nn9A6NtHW90ktDojax2sW8wk2PWTmPVkUlrJKht5ezMLnhBS4FiOg1QQxPqMLWgBMQKBgQD2UatyLDBOmhqPJh47oaHMMdSO/K+h9v2HKY31vpb59I4ppmxvkTCdm+EQWa4L0kpPm9XvC6gI8o/ipn1HRNqiC78rza3pxcUGAzqJCoCW80oMtu4INoOKo/CmxK/SELKvzfjXbugcRXtACmFWcnmfzWQ9ns6dfOsaxxqIwAzE3wKBgQCxg0avu6zCtRhIEgnul1fQdcS/jgU1Y0nNsSFtG3vfzh1mRIARHEtwY5V3biOGPjPsXY2O2TZorvSifb1kjRmSHajBkH4Wwmj3J+YAjDBQPV979lKyTLIvixMRXayH/EvbnYQuY+VHkNqql23CUECZolCe7XSiUNDVCSIlXW2zxQKBgDhnRSoUwk8NbK2A3gVroqAefPztEc61vyJXOqGeLfY4sOEKsePuTEKa8jOLRZaBZfDKz3c4pRa/bIFK/H74XlQi+niuVXeliNMypBHDbOSj+z3kGaeZzA9QMQgPG++vUSt+r8+tHstygaGNfvKLrhwQrTaLCeb4NuHf28yapZHjAoGAWH65nqJRXkAp+sQDb47DmIkdrOGAdz0obgMARwSMnOaEgZdkwSL5+O2sdf3Sd/pKqGt7RtvC5C1UiC1nXdl6Bf4DQ3xlX7NL4OtjHqLE8zulfocucvUJX+pVdzLb8G824iWE4ButEiCE4sbbUSPxEKZ6ysBWhRUFBy+mdjo9loECgYAlQxNcjVqvB8AKgaheV+SpH4aJi+i2eXpm0PDaDtTiYgEw4iJTHCWmVlX70zHFOg1P4o+oHQg0j7pdZ1RMwMjUtyR2GBYg7KbpNGpYGjkQFtWR0V4S+0UMcmpnynsd2jMGwqcFv5yEOCrVek7c71y04Rhi5ONSxM47d/sze4L4Fg==";

    // 服务器异步通知页面路径 需http://或者https://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问
    public static String notify_url = "http://商户网关地址/alipay.trade.wap.pay-JAVA-UTF-8/notify_url.jsp";

    // 页面跳转同步通知页面路径 需http://或者https://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问 商户可以自定义同步跳转地址
    public static String return_url = "http://商户网关地址/alipay.trade.wap.pay-JAVA-UTF-8/return_url.jsp";

    // 请求网关地址
    public static String URL = "https://openapi.alipaydev.com/gateway.do";
    // 编码

    public static String CHARSET = "UTF-8";

    // 返回格式
    public static String FORMAT = "json";

    // 支付宝公钥
    public static String ALIPAY_PUBLIC_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAkC1ARv+tDDVLkOsUGp7hiM5zyIr57HeGSfKRxHsJxTUHWqs2RY524dWigxHCNH2ZcnQIoSmJBPRpmrpSkDJjj+7WpYwCCkjvntauC/UCK/x100cxNsRvuF4cQznokIn5taMtmkCTBZBC2JAvAAJTdlbDhzpDGgTnsY4yoGPvwmZ69UV4kiqi1Md24XGPUf5+WLWU/9m/XA51949y+C8FPZwP2GWQTQiWW6GKz8364J9Lw02tENfgoIugdrfVlaQCAOnpfayE02xsCxLHudVZzdxhscuo3jtumi+Erd4mDNY7uBrAz1nxCRbaVeWEY0Ce4EmZMHQjX2N2qmGiJanVewIDAQAB";
    // 日志记录目录

    public static String log_path = "/log";

    // RSA2
    public static String SIGNTYPE = "RSA2";
}

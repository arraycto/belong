package com.belong.common.exception.wxapplet;


import com.belong.common.exception.base.BaseException;

/**
 * @Description: 微信小程序根异常
 * @Author: fengyu
 * @CreateDate: 2019/11/26 17:43
 * @UpdateDate: 2019/11/26 17:43
 * @Version: 1.0
 */
public class WxAppletException extends BaseException {
    private static final long serialVersionUID = 1L;
    public WxAppletException(String msg) {
        super(msg);
    }
    public WxAppletException(String code, Object[] args) {
        super("wxapplet", code, args, null);
    }
}

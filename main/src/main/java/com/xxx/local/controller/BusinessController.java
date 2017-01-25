package com.xxx.local.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 业务代码
 *
 * @author xiaobaoqiu  Date: 17-1-19 Time: 下午3:06
 */
@Controller
@RequestMapping("/business")
public class BusinessController {

    /**
     * 详情
     */
    @RequestMapping(value = "detail.json", method = RequestMethod.GET)
    @ResponseBody
    public String detail(String businessId) {
        return null;
    }
}

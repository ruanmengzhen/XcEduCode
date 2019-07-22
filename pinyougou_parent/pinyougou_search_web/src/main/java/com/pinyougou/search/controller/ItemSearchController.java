package com.pinyougou.search.controller;

import com.alibaba.dubbo.config.annotation.Reference;

import com.pinyougou.search.service.ItemSearchService;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/ItemSearch")
public class ItemSearchController {

    @Reference
    private ItemSearchService itemSerachService;
    @RequestMapping("/search")//接受post请求 加@RequestBody注解
    public Map<String, Object> search(@RequestBody Map searchMap) {
        return  itemSerachService.search(searchMap);
    }

}

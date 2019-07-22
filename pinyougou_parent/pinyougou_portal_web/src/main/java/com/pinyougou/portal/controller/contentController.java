package com.pinyougou.portal.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.content.service.ContentService;
import com.pinyougou.pojo.TbContent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 广告列表的控制层
 */
@RestController
@RequestMapping("/content")
public class contentController {

    @Reference
    private ContentService contentService;

    /**
     * 根据广告分类id 查询广告列表
     * @param categoryId 广告分类id
     * @return
     */
    @RequestMapping("/findByCategoryId")
    public List<TbContent> findByCategoryId(Long categoryId){
        return contentService.findByCategoryId(categoryId);
    }
}

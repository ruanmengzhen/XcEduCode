package com.xuecheng.manage_cms.service;

import com.xuecheng.framework.domain.cms.CmsConfig;
import com.xuecheng.manage_cms.dao.CmsConfigRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * 页面配置 业务逻辑层
 */
@Service
public class CmsConfigService {

    @Autowired
    CmsConfigRepository cmsConfigRepository;


    /**
     * 根据配置id获取cms的配置数据模型
     * @param id
     * @return
     */
    public CmsConfig getConfigById(String id){
        Optional<CmsConfig> optional = cmsConfigRepository.findById(id);

        CmsConfig cmsConfig = optional.orElse(null);
        return cmsConfig;
    }
}

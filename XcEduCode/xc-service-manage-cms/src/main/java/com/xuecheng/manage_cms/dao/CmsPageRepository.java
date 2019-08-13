package com.xuecheng.manage_cms.dao;

import com.fasterxml.jackson.core.SerializableString;
import com.xuecheng.framework.domain.cms.CmsPage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * cms页面的dao层 操作数据库
 * MongoRepository<T.ID> T:实体类类型  ID：主键的类型
 */
public interface CmsPageRepository extends MongoRepository<CmsPage,String> {

    //根据页面名称查询
    CmsPage findByPageName(String pageName);

    //根据页面名称和类型查询
    CmsPage findByPageNameAndPageType(String pageName,String pageType);

    //根据站点和页面类型查询记录
    int countBySiteIdAndPageType(String siteId, String pageType);

    //根据站点和页面类型分页查询
    Page<CmsPage> findBySiteIdAndPageName(String siteId, String pageName, Pageable pageable);


    /**新增页面时需要确保创建的页面名称、站点Id、页面webpath为唯一索引
     *  因此在新增前先 根据页面名称、站点Id、页面webpath查询页面是否存在
     */
     CmsPage findByPageNameAndSiteIdAndPageWebPath(String pageName,String siteId,String pageWebPath);
}

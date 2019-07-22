package com.pinyougou.page.service.impl;

//import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.mapper.TbGoodsDescMapper;
import com.pinyougou.mapper.TbGoodsMapper;
import com.pinyougou.mapper.TbItemCatMapper;
import com.pinyougou.mapper.TbItemMapper;
import com.pinyougou.page.service.ItemPageService;
import com.pinyougou.pojo.*;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
//import javassist.runtime.Desc;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//生成静态页面的实现类
@Service

public class ItemPageSeviceImpl implements ItemPageService {

    @Autowired
    private FreeMarkerConfigurer freeMarkerConfigurer;

    @Value("${pagedir}")
    private String pagedir;//生成文件目录的键

    @Autowired
    private TbGoodsMapper goodsMapper;//查询商品信息
    @Autowired
    private TbGoodsDescMapper goodsDescMapper;//查询商品扩展信息
    @Autowired
    private TbItemCatMapper itemCatMapper;//查询商品分类 信息
    @Autowired
    private TbItemMapper itemMapper;//查询sku信息


    /**
     * 根据商品id 生成商品详情页面
     * @param goodsId
     * @return
     */
    @Override
    public Boolean getItemHtml(Long goodsId) {
        //声明write，关闭流时使用
        Writer out=null;
        try {
            //1.创建Configuration对象，注入FreeMarkerConfigurer
            Configuration configuration = freeMarkerConfigurer.getConfiguration();
            //2.创建模板对象,参数是：模板文件
            Template template = configuration.getTemplate("item.ftl");
            //3.创建模板使用的数据集，将需要展示的数据添加到集合中
            Map dataModel=new HashMap();

            //3.1加载商品表中的信息进数据集,需要查询商品表，因此注入TbGoodsMapper
            TbGoods goods = goodsMapper.selectByPrimaryKey(goodsId);
            dataModel.put("goods",goods);

            //3.2加载尚品扩展表中的信息；需要查询商品扩展表，因此注入TbGoodsDescMapper
            TbGoodsDesc goodsDesc = goodsDescMapper.selectByPrimaryKey(goodsId);
            dataModel.put("goodsDesc",goodsDesc);

            //3.3加载商品分类名称，通过商品分类id 查询商品分类的名称，注入TbItemCat表
            String categoryName1 = itemCatMapper.selectByPrimaryKey(goods.getCategory1Id()).getName();
            String categoryName2 = itemCatMapper.selectByPrimaryKey(goods.getCategory2Id()).getName();
            String categoryName3 = itemCatMapper.selectByPrimaryKey(goods.getCategory3Id()).getName();
            dataModel.put("itemCat1",categoryName1);
            dataModel.put("itemCat2",categoryName2);
            dataModel.put("itemCat3",categoryName3);

            //3.4加载SKU信息，通过 商品的状态 和 商品id，并对按照是否默认进行降序排列，保证第一个为默认，，进行查询 sku信息
            TbItemExample example=new TbItemExample();
            TbItemExample.Criteria criteria = example.createCriteria();
            criteria.andStatusEqualTo("1");
            criteria.andGoodsIdEqualTo(goodsId);
            example.setOrderByClause("is_default desc");
            List<TbItem> itemList = itemMapper.selectByExample(example);
            dataModel.put("itemList",itemList);

            //4.创建writer对象，并制定生成的文件名，
            // 文件名使用pagedir.properties配置文件中配置的目录+商品id+.html；需要注入配置文件中的目录的键
            out=new FileWriter(pagedir+goodsId+".html");
            //5.调用模板对象的process 方法输出文件
            template.process(dataModel,out);
            //文件生成成功返回true
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            //文件生成失败返回false
            return false;
        } catch (TemplateException e) {
            e.printStackTrace();
            //文件生成失败返回false
            return false;
        }finally{
            //6.关闭流
            try {
                if (out!=null){
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    /**
     * 根据商品id 删除商品详情页面
     * @param goodsIds
     * @return
     */
    @Override
    public Boolean deleteItemHtml(Long[] goodsIds) {
        try {
            for (Long goodsId : goodsIds) {
                //删除文件, 文件路径和生成的路径一致
                new File(pagedir+goodsId+".html").delete();
            }
            return true;
        } catch (Exception e) {
            return false;
        }


    }
}

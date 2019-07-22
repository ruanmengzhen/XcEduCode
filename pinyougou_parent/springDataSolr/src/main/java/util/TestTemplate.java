package util;

import cn.itcast.pojo.TbItem;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.*;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:applicationContext-solr.xml")
public class TestTemplate {

    @Autowired
    private SolrTemplate solrTemplate;

    /**
     *  测试增加修改
     *  增加修改用的同一个方法，id相同下，会覆盖之前的值
     */

    @Test
    public void testAdd(){
        TbItem item=new TbItem();
        item.setId(1L);
        item.setBrand("华为");
        item.setCategory("手机");
        item.setPrice(new BigDecimal(3000));
        item.setSeller("华为专卖店");
        item.setTitle("华为 x23");
        item.setGoodsId(1L);
        solrTemplate.saveBean(item);
        solrTemplate.commit();

    }
    /**
     * 按照主键查询
     *
     */
    @Test
    public void testFindOne(){
        TbItem item =solrTemplate.getById(1,TbItem.class);
        System.out.println(item.getBrand());
    }

    /**
     * 删除所有
     */

    @Test
    public void testDelete(){
        Query query=new SimpleQuery("*:*");
        solrTemplate.delete(query);
        solrTemplate.commit();
    }

    /**
     * 循环插入 100 条测试数据
     */
    @Test
    public void testAddList(){
        List<TbItem> list = new ArrayList<TbItem>();
        for (int i = 0; i < 100; i++) {
            TbItem item =new TbItem();
            item.setId(i+1L);
            item.setBrand("联想");
            item.setGoodsId(i+1L);
            item.setSeller("联想专卖店");
            item.setTitle("联想 红色4G手机"+i);
            item.setPrice(new BigDecimal(2500+i));
            list.add(item);
        }
        //solrTemplate.saveBeans(list);//添加

        //删除商家是联想专卖店的 信息
        //构建删除对象
        Query query=new SimpleQuery("*:*");//
        Criteria criteria=new Criteria("item_seller").is("联想专卖店");
       query.addCriteria(criteria);
        solrTemplate.delete(query);

        solrTemplate.commit();
    }

    /**
     * 分页查询
     */
    @Test
    public void testPageQuery(){

        //创建Query对象 配置主要查询条件 查询所有 *：*
        Query query=new SimpleQuery("*:*");
        query.setOffset(10);//设置开始索引，默认从0开始
        query.setRows(30);//设置每页显示条数，默认10条

        //分页查询用的是queryForPage的方法，参数 需要一个Query
        Page<TbItem> page = solrTemplate.queryForPage(query,TbItem.class);

        long totalElements = page.getTotalElements();//获取总记录数
        List<TbItem> content = page.getContent();//获取当前页的数据
        int totalPages = page.getTotalPages();//总页码
        System.out.println("每页显示的条数"+content.size());
        System.out.println("总记录数："+totalElements);
        System.out.println("总页码"+totalPages);
    }

    /**
     * 条件查询
     */
    @Test
    public void testPageQueryMutil(){
        Query query = new SimpleQuery("*:*");
        //构建查询条件
        Criteria criteria=new Criteria("item_brand").is("联想");//将标题进行匹配词条 查询词条是联想的

        criteria=criteria.and("item_title").contains("手机");// 匹配词条包含手机的值

        //过滤查询 FilterQuery
        FilterQuery filterQuery=new SimpleQuery();
        filterQuery.addCriteria(criteria);

        query.addCriteria(criteria);
        //分页查询用的是queryForPage的方法，参数 需要一个Query
        Page<TbItem> page = solrTemplate.queryForPage(query,TbItem.class);

        long totalElements = page.getTotalElements();//获取总记录数
        List<TbItem> content = page.getContent();//获取当前页的数据
        int totalPages = page.getTotalPages();//总页码
        System.out.println("每页显示的条数"+content.size());
        System.out.println("总记录数："+totalElements);
        System.out.println("总页码"+totalPages);
    }

    @Test
    public void testFilterQuery(){
        Query query = new SimpleQuery("*:*");
        //构建查询条件,匹配词条包含 6
        Criteria criteria=new Criteria("item_brand").contains("联想");
        //过滤查询 FilterQuery
        FilterQuery filterQuery=new SimpleQuery();
        filterQuery.addCriteria(criteria);

        query.addCriteria(criteria);
        //分页查询用的是queryForPage的方法，参数 需要一个Query
        Page<TbItem> page = solrTemplate.queryForPage(query,TbItem.class);

        long totalElements = page.getTotalElements();//获取总记录数
        List<TbItem> content = page.getContent();//获取当前页的数据
        int totalPages = page.getTotalPages();//总页码
        System.out.println("每页显示的条数"+content.size());
        System.out.println("总记录数："+totalElements);
        System.out.println("总页码"+totalPages);
    }














}

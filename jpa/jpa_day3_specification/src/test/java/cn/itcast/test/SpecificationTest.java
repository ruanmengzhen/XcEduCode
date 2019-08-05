package cn.itcast.test;

import cn.itcast.dao.CustomerDao;
import cn.itcast.domain.Customer;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.persistence.criteria.*;
import java.util.List;

//使用specification 中的方法 完成条件查询

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:applicationContext.xml")
public class SpecificationTest {
    @Autowired
    private CustomerDao customerDao;



    /**
     * 根据客户名称查询
     * CriteriaQuery：内部封装了很多的查询方法
     * root： 获取查询对象需要的属性
     * 查询条件  ：查询方式。比较的属性名称
     */
    @Test
    public void testSpecification(){
        Specification<Customer> spec=new Specification<Customer>() {
            public Predicate toPredicate(Root<Customer> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                //获取查询对象需要的属性
                Path<Object> custName = root.get("custName");
                //构建查询条件的方法, 等值查询，参数：属性的对象，实际的属性的值
                Predicate predicate = cb.equal(custName, "传智");
                return predicate;
            }
        };

        Customer customer = customerDao.findOne(spec);
        System.out.println(customer);
    }

    /**
     * 多条件查询
     *    案例：根据客户名（传智播客）和客户所属行业查询（it教育）
     */
    @Test
    public void testSpec(){
        Specification<Customer> spec = new Specification<Customer>() {
            public Predicate toPredicate(Root<Customer> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                //获取查询对象的属性
                Path<Object> custName = root.get("custName");
                Path<Object> custIndustry = root.get("custIndustry");
                //构建查询方法  先等值匹配   两个条件都要符合 使用and条件
                Predicate p1 = cb.equal(custName, "传智");
                Predicate p2 = cb.equal(custIndustry, "IT行业");

                Predicate predicate = cb.and(p1, p2);
                return predicate;

            }
        };

        List<Customer> list = customerDao.findAll(spec);
        for (Customer customer : list) {
            System.out.println(customer);
        }
    }

/**
 * 案例：完成根据客户名称的模糊匹配，返回客户列表
 *      客户名称以 ’传智播客‘ 开头
 *
 * equal ：直接的到path对象（属性），然后进行比较即可
 * gt，lt,ge,le,like : 得到path对象，根据path指定比较的参数类型，再去进行比较
 *      指定参数类型：path.as(属性类型的字节码对象)
 */
    @Test
    public void testSpec1(){
        Specification<Customer> spec = new Specification<Customer>() {
            public Predicate toPredicate(Root<Customer> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
               //获取查询对象的属性
                Path<Object> custName = root.get("custName");
                //构建查询 方法
                Predicate like = cb.like(custName.as(String.class), "传%");
                return like;

            }
        };
/*
        //模糊查询所有
        List<Customer> list = customerDao.findAll(spec);
        for (Customer customer : list) {
            System.out.println(customer);
        }*/

        //根据id排序查询,DESC:
        Sort sort=new Sort(Sort.Direction.DESC,"custId");
        customerDao.findAll(spec,sort);


    }









}

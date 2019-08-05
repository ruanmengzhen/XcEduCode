package cn.itcast.test;

import cn.itcast.dao.CustomerDao;
import cn.itcast.domain.Customer;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)//声明spring的测试环境
@ContextConfiguration(locations = "classpath:applicationContext.xml")//指定spring容器的配置信息
public class JpqlTest {

    @Autowired
    private CustomerDao customerDao;


    /**
     *测试 根据客户名称和id 查询
     */
    @Test
    public void testFindCustNameAndId(){
        List<Customer> list = customerDao.findByCustNameAndId("传智播客", 1l);
        for (Customer customer : list) {
            System.out.println(customer);
        }
    }

    /**
     * 测试 根据id修改客户名称
     */
    @Test
    public void testUpdate(){
        customerDao.updateByIdAndCustName(2l, "黑马 程序眼");

    }

    /**
     * 使用sql语句查询
     */
    @Test
    public void testFind(){
        List<Object[]> list = customerDao.findSQl();
        for (Object[] objects : list) {
            System.out.println(objects);
        }
    }



    /**
     * 按照spring Data jpa 的命名规则定义查询方法名称
     * 命名规则：
     *  简单查询：    findBy+属性名(首字母大写)+查询方式
     *              当没有查询方式时 默认等值匹配
     *  多条件查询： findBy+属性名(首字母大写)+查询方式+条连接符(and|or)+属性名(首字母大写)+查询方式+…………
     */

    //测试   根据客户名称查询
    @Test
    public void testFindByCustName(){
        Customer custName = customerDao.findByCustName("传智播客");
        System.out.println(custName);
    }


    //测试  根据客户名称模糊查询
    @Test
    public void testFindByCustNameLike(){
        List<Customer> list = customerDao.findByCustNameLike("传智%");
        for (Customer customer : list) {

            System.out.println(customer);
        }
    }

    //测试  使用客户名称模糊匹配和客户所属行业精准匹配的查询
    @Test
    public void testFindByCustNameLikeAndAndCustIndustry(){
        List<Customer> list = customerDao.findByCustNameLikeAndAndCustIndustry("传智%", "IT行业");
        for (Customer customer : list) {

            System.out.println(customer);
        }

    }

}

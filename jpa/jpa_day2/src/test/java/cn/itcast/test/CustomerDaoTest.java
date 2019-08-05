package cn.itcast.test;

import cn.itcast.dao.CustomerDao;
import cn.itcast.domain.Customer;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringJUnit4ClassRunner.class)//声明spring提供的测试环境
@ContextConfiguration(locations ="classpath:applicationContext.xml")//指定spring容器的配置信息
public class CustomerDaoTest {

    @Autowired
    private CustomerDao customerDao;


    //测试保存操作
    @Test
    public void testSave(){
        Customer customer = new Customer();
        customer.setCustAddress("北京");
        customer.setCustName("黑马程序员");
        customer.setCustIndustry("IT教育");
        customerDao.save(customer);
    }

    /**
     * 修改操作，修改也用save方法，会先根据查询该对象中是否存在id 存在 则修改 不存下 则添加
     *
     */
    @Test
    public void testUpdate(){
//       //根据id查询customer
//        Customer customer = customerDao.findOne(1l);
//        customer.setCustAddress("西安");
//        customerDao.save(customer);

        Customer customer = new Customer();
        customer.setCustName("wzdsg");
        customer.setCustId(2l);
        customer.setCustAddress("南京");
        customerDao.save(customer);
    }


    //删除表中的信息，根据id 删除 先查询后删除
    @Test
    public void testDelete(){
        //根据id 删除方法
        customerDao.delete(3l);
    }

    /**
     * 根据id  查询一个 对象 findOne :立即查询
     * getOne
     */
    @Test
    public void testFindOne(){

        Customer customer = customerDao.findOne(1l);
        System.out.println(customer);
    }

    /**
     * getOne: 得到的是个代理对象
     */
    @Test
    @Transactional
    public void testGetOne(){
        Customer customer = customerDao.getOne(1l);

        System.out.println(customer);
    }


    /**
     * 查询总记录数
      */
    @Test
    public void testCount(){
        long count = customerDao.count();
        System.out.println(count);
    }

    /**
     * 判断id为1 的对象是否存在
     * 存在返回true 不存在返回false
     */
    @Test
    public void testExist(){
        boolean exists = customerDao.exists(1l);
        System.out.println(exists);
    }
}

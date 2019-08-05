import cn.itcast.SpringBootJpaApplication;
import cn.itcast.dao.CustomerDao;
import cn.itcast.domain.Customer;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)//声明spring 容器的 测试环境
@SpringBootTest(classes = SpringBootJpaApplication.class)//声明测试的启动类
public class JpaTest {

    @Autowired
    private CustomerDao customerDao;
    /**
     * spring boot 整合 spring data jpa
     * 测试 添加数据
     */
    @Test
    public void testSave(){

        Customer customer=new Customer();
        customer.setCustName("传智");
        customer.setCustAddress("西安");
        customer.setCustIndustry("IT行业");
        customerDao.save(customer);


    }
}

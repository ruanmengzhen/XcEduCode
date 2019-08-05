package cn.itcast;

import cn.itcast.pojo.Customer;
import cn.itcast.util.JpaUtil;
import org.junit.Test;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

/**
 * 使用jpa进行增删改查的操作
 */
public class TestJpa {


    //给数据库表 增加数据
    @Test
    public void testSave(){
        //1.加载配置文件创建实体管理器工厂对象
        EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("myJpa");
        //2.通过实体管理器工行获取实体管理器对象
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        //3.通过实体管理器对象获取事务对象，开启事务
        EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();//开启事务
        //4.完成增加操作给表中的添加一条数据
        //创建实体类对象
        Customer customer = new Customer();
        customer.setCustName("传智播客");
        customer.setCustAddress("北京");
        customer.setCustIndustry("IT行业");//所属行业

        entityManager.persist(customer);
        //5.提交事务
        transaction.commit();
        //6.释放资源
        entityManager.close();
        entityManagerFactory.close();

    }

    //查询表中的数据
    @Test
    public void testFind(){
        //1.通过JPA工具类获取实体管理器对象
        EntityManager em = JpaUtil.getEntityManager();
        //3.通过实体管理器对象获取事务对象
        EntityTransaction tx = em.getTransaction();
        //4.开启事务
        tx.begin();
        //5.完成查询操作

        Customer customer = em.find(Customer.class, 2l);//查询对象
        System.out.println(customer);
//6、提交事务
        tx.commit();
        //7.释放资源
        em.close();

    }






}

package cn.itcast.dao;

import cn.itcast.domain.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * dao的接口需要继承
 * JpaRepository<T,ID> ：完成基本的CRUD 的操作
 *  JpaSpecificationExecutor<T>：完成复杂查询
 *     T:表示实体类类型
 *     ID：主键的类型
 *
 */
public interface CustomerDao extends JpaRepository<Customer,Long>,JpaSpecificationExecutor<Customer> {

    /**
     * 案例：根据客户名称和客户id查询客户
     * sql:select * from cust_customer where cust_name=? and cust_id=?
     * jpql:from Customer where custName=? and custId=?
     *
     * 当有多个占位符的参数时,占位符的位置要和参数的位置一直
     * 也可以根据索引的方式，来指定占位符的位置
     */
    @Query(value = "from Customer where custName=? and custId=?")
    public List<Customer> findByCustNameAndId(String custName,Long id);


    /**
     * 使用jpql完成更新操作
     *  案例 ： 根据id更新，客户的名称
     *    更新2号客户的名称，将名称改为“黑马程序员”
     *    sql: update cust_customer set cust_name=? where cust_id=?
     *    jpql:update Customer set custName=?2 where custId=?1
     *
     * @return
     */
    @Query(value = "update Customer set custName=?2 where custId=?1")
    @Modifying//表示当前执行的是更新操作
    public void updateByIdAndCustName(Long id,String custName);

    /**
     *使用sql语句进行查询
     *sql语句：select * from cust_customer
     * nativeQuery:是否本地查询
     */
    @Query(value = "select * from cst_customer" ,nativeQuery = true)
    public List<Object[]> findSQl();




    /**
     * 按照spring Data jpa 的命名规则定义查询方法名称
     * 命名规则：
     *  简单查询：    findBy+属性名(首字母大写)+查询方式
     *              当没有查询方式时 默认等值匹配
     *  多条件查询： findBy+属性名(首字母大写)+查询方式+条连接符(and|or)+属性名(首字母大写)+查询方式+…………
     */

    //根据客户名称查询
    public Customer findByCustName(String custName);

    //根据客户名称模糊查询
    public List<Customer> findByCustNameLike(String custName);

    //使用客户名称模糊匹配和客户所属行业精准匹配的查询
    public List<Customer> findByCustNameLikeAndAndCustIndustry(String custName,String custIndustry);










}

package cn.itcast.dao;

import cn.itcast.domain.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

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

}

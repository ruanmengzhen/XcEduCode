package cn.itcast.pojo;

import javax.persistence.*;
import java.io.Serializable;

/**
 * 客户表的实体类
 *
 */
@Entity//表示是个实体类
@Table(name = "cst_customer")//表示实体类与数据库表的映射关系，name指数据库表的名称
public class Customer implements Serializable {

    @Id//指定当前字段是主键
    @GeneratedValue(strategy = GenerationType.IDENTITY)//表示主键的生成策略，GebertionType.IDENTITY表示自增
    @Column(name="cust_id")
    private Long custId;//客户编号(主键)

    @Column(name="cust_name")  //@Column: 表示属性和表中字段的映射关系
    private String custName;//客户名称(公司名称)

    @Column(name="cust_source")
    private String custSource;//客户信息来源

    @Column(name="cust_industry")
    private String custIndustry;//客户所属行业

    @Column(name="cust_level")
    private String custLevel;//客户级别

    @Column(name="cust_address")
    private String custAddress;//客户联系地址

    @Column(name="cust_phone")
    private String custPhone;//客户联系电话

    public Customer() {
    }

    public Customer(Long custId, String custName, String custSource, String custIndustry, String custLevel, String custAddress, String custPhone) {
        this.custId = custId;
        this.custName = custName;
        this.custSource = custSource;
        this.custIndustry = custIndustry;
        this.custLevel = custLevel;
        this.custAddress = custAddress;
        this.custPhone = custPhone;
    }

    public Long getCustId() {
        return custId;
    }

    public void setCustId(Long custId) {
        this.custId = custId;
    }

    public String getCustName() {
        return custName;
    }

    public void setCustName(String custName) {
        this.custName = custName;
    }

    public String getCustSource() {
        return custSource;
    }

    public void setCustSource(String custSource) {
        this.custSource = custSource;
    }

    public String getCustIndustry() {
        return custIndustry;
    }

    public void setCustIndustry(String custIndustry) {
        this.custIndustry = custIndustry;
    }

    public String getCustLevel() {
        return custLevel;
    }

    public void setCustLevel(String custLevel) {
        this.custLevel = custLevel;
    }

    public String getCustAddress() {
        return custAddress;
    }

    public void setCustAddress(String custAddress) {
        this.custAddress = custAddress;
    }

    public String getCustPhone() {
        return custPhone;
    }

    public void setCustPhone(String custPhone) {
        this.custPhone = custPhone;
    }

    @Override
    public String toString() {
        return "Customer{" +
                "custId=" + custId +
                ", custName='" + custName + '\'' +
                ", custSource='" + custSource + '\'' +
                ", custIndustry='" + custIndustry + '\'' +
                ", custLevel='" + custLevel + '\'' +
                ", custAddress='" + custAddress + '\'' +
                ", custPhone='" + custPhone + '\'' +
                '}';
    }
}

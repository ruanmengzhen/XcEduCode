package cn.itcast.util;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

/**
 * jpa 的工具类
 */
public class JpaUtil {
    private static EntityManagerFactory entityManagerFactory;

    static {
        entityManagerFactory= Persistence.createEntityManagerFactory("myJpa");
    }

    //获取实体管理器对象
    public static EntityManager getEntityManager(){
        return entityManagerFactory.createEntityManager();
    }

}

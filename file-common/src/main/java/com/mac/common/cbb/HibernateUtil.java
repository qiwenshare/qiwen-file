package com.mac.common.cbb;

import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;


/**
 * Created by Administrator on 2016/9/10.
 */
public class HibernateUtil {
    private static HibernateUtil me;
    private SessionFactory sessionFactory;

    private HibernateUtil() {
        StandardServiceRegistry registry = new StandardServiceRegistryBuilder().configure().build();
        sessionFactory = new MetadataSources(registry).buildMetadata().buildSessionFactory();
    }

    public static HibernateUtil getInstance() {
        if (me == null) {
            me = new HibernateUtil();
        }
        return me;
    }

    public SessionFactory getSessionFactory() {
        return this.sessionFactory;
    }
}

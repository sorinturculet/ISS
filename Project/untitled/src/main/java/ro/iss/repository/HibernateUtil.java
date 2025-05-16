package ro.iss.repository;

import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import ro.iss.domain.*;

public class HibernateUtil {

    private static final SessionFactory sessionFactory;

    static {
        try {
            // Load config from hibernate.cfg.xml
            Configuration configuration = new Configuration().configure();

            // Register annotated entity classes
            configuration.addAnnotatedClass(User.class);
            configuration.addAnnotatedClass(Drug.class);
            configuration.addAnnotatedClass(Order.class);
            configuration.addAnnotatedClass(OrderItem.class);

            // Build ServiceRegistry & SessionFactory
            ServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder()
                    .applySettings(configuration.getProperties())
                    .build();

            sessionFactory = configuration.buildSessionFactory(serviceRegistry);
        } catch (Throwable ex) {
            System.err.println("Initial SessionFactory creation failed: " + ex);
            throw new ExceptionInInitializerError(ex);
        }
    }

    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }
}

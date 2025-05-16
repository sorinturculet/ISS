package ro.iss.repository;

import org.hibernate.Session;
import org.hibernate.Transaction;
import ro.iss.domain.Order;

import java.util.List;

public class OrderRepository {

    public void save(Order order) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();
            session.persist(order);
            tx.commit();
        }
    }

    public Order findById(int id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(Order.class, id);
        }
    }

    public List<Order> findAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM Order", Order.class).list();
        }
    }

    public void update(Order order) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();
            session.merge(order);
            tx.commit();
        }
    }

    public void delete(Order order) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();
            session.remove(order);
            tx.commit();
        }
    }
}

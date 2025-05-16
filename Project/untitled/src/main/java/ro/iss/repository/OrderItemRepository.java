package ro.iss.repository;

import org.hibernate.Session;
import org.hibernate.Transaction;
import ro.iss.domain.OrderItem;

import java.util.List;

public class OrderItemRepository {

    public void save(OrderItem orderItem) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();
            session.persist(orderItem);
            tx.commit();
        }
    }

    public OrderItem findById(int id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(OrderItem.class, id);
        }
    }

    public List<OrderItem> findAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM OrderItem", OrderItem.class).list();
        }
    }

    public void update(OrderItem orderItem) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();
            session.merge(orderItem);
            tx.commit();
        }
    }

    public void delete(OrderItem orderItem) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();
            session.remove(orderItem);
            tx.commit();
        }
    }
}

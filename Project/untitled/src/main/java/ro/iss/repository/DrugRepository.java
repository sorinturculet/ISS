package ro.iss.repository;

import org.hibernate.Session;
import org.hibernate.Transaction;
import ro.iss.domain.Drug;

import java.util.List;

public class DrugRepository {

    public void save(Drug drug) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();
            session.persist(drug);
            tx.commit();
        }
    }

    public Drug findById(int id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(Drug.class, id);
        }
    }

    public List<Drug> findAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM Drug", Drug.class).list();
        }
    }

    public void update(Drug drug) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();
            session.merge(drug);
            tx.commit();
        }
    }

    public void delete(Drug drug) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();
            session.remove(drug);
            tx.commit();
        }
    }
}

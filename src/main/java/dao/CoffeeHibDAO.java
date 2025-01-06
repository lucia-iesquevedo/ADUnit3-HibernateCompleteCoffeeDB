package dao;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.PersistenceException;
import lombok.extern.log4j.Log4j2;
import model.Coffee;
import model.EncriptedCode;

import java.util.List;

@Log4j2
@ApplicationScoped
public class CoffeeHibDAO {
    private final JPAUtil jpautil;
    private EntityManager em;

    @Inject
    public CoffeeHibDAO(JPAUtil hu) {
        this.jpautil = hu;
    }

    public List<Coffee> getAll() {
        List<Coffee> list;
        try {
            em = jpautil.getEntityManager();
            list = em.createQuery("from Coffee", Coffee.class).getResultList();
        } finally {
            if (em != null) em.close();
        }

        return list;
    }

    public Coffee get(Coffee coffee) {
        if (coffee.getId() != null) {
            coffee = getByID(coffee.getId());
        } else if (coffee.getCofName() != null) {
            coffee = getByName(coffee.getCofName());
        }
        return coffee;
    }
    private Coffee getByID(int id) {
        Coffee coffee;
        em = jpautil.getEntityManager();
        try {
            coffee = em.find(Coffee.class, id);
        } finally {
            if (em != null) em.close();
        }
        return coffee;
    }

    private Coffee getByName(String name) {
        Coffee coffee;
        em = jpautil.getEntityManager();
        try {
//            coffee = em
//                    .createNamedQuery("hql_get_coffee_by_name", Coffee.class)
//                    .setParameter("name", name)
//                    .getSingleResult();

//If name is duplicated, returns the first occurrence
            coffee = em
                    .createNamedQuery("hql_get_coffee_by_name", Coffee.class)
                    .setParameter("name", name)
                    .getResultList().get(0);
        } finally {
            if (em != null) em.close();
        }

        return coffee;
    }

    public void add(Coffee coffee) {

        em = jpautil.getEntityManager();
        EntityTransaction tx = null;

        try {
            tx = em.getTransaction();
            tx.begin();
            EncriptedCode ec = coffee.getEncriptedCode();
            ec.setCoffee(coffee);
            em.persist(coffee);
            tx.commit();
        }catch (PersistenceException pe) {
            if (tx != null && tx.isActive()) tx.rollback();
            System.out.println("Supplier does not exist");
        }   catch (Exception e) {
                if (tx != null && tx.isActive()) tx.rollback();
 //               if (e.getCause() instanceof TransientPropertyValueException)  //Supplier does not exist
 //                   System.out.println("Supplier does not exist");
 //               else
                    log.error("Undefined error", e);
        } finally {
            if (em != null) em.close();
        }
    }

    public void delete(Coffee coffee) {
        //With cascade.REMOVE
        em = jpautil.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        tx.begin();
        try {
            //Reattach the object before removing
            em.remove(em.merge(coffee));
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            e.printStackTrace();

        } finally {
            if (em != null) em.close();
        }
    }
}

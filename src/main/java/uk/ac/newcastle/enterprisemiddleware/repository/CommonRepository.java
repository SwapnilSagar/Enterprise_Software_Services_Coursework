package uk.ac.newcastle.enterprisemiddleware.repository;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.validation.ConstraintViolationException;
import java.util.List;
import java.util.Map;

/**
 * @author Swapnil Sagar
 * */
public class CommonRepository<T, ID> {

    @Inject
    EntityManager entityManager;

    private final Class<T> entityClass;



    public CommonRepository(Class<T> entityClass) {
        this.entityClass = entityClass;
    }

    /**
     * @param t The t object to be persisted
     * @return The T object that has been persisted
     * @throws ConstraintViolationException, ValidationException, Exception
     * */
    public T create(T t)  throws Exception {
        // Write the t to the database.
        entityManager.persist(t);
        return t;
    }



    public List<T> getAllRecords(){
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<T> criteriaQuery = criteriaBuilder.createQuery(entityClass);
        criteriaQuery.select(criteriaQuery.from(entityClass));
        return entityManager.createQuery(criteriaQuery).getResultList();
    }

    /**
     * Generic, SQL-injection-safe query method
     * @param jpql JPQL string with named parameters
     * @param params key–value map of parameters to bind
     */
    public List<T> getAllRelatedRecords(String jpql, Map<String, Object> params){
        TypedQuery<T> query = entityManager.createQuery(jpql, entityClass);
        if(params != null){
            params.forEach(query::setParameter);
        }


        return query.getResultList();
    }

    public T getRecordById(ID id){
        return entityManager.find(entityClass, id);
    }



    /**
     * Finds a single record by a specific field and value.
     * @param fieldName The name of the field in the entity (e.g., "email" or "phoneNumber")
     * @param value The value to search for
     * @return The found entity, or null if not found
     */
    public T getRecordByField(String fieldName, Object value)
    {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<T> cq = cb.createQuery(entityClass);
        Root<T> root = cq.from(entityClass);
        cq.select(root).where(cb.equal(root.get(fieldName), value));

        try {
            return entityManager.createQuery(cq).getSingleResult();
        } catch (NoResultException e) {
            return null; // Return null if no record is found
        }


    }

    public T update(T t){
        entityManager.merge(t);
        return t;
    }



    public T delete(T t) {
        entityManager.remove(entityManager.merge(t));
        return t;
    }

}

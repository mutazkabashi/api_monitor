package com.kry.apimonitor.repository;

import com.kry.apimonitor.domain.BaseModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static javax.persistence.Persistence.createEntityManagerFactory;


/**
 * Base Repository which holds the common Repository functionality
 *
 * @author Mutaz Hussein Kabashi
 * @version 1.0
 */
public abstract class DataBaseRepository<T extends BaseModel> {

    protected static EntityManagerFactory factory;
    protected final Class<T> clazz = getModelType();
    static final Logger LOGGER = LoggerFactory.getLogger(DataBaseRepository.class);

    public void init(String persistenceUnitName) {
        try {
            if (factory == null) {
                factory = Persistence.createEntityManagerFactory(persistenceUnitName);
            }
        } catch (Exception ex) {
            LOGGER.error("JPA facttory initialization operation failed with the following exception ", ex.getCause());
        }
    }


    /**
     * This Method fetchs all records from the database (work like Select * from tablename)
     *
     * @return fetched records from the database as a list of Java Objects (POJO)
     */
    public List<T> findAll() {
        LOGGER.debug("get all web services from db ");
        List<T> result = new ArrayList<>();
        EntityManager em = factory.createEntityManager();
        try {
            result = em.createQuery(getFindAllQuery(), clazz).getResultList();
            LOGGER.debug("get all web services from db ");
        } catch (Exception ex) {
            LOGGER.error("findAll operation failed with the following exception ", ex.getCause());
            throw new RepositoryException("findAll operation failed with the following exception ", ex.getCause());
        }
         finally{
            em.close();
        }
        return result;

    }

    /**
     * abstarct method, which should be implemented by the subclass to specify the SqlQuery for FindAll
     *
     * @return FindAll's Sql query
     */
    public abstract String getFindAllQuery();

    /**
     * abstarct method , which we use to specify the Classs of the Model/Pojo
     * FIXME we should use Generic instead
     */
    public abstract Class<T> getModelType();

    /**
     * This Method fetchs all records from the database using the passed  query
     *
     * @param JPA query to be used to fetch data from the database
     * @return fetched records from the database as a list of Java Objects (POJO)
     */
    public List<T> findByQuery(String query) {
        LOGGER.debug("get records from db  based on JPA query");
        List<T> result = new ArrayList<>();
        EntityManager em = factory.createEntityManager();
        try {
            result = em.createQuery(query, clazz).getResultList();
        } catch (Exception ex) {
            LOGGER.error("findByQuery operation failed with the following exception ", ex.getCause());
            throw new RepositoryException("findByQuery operation failed with the following exception ", ex.getCause());
        } finally {
            em.close();
        }
        LOGGER.debug("get records from db  based on JPA query finished successfully ");
        return result;

    }

    /**
     * This Method fetchs all records from the database using the passed simple query
     * with one parameter.
     *
     * @param JPL       query to be used to fetch data from the database
     * @param parameter : Query parameter (where condition)
     * @param value     : paramter's value
     * @return fetched records from the database as a list of Java Objects (POJO)
     */
    public List<T> findByQueryAndParameter(String query, String parameter, Object value) {
        LOGGER.debug("get records from db  based on JPA query");
        List<T> result = new ArrayList<>();
        EntityManager em = factory.createEntityManager();
        try {
            result = em.createQuery(query, clazz).setParameter(parameter, value)
                    .getResultList();
            LOGGER.debug("get records from db  based on JPA query finished successfully ");
        } catch (Exception ex) {
            LOGGER.error("findByQueryAndParameter operation failed with the following exception ", ex.getCause());
            throw new RepositoryException("findByQueryAndParameter operation failed with the following exception ", ex.getCause());
        } finally {
            em.close();
        }
        return result;

    }

    /**
     * Insert a Record (java Object POJO) into Databse
     *
     * @param record a Java object to be instered into database
     */
    public void insert(T record) {
        LOGGER.debug("insert new record " + record);
        EntityManager em = factory.createEntityManager();
        em.getTransaction().begin();
        try {
            em.persist(record);
            em.getTransaction().commit();
            LOGGER.debug("record inserted succufully");
        } catch (Exception ex) {
            em.getTransaction().rollback();
            LOGGER.error("insertion operation failed with the following exception ", ex.getCause());
            throw new RepositoryException("insertion operation failed with the following exception ", ex);
        } finally {
            em.close();
        }

    }

    /**
     * Update a record in the databse
     *
     * @param record a Java object to be updated into the databse
     */
    public void update(BaseModel record) {
        LOGGER.debug("update a  record " + record);

        EntityManager em = factory.createEntityManager();
        em.getTransaction().begin();
        record.setLastModifiedDate(Instant.now());
        try {
            em.merge(record);
            em.getTransaction().commit();
            LOGGER.debug("record updated successfully");

        } catch (Exception ex) {
            LOGGER.error("update operation failed with the following exception ", ex.getCause());
            throw new RepositoryException("update operation failed with the following exception ", ex.getCause());
        } finally {
            em.close();
        }
    }

    /**
     * delete a record from the datbase
     *
     * @param query
     */
    public void deleteByQuery(String query) {
        LOGGER.debug("delete a  record with ");

        EntityManager em = factory.createEntityManager();
        em.getTransaction().begin();
        try {
            em.createQuery(query).executeUpdate();
            em.getTransaction().commit();
            LOGGER.debug("record deleted successfully");
        } catch (Exception ex) {
            LOGGER.error("delete operation failed with the following exception ", ex.getCause());
            throw new RepositoryException("delete operation failed with the following exception ", ex.getCause());
        } finally {
            em.close();
        }

    }

    public void close() {
        if (factory.isOpen()) {
            factory.close();
        }
    }


}

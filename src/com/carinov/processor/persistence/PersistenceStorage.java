package com.carinov.processor.persistence;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Query;

import com.carinov.processor.ProcessorData;

public class PersistenceStorage {
	private static PersistenceStorage storage;
	private static Object lock = new Object();
	private EntityManagerFactory entityManagerFactory;
	private EntityManager em;

	private PersistenceStorage() {
		try {
//			entityManagerFactory =  Persistence.createEntityManagerFactory("ProcessorData");
//			em = entityManagerFactory.createEntityManager();
		} catch(Throwable ex) {
			ex.printStackTrace();
		}
	}

	@Override
	protected void finalize() throws Throwable {
		em.close();
		entityManagerFactory.close();
		super.finalize();
	}

	public static PersistenceStorage getStorage() {
		if(storage == null)
			init();
		return storage;
	}

	public static void init() {
		if(storage == null) {
			synchronized (lock) {
				if(storage == null)
					storage = new PersistenceStorage();
			}
		}
	}

	public void store(ProcessorData data) {
		em.persist(data);
	}
	
	public void delete(ProcessorData data) {
//		em.remove(data);
	}

	public <T> EntityTransaction transactionStore(ProcessorData data) {
		EntityTransaction userTransaction = em.getTransaction();
		if(userTransaction != null) {
			userTransaction.begin(); {
				em.persist(data);
			}
		}
		return userTransaction;
	}

	public void commit(EntityTransaction userTransaction) {
		userTransaction.commit();
	}

	public void rollback(EntityTransaction userTransaction) {
		userTransaction.rollback();
	}

	public boolean hasData() {
		Query qry = em.createQuery("SELECT p.requestId FROM ProcessorData p");
		return qry.getResultList().size() > 0;
	}
}
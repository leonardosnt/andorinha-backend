package repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

public abstract class AbstractCrudRepository {

	@PersistenceContext
	protected EntityManager em;

}

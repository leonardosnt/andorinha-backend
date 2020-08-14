package repository;

import java.lang.reflect.ParameterizedType;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import repository.base.EntityQuery;
import repository.base.TupleQuery;

public abstract class AbstractCrudRepository<T> {

	@PersistenceContext
	protected EntityManager em;

	protected Class<T> persistentClass;

	@SuppressWarnings("unchecked")
	@PostConstruct
	private void postConstruct() {
		this.persistentClass = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
	}

	public void inserir(T entidade) {
		this.em.persist(entidade);
	}

	public void atualizar(T entidade) {
		this.em.merge(entidade);
	}

	public void remover(int id) {
		T usuario = consultar(id);
		if (usuario != null) {
			this.em.remove(usuario);
		}
	}

	public List<T> listarTodos()  {
		return this.em.createQuery("SELECT t FROM " + this.persistentClass.getName() + " t", this.persistentClass).getResultList();
	}

	public T consultar(int id) {
		return this.em.find(this.persistentClass, id);
	}

	protected EntityQuery<T> createEntityQuery() {
		return EntityQuery.create(this.em, this.persistentClass);
	}

	protected EntityQuery<T> createCountQuery() {
		return EntityQuery.createCount(this.em, this.persistentClass);
	}

	protected TupleQuery<T> createTupleQuery() {
		return TupleQuery.create(this.em, this.persistentClass);
	}
}

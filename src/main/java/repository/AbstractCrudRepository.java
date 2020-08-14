package repository;

import java.lang.reflect.ParameterizedType;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

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

	public T consultar(int id) {
		return this.em.find(this.persistentClass, id);
	}
}

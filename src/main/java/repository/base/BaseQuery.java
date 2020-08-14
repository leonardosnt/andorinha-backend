package repository.base;

import java.sql.Date;
import java.time.ZonedDateTime;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import javax.persistence.TemporalType;
import javax.persistence.criteria.Predicate;

public interface BaseQuery<E> {

	List<?> list();

	BaseQuery<E> innerJoinFetch(String attribute);

	BaseQuery<E> leftJoinFetch(String attribute);

	BaseQuery<E> leftJoin(String attribute);

	BaseQuery<E> addOrderBy(String type, String path);

	BaseQuery<E> addAscendingOrderBy(String path);

	BaseQuery<E> addDescendingOrderBy(String path);

	BaseQuery<E> setFirstResult(Integer firstResult);

	BaseQuery<E> setMaxResults(Integer maxResults);

	BaseQuery<E> objectEqualsTo(String path, Object value);

	BaseQuery<E> equal(String path, Object value);

	BaseQuery<E> equal(String path, Calendar value, TemporalType temporalType);

	BaseQuery<E> equal(String path, java.util.Date value, TemporalType temporalType);

	BaseQuery<E> notEqual(String path, Object value);

	BaseQuery<E> isEmpty(String path, Boolean executeFilter);

	BaseQuery<E> isNotEmpty(String path, Boolean executeFilter);

	BaseQuery<E> isMemberOf(String path, Object value);

	BaseQuery<E> isNull(String path);

	BaseQuery<E> isNull(String path, Boolean apply);

	BaseQuery<E> isNotNull(String path, Boolean apply);

	Optional<Predicate> objectEqualsToPredicate(String path, Object value);

	BaseQuery<E> like(String path, String value);

	BaseQuery<E> likeIgnoreCase(String path, String value);

	BaseQuery<E> addInDisjunction(Optional<Predicate>... optionalPredicates);

	BaseQuery<E> stringEqualsTo(String path, String value);

	BaseQuery<E> lessThanOrEqualsTo(String path, java.util.Date data, Boolean apply);

	BaseQuery<E> greaterThanOrEqualsTo(String path, java.util.Date data, Boolean apply);

	BaseQuery<E> greaterThanOrEqualsTo(String path, Comparable<?> comparable);

	BaseQuery<E> lessThanOrEqualsTo(String path, Comparable<?> comparable);

	BaseQuery<E> between(String path, Date firstDate, Date secondDate);

	BaseQuery<E> between(String path, ZonedDateTime firstDate, ZonedDateTime secondDate);

	BaseQuery<E> between(String path, java.util.Date firstDate, java.util.Date secondDate);

	BaseQuery<E> in(String path, Collection<?> collection);

	BaseQuery<E> apply(Consumer<BaseQuery<E>> consumer);

	<T> BaseQuery<E> apply(BiConsumer<BaseQuery<E>, T> consumer, T obj);

}
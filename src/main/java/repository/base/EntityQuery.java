package repository.base;

import java.sql.Date;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.TemporalType;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang3.StringUtils;

public class EntityQuery<E> implements BaseQuery<E> {

    private final EntityManager entityManager;
    private final Class<E> entityClass;

    private final CriteriaBuilder criteriaBuilder;
    private final CriteriaQuery<E> criteriaQuery;
    private final CriteriaQuery<Long> criteriaCountQuery;

    private final Root<E> root;
    private final List<Predicate> predicates = new ArrayList<>();

    private Integer firstResult;
    private Integer maxResults;

    private List<Order> orders = new ArrayList<>();

    private EntityQuery(EntityManager entityManager, Class<E> entityClass, boolean isCount) {
        this.entityManager = entityManager;
        this.entityClass = entityClass;
        this.criteriaBuilder = entityManager.getCriteriaBuilder();
        if (isCount){
            this.criteriaQuery = null;
            this.criteriaCountQuery = criteriaBuilder.createQuery(Long.class);
            this.root = criteriaCountQuery.from(entityClass);
        }
        else {
            this.criteriaCountQuery = null;
            this.criteriaQuery = criteriaBuilder.createQuery(entityClass);
            this.root = criteriaQuery.from(entityClass);
        }
    }

    public static <T> EntityQuery<T> create(EntityManager entityManager, Class<T> entityClass) {
        return new EntityQuery<>(entityManager, entityClass, false);
    }

    public static <T> EntityQuery<T> createCount(EntityManager entityManager, Class<T> entityClass) {
        return new EntityQuery<>(entityManager, entityClass, true);
    }

    @Override
    public List<E> list() {
        TypedQuery<E> typedQuery = prepareSelectTypedQuery();

        if (firstResult != null) {
            typedQuery.setFirstResult(firstResult);
        }

        if (maxResults != null) {
            typedQuery.setMaxResults(maxResults);
        }

        return typedQuery.getResultList();
    }

    public E uniqueResult() {
        TypedQuery<E> typedQuery = prepareSelectTypedQuery();
        return typedQuery.getSingleResult();
    }

    public long count() {
        TypedQuery<Long> typedQuery = prepareCountTypedQuery();
        return typedQuery.getSingleResult();
    }

    private TypedQuery<Long> prepareCountTypedQuery() {
        this.criteriaCountQuery.select(criteriaBuilder.count(root));
        this.criteriaCountQuery.where(predicates.toArray(new Predicate[predicates.size()]));
        return entityManager.createQuery(this.criteriaCountQuery);
    }

    private TypedQuery<E> prepareSelectTypedQuery() {
        criteriaQuery.select(root);
        criteriaQuery.where(predicates.toArray(new Predicate[predicates.size()])).orderBy(orders);
        return entityManager.createQuery(criteriaQuery);
    }

    @Override
    public EntityQuery<E> innerJoinFetch(String attribute) {
        root.fetch(attribute, JoinType.INNER);
        return this;
    }

    @Override
    public EntityQuery<E> leftJoinFetch(String attribute) {
        root.fetch(attribute, JoinType.LEFT);
        return this;
    }

    @Override
    public EntityQuery<E> leftJoin(String attribute) {
        root.join(attribute, JoinType.LEFT);
        return this;
    }

    @Override
    public EntityQuery<E> addOrderBy(String type, String path) {
        if (type != null && path != null){
            if ("asc".equalsIgnoreCase(type)){
                this.addAscendingOrderBy(path);
            }
            else{
                this.addDescendingOrderBy(path);
            }
        }
        return this;
    }

    @Override
    public EntityQuery<E> addAscendingOrderBy(String path) {
        orders.add(criteriaBuilder.asc(toJpaPath(path)));
        return this;
    }

    @Override
    public EntityQuery<E> addDescendingOrderBy(String path) {
        orders.add(criteriaBuilder.desc(toJpaPath(path)));
        return this;
    }

    @Override
    public EntityQuery<E> setFirstResult(Integer firstResult) {
        this.firstResult = firstResult;
        return this;
    }

    @Override
    public EntityQuery<E> setMaxResults(Integer maxResults) {
        this.maxResults = maxResults;
        return this;
    }

    @Override
    public EntityQuery<E> objectEqualsTo(String path, Object value) {
        if (value != null) {
            addEqualPredicate(path, value);
        }
        return this;
    }

    @Override
    public EntityQuery<E> equal(String path, Object value) {
        if (value != null) {
            addEqualPredicate(path, value);
        }
        return this;
    }

    @Override
    public EntityQuery<E> equal(String path, Calendar value, TemporalType temporalType) {
        if (value != null) {
            addEqualPredicate(path, value.getTime(), temporalType);
        }
        return this;
    }

    @Override
    public EntityQuery<E> equal(String path, java.util.Date value, TemporalType temporalType) {
        if (value != null) {
            addEqualPredicate(path, value, temporalType);
        }
        return this;
    }

    @Override
    public EntityQuery<E> notEqual(String path, Object value) {
        if (value != null) {
            addNotEqualPredicate(path, value);
        }
        return this;
    }


    @Override
    public EntityQuery<E> isEmpty(String path, Boolean executeFilter) {
        if (executeFilter != null && executeFilter.booleanValue() ) {
            addEmptyPredicate(path);
        }
        return this;
    }

    @Override
    public EntityQuery<E> isNotEmpty(String path, Boolean executeFilter) {
        if (executeFilter != null && executeFilter.booleanValue() ) {
            addNotEmptyPredicate(path);
        }
        return this;
    }

    @Override
    public EntityQuery<E> isMemberOf(String path, Object value) {
        if (value != null ) {
            addIsMemberOfPredicate(path, value);
        }
        return this;
    }

    @Override
    public EntityQuery<E> isNull(String path) {
        addIsNullPredicate(path);
        return this;
    }

    @Override
    public EntityQuery<E> isNull(String path, Boolean apply) {
        if (apply != null && apply) {
            addIsNullPredicate(path);
        }
        return this;
    }

    @Override
    public EntityQuery<E> isNotNull(String path, Boolean apply) {
        if (apply != null && apply) {
            addIsNotNullPredicate(path);
        }
        return this;
    }

    @Override
    public Optional<Predicate> objectEqualsToPredicate(String path, Object value) {
        if (value != null) {
            return Optional.of(equalPredicate(path, value));
        }
        return Optional.empty();
    }

    @Override
    public EntityQuery<E> like(String path, String value) {
        if (StringUtils.isNotBlank(value)) {
            predicates.add(criteriaBuilder.like(toJpaPath(path), '%' + value + '%'));
        }
        return this;
    }

    @Override
    public EntityQuery<E> likeIgnoreCase(String path, String value) {
        if (StringUtils.isNotBlank(value)) {
            predicates.add(criteriaBuilder.like(criteriaBuilder.upper( toJpaPath(path) ), '%' + value.toUpperCase() + '%'));
        }
        return this;
    }

    @Override
    public EntityQuery<E> addInDisjunction(Optional<Predicate>... optionalPredicates) {
        List<Predicate> predicateList = Arrays.stream(optionalPredicates).filter(Optional::isPresent).map(Optional::get).collect(Collectors.toList());
        if (predicateList.size() > 1) {
            predicates.add(criteriaBuilder.or(predicateList.toArray(new Predicate[predicateList.size()])));
        } else if (predicateList.size() == 1) {
            predicates.add(predicateList.get(0));
        }
        return this;
    }

    @Override
    public EntityQuery<E> stringEqualsTo(String path, String value) {
        if (StringUtils.isNotBlank(value)) {
            addEqualPredicate(path, value);
        }
        return this;
    }

    @Override
    public EntityQuery<E> lessThanOrEqualsTo(String path, java.util.Date data, Boolean apply) {
        if (Objects.nonNull(data) && Objects.nonNull(apply) && apply) {
            predicates.add(criteriaBuilder.lessThanOrEqualTo(toJpaPath(path), data));
        }
        return this;
    }

    @Override
    public EntityQuery<E> greaterThanOrEqualsTo(String path, java.util.Date data, Boolean apply) {
        if (Objects.nonNull(data) && Objects.nonNull(apply) && apply) {
            predicates.add(criteriaBuilder.lessThanOrEqualTo(toJpaPath(path), data));
        }
        return this;
    }

    @Override
    public EntityQuery<E> greaterThanOrEqualsTo(String path, Comparable comparable) {
        if (Objects.nonNull(comparable)) {
            predicates.add(criteriaBuilder.greaterThanOrEqualTo(toJpaPath(path), comparable));
        }
        return this;
    }

    @Override
    public EntityQuery<E> lessThanOrEqualsTo(String path, Comparable comparable) {
        if (Objects.nonNull(comparable)) {
            predicates.add(criteriaBuilder.lessThanOrEqualTo(toJpaPath(path), comparable));
        }
        return this;
    }

    @Override
    public EntityQuery<E> between(String path, Date firstDate, Date secondDate) {
        if (Objects.nonNull(firstDate) && Objects.nonNull(secondDate)) {
            predicates.add(criteriaBuilder.between(toJpaPath(path), firstDate, secondDate));
        }
        return this;
    }

    @Override
    public EntityQuery<E> between(String path, ZonedDateTime firstDate, ZonedDateTime secondDate) {
        if (Objects.nonNull(firstDate) && Objects.nonNull(secondDate)) {
            predicates.add(criteriaBuilder.between(toJpaPath(path), firstDate, secondDate));
        }
        return this;
    }

    @Override
    public EntityQuery<E> between(String path, java.util.Date firstDate, java.util.Date secondDate) {
        if (Objects.nonNull(firstDate) && Objects.nonNull(secondDate)) {
            predicates.add(criteriaBuilder.between(toJpaPath(path), new Date( firstDate.getTime() ), new Date( secondDate.getTime() )));
        }
        return this;
    }

    @Override
    public EntityQuery<E> in(String path, Collection collection) {
        if (collection != null && !collection.isEmpty()) {
            predicates.add(criteriaBuilder.in(toJpaPath(path)).value(collection));
        }
        return this;
    }

    private void addEqualPredicate(String path, Object value) {
        predicates.add(equalPredicate(path, value));
    }

    private void addEqualPredicate(String path, java.util.Date value, TemporalType temporalType) {
        predicates.add(equalPredicate(path, value, temporalType));
    }

    private void addNotEqualPredicate(String path, Object value) {
        predicates.add(notEqualPredicate(path, value));
    }

    private void addNotEmptyPredicate(String path) {
        predicates.add(notEmptyPredicate(path));
    }

    private void addEmptyPredicate(String path) {
        predicates.add(emptyPredicate(path));
    }

    private void addIsNullPredicate(String path) {
        predicates.add(nullPredicate(path));
    }

    private void addIsNotNullPredicate(String path) {
        predicates.add(notNullPredicate(path));
    }

    private void addIsMemberOfPredicate(String path, Object value) {
        predicates.add(isMemberOfPredicate(path, value));
    }

    private Predicate equalPredicate(String path, Object value) {
        return criteriaBuilder.equal(toJpaPath(path), value);
    }

    private Predicate equalPredicate(String path, java.util.Date value, TemporalType temporalType) {
        if (temporalType == TemporalType.DATE){
            Expression<Date> function = criteriaBuilder.function("date", Date.class, toJpaPath(path));
            return criteriaBuilder.equal(function, value);
        }
        else {
            return criteriaBuilder.equal(toJpaPath(path), value);
        }
    }

    private Predicate notEqualPredicate(String path, Object value) {
        return criteriaBuilder.notEqual(toJpaPath(path), value);
    }

    private Predicate notEmptyPredicate(String path) {
        return criteriaBuilder.isNotEmpty(toJpaPath(path));
    }

    private Predicate emptyPredicate(String path) {
        return criteriaBuilder.isEmpty(toJpaPath(path));
    }

    private Predicate notNullPredicate(String path) {
        return criteriaBuilder.isNotNull(toJpaPath(path));
    }

    private Predicate nullPredicate(String path) {
        return criteriaBuilder.isNull(toJpaPath(path));
    }

    private Predicate isMemberOfPredicate(String path, Object value) {
        return criteriaBuilder.isMember(value, toJpaPath(path));
    }

    private <T> Path<T> toJpaPath(String stringPath) {
        String[] pathParts = StringUtils.split(stringPath, '.');

        assert pathParts != null && pathParts.length > 0 : "Path cannot be empty";

        Path<T> jpaPath = null;
        for (String eachPathPart : pathParts) {
            if (jpaPath == null) {
                jpaPath = root.get(eachPathPart);
            } else {
                jpaPath = jpaPath.get(eachPathPart);
            }
        }

        return jpaPath;
    }

    @Override
    public EntityQuery<E> apply(Consumer<BaseQuery<E>> consumer) {
        consumer.accept(this);
        return this;
    }

    @Override
    public <T> EntityQuery<E> apply(BiConsumer<BaseQuery<E>, T> consumer, T obj) {
        consumer.accept(this, obj);
        return this;
    }

}
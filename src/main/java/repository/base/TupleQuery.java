package repository.base;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.StringUtils;

import javax.persistence.*;
import javax.persistence.criteria.*;
import java.lang.reflect.InvocationTargetException;
import java.sql.Date;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class TupleQuery<E> implements BaseQuery<E> {

    private final EntityManager entityManager;
    private final Class<E> entityClass;

    private Class dtoClass;

    private final CriteriaBuilder criteriaBuilder;
    private final CriteriaQuery<Tuple> criteriaQuery;

    private List<String> selectPart;
    private List<String> groupByPart;

    private final Root<E> root;
    private List<Join> joins;
    private final List<Predicate> predicates = new ArrayList<>();

    private Integer firstResult;
    private Integer maxResults;

    private List<Order> orders = new ArrayList<>();

    private TupleQuery(EntityManager entityManager, Class<E> entityClass) {
        this.entityManager = entityManager;
        this.entityClass = entityClass;
        this.criteriaBuilder = entityManager.getCriteriaBuilder();
        this.criteriaQuery = criteriaBuilder.createTupleQuery();
        this.root = criteriaQuery.from(entityClass);
        this.selectPart = new ArrayList<>();
        this.groupByPart = new ArrayList<>();
        this.joins = new ArrayList<>();
    }

    public static <T> TupleQuery<T> create(EntityManager entityManager, Class<T> entityClass) {
        return new TupleQuery<>(entityManager, entityClass);
    }

    public TupleQuery<E> select(String ... select) {
        this.selectPart.addAll( Arrays.asList(select));
        return this;
    }

    @Override
    public List<Tuple> list() {
        TypedQuery<Tuple> typedQuery = prepareSelectTypedQuery();

        if (firstResult != null) {
            typedQuery.setFirstResult(firstResult);
        }

        if (maxResults != null) {
            typedQuery.setMaxResults(maxResults);
        }

        return typedQuery.getResultList();
    }

    public <T> List<T> list(Class<T> clazz) {
        List<Tuple> tuples = this.list();
        try {

            List<T> lista = new ArrayList<>();
            for ( Tuple t: tuples ) {
                lista.add( createAndPopulateBean(clazz, t) );
            }
            return lista;

        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new PersistenceException("Erro ao criar DTO", e);
        }


    }

    public Tuple uniqueResult() {
        TypedQuery<Tuple> typedQuery = prepareSelectTypedQuery();
        return typedQuery.getSingleResult();
    }

    private TypedQuery<Tuple> prepareSelectTypedQuery() {
        List<Selection> path = new ArrayList<>();
        for (String s: this.selectPart ) {
            path.add( toJpaPath(s).alias(s) );
        }

        criteriaQuery.multiselect(path.toArray(new Selection[path.size()]));
        criteriaQuery.where(predicates.toArray(new Predicate[predicates.size()]));

        if ( this.groupByPart.size() > 0 ){
            List<Expression<?>> pathGroup = new ArrayList<>();
            for (String s: this.groupByPart ) {
                pathGroup.add( toJpaPath(s) );
            }
            criteriaQuery.groupBy(pathGroup);
        }

        if (orders.size() > 0) {
            criteriaQuery.orderBy(orders);
        }

        return entityManager.createQuery(criteriaQuery);
    }

    @Override
    public TupleQuery<E> innerJoinFetch(String attribute) {
        root.fetch(attribute, JoinType.INNER);
        return this;
    }

    public TupleQuery<E> join(String attribute) {
        root.join(attribute);
        return this;
    }

    @Override
    public TupleQuery<E> leftJoin(String attribute) {
        root.join(attribute, JoinType.LEFT);
        return this;
    }

    public TupleQuery<E> rightJoin(String attribute) {
        root.join(attribute, JoinType.RIGHT);
        return this;
    }

    public TupleQuery<E> join(String attribute, String alias) {
        if(attribute.contains(".") ){
            String joinClass = attribute.split("\\.")[0];
            String joinAttr = attribute.split("\\.")[1];
            for (int i =0; i< this.joins.size(); i++){
                if (this.joins.get(i).getAlias().equalsIgnoreCase(joinClass)){
                    Join novo = this.joins.get(i).join(joinAttr);
                    novo.alias(alias);
                    this.joins.add(novo);
                    break;
                }
            }
        }
        else {
            Join j = root.join(attribute);
            j.alias(alias);
            this.joins.add(j);
        }

        return this;
    }

    @Override
    public TupleQuery<E> leftJoinFetch(String attribute) {
        root.fetch(attribute, JoinType.LEFT);
        return this;
    }
    public TupleQuery<E> rightJoinFetch(String attribute) {
        root.fetch(attribute, JoinType.RIGHT);
        return this;
    }

    @Override
    public TupleQuery<E> addOrderBy(String type, String path) {
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
    public TupleQuery<E> addAscendingOrderBy(String path) {
        if (path != null) {
            orders.add(criteriaBuilder.asc(toJpaPath(path)));
        }
        return this;
    }

    @Override
    public TupleQuery<E> addDescendingOrderBy(String path) {
        if (path != null) {
            orders.add(criteriaBuilder.desc(toJpaPath(path)));
        }
        return this;
    }

    @Override
    public TupleQuery<E> setFirstResult(Integer firstResult) {
        this.firstResult = firstResult;
        return this;
    }

    @Override
    public TupleQuery<E> setMaxResults(Integer maxResults) {
        this.maxResults = maxResults;
        return this;
    }

    @Override
    public TupleQuery<E> objectEqualsTo(String path, Object value) {
        if (value != null) {
            addEqualPredicate(path, value);
        }
        return this;
    }

    @Override
    public TupleQuery<E> equal(String path, Object value) {
        if (value != null) {
            addEqualPredicate(path, value);
        }
        return this;
    }

    @Override
    public TupleQuery<E> equal(String path, Calendar value, TemporalType temporalType) {
        if (value != null) {
            addEqualPredicate(path, value.getTime(), temporalType);
        }
        return this;
    }

    @Override
    public TupleQuery<E> equal(String path, java.util.Date value, TemporalType temporalType) {
        if (value != null) {
            addEqualPredicate(path, value, temporalType);
        }
        return this;
    }


    @Override
    public TupleQuery<E> notEqual(String path, Object value) {
        if (value != null) {
            addNotEqualPredicate(path, value);
        }
        return this;
    }

    @Override
    public TupleQuery<E> isEmpty(String path, Boolean executeFilter) {
        if (executeFilter != null && executeFilter.booleanValue() ) {
            addEmptyPredicate(path);
        }
        return this;
    }

    @Override
    public TupleQuery<E> isNull(String path) {
        addIsNullPredicate(path);
        return this;
    }

    @Override
    public TupleQuery<E> isNull(String path, Boolean apply) {
        if (apply != null && apply) {
            addIsNullPredicate(path);
        }
        return this;
    }

    @Override
    public TupleQuery<E> isNotNull(String path, Boolean apply) {
        if (apply != null && apply) {
            addIsNotNullPredicate(path);
        }
        return this;
    }

    @Override
    public TupleQuery<E> isMemberOf(String path, Object value) {
        if (value != null ) {
            addIsMemberOfPredicate(path, value);
        }
        return this;
    }

    @Override
    public TupleQuery<E> isNotEmpty(String path, Boolean executeFilter) {
        if (executeFilter != null && executeFilter.booleanValue() ) {
            addNotEmptyPredicate(path);
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
    public TupleQuery<E> like(String path, String value) {
        if (StringUtils.isNotBlank(value)) {
            predicates.add(criteriaBuilder.like(toJpaPath(path), '%' + value + '%'));
        }
        return this;
    }

    @Override
    public TupleQuery<E> likeIgnoreCase(String path, String value) {
        if (StringUtils.isNotBlank(value)) {
            predicates.add(criteriaBuilder.like(criteriaBuilder.upper( toJpaPath(path) ), '%' + value.toUpperCase() + '%'));
        }
        return this;
    }

    @Override
    public TupleQuery<E> addInDisjunction(Optional<Predicate>... optionalPredicates) {
        List<Predicate> predicateList = Arrays.stream(optionalPredicates).filter(Optional::isPresent).map(Optional::get).collect(Collectors.toList());
        if (predicateList.size() > 1) {
            predicates.add(criteriaBuilder.or(predicateList.toArray(new Predicate[predicateList.size()])));
        } else if (predicateList.size() == 1) {
            predicates.add(predicateList.get(0));
        }
        return this;
    }

    @Override
    public TupleQuery<E> stringEqualsTo(String path, String value) {
        if (StringUtils.isNotBlank(value)) {
            addEqualPredicate(path, value);
        }
        return this;
    }

    @Override
    public TupleQuery<E> lessThanOrEqualsTo(String path, java.util.Date data, Boolean apply) {
        if (Objects.nonNull(data) && Objects.nonNull(apply) && apply) {
            predicates.add(criteriaBuilder.lessThanOrEqualTo(toJpaPath(path), data));
        }
        return this;
    }

    @Override
    public TupleQuery<E> greaterThanOrEqualsTo(String path, java.util.Date data, Boolean apply) {
        if (Objects.nonNull(data) && Objects.nonNull(apply) && apply) {
            predicates.add(criteriaBuilder.lessThanOrEqualTo(toJpaPath(path), data));
        }
        return this;
    }

    @Override
    public TupleQuery<E> greaterThanOrEqualsTo(String path, Comparable comparable) {
        if (Objects.nonNull(comparable)) {
            predicates.add(criteriaBuilder.greaterThanOrEqualTo(toJpaPath(path), comparable));
        }
        return this;
    }

    @Override
    public TupleQuery<E> lessThanOrEqualsTo(String path, Comparable comparable) {
        if (Objects.nonNull(comparable)) {
            predicates.add(criteriaBuilder.lessThanOrEqualTo(toJpaPath(path), comparable));
        }
        return this;
    }

    @Override
    public TupleQuery<E> between(String path, Date firstDate, Date secondDate) {
        if (Objects.nonNull(firstDate) && Objects.nonNull(secondDate)) {
            predicates.add(criteriaBuilder.between(toJpaPath(path), firstDate, secondDate));
        }
        return this;
    }

    @Override
    public TupleQuery<E> between(String path, ZonedDateTime firstDate, ZonedDateTime secondDate) {
        if (Objects.nonNull(firstDate) && Objects.nonNull(secondDate)) {
            predicates.add(criteriaBuilder.between(toJpaPath(path), firstDate, secondDate));
        }
        return this;
    }

    @Override
    public TupleQuery<E> between(String path, java.util.Date firstDate, java.util.Date secondDate) {
        if (Objects.nonNull(firstDate) && Objects.nonNull(secondDate)) {
            predicates.add(criteriaBuilder.between(toJpaPath(path), new Date( firstDate.getTime() ), new Date( secondDate.getTime() )));
        }
        return this;
    }

    @Override
    public TupleQuery<E> in(String path, Collection collection) {
        if (collection != null && !collection.isEmpty()) {
            predicates.add(criteriaBuilder.in(toJpaPath(path)).value(collection));
        }
        return this;
    }

    public TupleQuery<E> groupBy(String ... groupBy) {
        this.groupByPart.addAll( Arrays.asList(groupBy) );
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
        //remove alias
        if ( stringPath.contains(" as") ){
            stringPath = stringPath.substring( 0, stringPath.indexOf(" as") );
        }
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

    public static <T> T createAndPopulateBean( Class<T> clazz, Tuple tuple ) throws InstantiationException, IllegalAccessException,
            InvocationTargetException, NoSuchMethodException {
        T bean = clazz.newInstance();

        for( TupleElement<?> e: tuple.getElements() ) {
            String name = e.getAlias();
            if( name.contains(" as ") ){
                name = name.substring( name.indexOf(" as") + 4 );
            }
            PropertyUtils.setSimpleProperty(bean, name, tuple.get(e));
        };

        return bean;
    }

    @Override
    public TupleQuery<E> apply(Consumer<BaseQuery<E>> consumer) {
        consumer.accept(this);
        return this;
    }

    @Override
    public <T> TupleQuery<E> apply(BiConsumer<BaseQuery<E>, T> consumer, T obj) {
        consumer.accept(this, obj);
        return this;
    }
}
package dbtest.core;

import java.util.HashSet;
import java.util.List;

import net.omikron.sql.template.Condition;
import dbtest.exception.NotOneResultReturnedException;
import dbtest.springProxy.DatabaseConnectionManager;

public class JdbcBeanDao<B> {

	private final BeanCreator<B> creator;
	private final BeanDeleter<B> deleter;
	private final BeanFetcher<B> fetcher;
	private final BeanUpdater<B> updater;
	private final SqlBeanClassDescriptor<B> descriptor;

	private final HashSet<Object> cache = new HashSet<>();

	public JdbcBeanDao(DatabaseConnectionManager db, Class<B> beanClass) {
		descriptor = new SqlBeanClassDescriptor<>(beanClass);
		creator = new BeanCreator<>(db, descriptor);
		deleter = new BeanDeleter<>(db, descriptor);
		fetcher = new BeanFetcher<>(db, descriptor);
		updater = new BeanUpdater<>(db, descriptor);
	}

	public B create() {
		B b = creator.createNewBean();
		pushToCache(b);
		return b;
	}

	public void deleteUnsafe(B b) {
		deleter.deleteBean(b);
		removeCached(b);
	}
	public void delete(B b) {
		if(isBeanCached(b)) {
			deleteUnsafe(b);
		}else{
			throw new IllegalArgumentException("bean is not managed by this DAO");
		}
	}

	public List<B> fetchAll() {
		List<B> b= fetcher.fetchAll();
		pushToCache(b);
		return b;
	}

	public B fetchById(Object id) throws NotOneResultReturnedException {
		B b = fetcher.fetchById(id);
		pushToCache(b);
		return b;
	}

	public List<B> fetchMultiByFieldEquals(String fieldName, Object comparision) {
		List<B> b = fetcher.fetchMultiByFieldEquals(fieldName, comparision);
		pushToCache(b);
		return b;
	}

	public B fetchSingleByFieldEquals(String fieldName, Object comparision) throws NotOneResultReturnedException {
		B b = fetcher.fetchSingleByFieldEquals(fieldName, comparision);
		pushToCache(b);
		return b;
	}

	public List<B> fetchMultiByFieldEquals(String[] fieldNames,
			Object[] comparisions) {
		List<B> b = fetcher.fetchMultiByFieldEquals(fieldNames, comparisions);
		pushToCache(b);
		return b;
	}

	public B fetchSingleByFieldsEquals(String[] fieldNames,
			Object[] comparisions) throws NotOneResultReturnedException {
		B b = fetcher.fetchSingleByFieldsEquals(fieldNames, comparisions);
		pushToCache(b);
		return b;
	}

	public List<B> fetchMultiByCondition(Condition[] conditions,
			Object[] substitudes) {
		List<B> b = fetcher.fetchMultiByCondition(conditions, substitudes);
		pushToCache(b);
		return b;
	}

	public B fetchSingleByCondition(Condition[] conditions, Object[] substitutes) throws NotOneResultReturnedException {
		B b = fetcher.fetchSingleByCondition(conditions, substitutes);
		pushToCache(b);
		return b;
	}
	
	public void update(B b) {
		if(isBeanCached(b)) {
			updateUnsafe(b);
		}else{
			throw new IllegalArgumentException("bean is not managed by this DAO");
		}
	}
	public void updateUnsafe(B b) {
		updater.saveChanges(b);
	}
	
	private void pushToCache(B b) {
		cache.add(descriptor.createProeprtyAccess(b).getIdPropertyValue());
	}
	private void pushToCache(List<B> bs) {
		for(B b : bs) {
			pushToCache(b);
		}
	}
	
	private boolean isBeanCached(B b)	 {
		return cache.contains(descriptor.createProeprtyAccess(b).getIdPropertyValue());
	}
	
	private void removeCached(B b) {
		cache.remove(descriptor.createProeprtyAccess(b).getIdPropertyValue());
	}

}

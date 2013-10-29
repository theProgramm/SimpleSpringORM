package dbtest.core;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import net.omikron.sql.template.Condition;

import org.springframework.jdbc.core.BeanPropertyRowMapper;

import dbtest.exception.NotOneResultReturnedException;
import dbtest.springProxy.AnnotatedBeanPropertyRowMapper;
import dbtest.springProxy.DatabaseConnectionManager;

/*
 * TODO Merge with other bean editors
 */
class BeanFetcher<B> {

	private final DatabaseConnectionManager db;
	private final SqlBeanClassDescriptor<B> beanClassDescription;
	private final BeanPropertyRowMapper<B> mapper;

	public BeanFetcher(DatabaseConnectionManager db,
			SqlBeanClassDescriptor<B> beanClassDescription) {
		this.db = db;
		this.beanClassDescription = beanClassDescription;
		this.mapper = AnnotatedBeanPropertyRowMapper
				.newInstance(beanClassDescription.getWrappedClass());
	}

	public List<B> fetchMulti(IStatementProvider provider) {
		List<B> b = db.getTemplate().query(provider, mapper);
		return b;
	}

	public B fetchSaveSingle(IStatementProvider provider)
			throws NotOneResultReturnedException {
		List<B> b = fetchMulti(provider);
		if (b.size() != 1) {
			throw new NotOneResultReturnedException();
		}
		return b.get(0);
	}

	public List<B> fetchAll() {
		return fetchMulti(new FetchAllStatementProvider());
	}

	public B fetchById(Object id) throws NotOneResultReturnedException {
		return fetchSaveSingle(new FetchByConditionStatementProvider(
				beanClassDescription.getIdColumn(), id));
	}

	public List<B> fetchMultiByFieldEquals(String fieldName, Object comparator) {
		return fetchMulti(new FetchByConditionStatementProvider(fieldName,
				comparator));
	}

	public B fetchSingleByFieldEquals(String fieldName, Object comparator)
			throws NotOneResultReturnedException {
		return fetchSaveSingle(new FetchByConditionStatementProvider(fieldName,
				comparator));
	}

	public List<B> fetchMultiByFieldEquals(String[] fieldNames,
			Object[] comparators) {
		return fetchMulti(new FetchByConditionStatementProvider(fieldNames,
				comparators));
	}

	public B fetchSingleByFieldsEquals(String[] fieldNames, Object[] comparators)
			throws NotOneResultReturnedException {
		return fetchSaveSingle(new FetchByConditionStatementProvider(
				fieldNames, comparators));
	}

	public List<B> fetchMultiByCondition(Condition[] conditions,
			Object[] comparators) {
		return fetchMulti(new FetchByConditionStatementProvider(conditions,
				comparators));
	}

	public B fetchSingleByCondition(Condition[] conditions, Object[] comparators)
			throws NotOneResultReturnedException {
		return fetchSaveSingle(new FetchByConditionStatementProvider(
				conditions, comparators));
	}

	private class FetchAllStatementProvider extends AbstractStatementProvider {

		@Override
		public void setValues(PreparedStatement ps) throws SQLException {
			// NOP
		}

		@Override
		public String getSql() {
			return db.getSqlBuilder().selectStatement(
					beanClassDescription.getSourceTable(),
					beanClassDescription.getDatabaseFieldNames());
		}

	}

	private class FetchByConditionStatementProvider extends
			AbstractStatementProvider {

		private final Condition[] conditions;
		private final Object[] values;

		public FetchByConditionStatementProvider(Condition[] conditions,
				Object[] values) {
			if (values.length > conditions.length) {
				throw new IllegalArgumentException(
						"more values than conditions given");
			}
			this.conditions = conditions;
			this.values = values;
		}

		public FetchByConditionStatementProvider(String fieldName, Object value) {
			this(
					Condition
							.fieldToWildcardCondition(columnNameFromFieldName(fieldName)),
					value);
		}

		public FetchByConditionStatementProvider(Condition condition,
				Object value) {
			this(new Condition[] { condition }, new Object[] { value });
		}

		public FetchByConditionStatementProvider(String[] columnNames,
				Object[] comparators) {
			this(fieldToWildCardConditions(columnNames), comparators);
		}

		@Override
		public void setValues(PreparedStatement ps) throws SQLException {
			for (int i = 0; i < values.length; i++) {
				ps.setObject(i + 1, values[i]);
			}
		}

		@Override
		public String getSql() {
			return db.getSqlBuilder().selectStatement(
					beanClassDescription.getSourceTable(),
					beanClassDescription.getDatabaseFieldNames(), conditions);
		}

	}

	private String columnNameFromFieldName(String s) {
		return beanClassDescription.getSaveFieldDescriptorByPropertyName(s)
				.getDatabasefieldName();
	}

	private Condition[] fieldToWildCardConditions(String[] fields) {
		Condition[] ret = new Condition[fields.length];
		for (int i = 0; i < fields.length; i++) {
			ret[i] = Condition
					.fieldToWildcardCondition(columnNameFromFieldName(fields[i]));
		}
		return ret;
	}

}

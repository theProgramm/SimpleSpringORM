package dbtest.core;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.jdbc.support.GeneratedKeyHolder;

import dbtest.springProxy.DatabaseConnectionManager;


/*
 * TODO Merge with other bean editors
 */
class BeanCreator<B> {
	
	private final DatabaseConnectionManager db;
	private final SqlBeanClassDescriptor<B> beanClassDescription;
	private final CreateStatementProvider psCreator;
	private final GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
	
	public BeanCreator(DatabaseConnectionManager db,
			SqlBeanClassDescriptor<B> beanClassDescription) {
		this.db = db;
		this.beanClassDescription = beanClassDescription;
		this.psCreator = generatePsCreator();
	}

	
	private CreateStatementProvider generatePsCreator() {
			return new CreateStatementProvider();
	}


	public B createNewBean() {
		B ret = beanClassDescription.newInstance();
		db.getTemplate().update(psCreator, keyHolder);
		try {
			beanClassDescription.createProeprtyAccess(ret).setIdPropertyValue(keyHolder.getKey());
		} catch (InvalidDataAccessApiUsageException
				| DataRetrievalFailureException e) {
			throw new RuntimeException(e);
		}
		return ret;
	}

	private class CreateStatementProvider extends AbstractStatementProvider {

		@Override
		public PreparedStatement createPreparedStatement(Connection con)
				throws SQLException {
			PreparedStatement ps = con.prepareStatement(getSql(), Statement.RETURN_GENERATED_KEYS);
			setValues(ps);
			return ps;
		}
		
		@Override
		public void setValues(PreparedStatement ps) throws SQLException {
		}

		@Override
		public String getSql() {
			return db.getSqlBuilder().insertStatement(beanClassDescription.getSourceTable(), new Object[] {}, new String[] {});
		}
	}
}

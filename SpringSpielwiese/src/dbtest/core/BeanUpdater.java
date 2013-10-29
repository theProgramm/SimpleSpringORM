package dbtest.core;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import net.omikron.sql.template.Condition;
import dbtest.springProxy.DatabaseConnectionManager;

class BeanUpdater<B> {

	private final DatabaseConnectionManager db;
	final SqlBeanClassDescriptor<B> beanClassDescription;
	private final UpdateStatementProvider psCreator;

	public BeanUpdater(DatabaseConnectionManager db,
			SqlBeanClassDescriptor<B> beanClassDescription) {
		this.db = db;
		this.beanClassDescription = beanClassDescription;
		this.psCreator = generatePsCreator();
	}

	private UpdateStatementProvider generatePsCreator() {
		return new UpdateStatementProvider();
	}

	public void saveChanges(B b) {
		db.getTemplate().update(psCreator.setObject(b));
	}

	private class UpdateStatementProvider extends AbstractStatementProvider {

		private BeanPropertyAccess<B> extraktor;

		@Override
		public void setValues(PreparedStatement ps) throws SQLException {
			ps.setObject(1, extraktor.getIdPropertyValue());
		}

		@Override
		public String getSql() {
			return db.getSqlBuilder().updateStatement(
					beanClassDescription.getSourceTable(),
					beanClassDescription.getDatabaseFieldNames(),
					generateValues(),
					Condition.fieldToWildcardCondition(beanClassDescription
							.getIdColumn()));
		}

		private Object[] generateValues() {
			if (extraktor == null) {
				throw new IllegalStateException();
			}
			Object[] values = new Object[beanClassDescription
					.getDatabaseFieldNames().length];
			for (int i = 0; i < values.length; i++) {
				values[i] = extraktor
						.getPropertyByDatabaseName(beanClassDescription
								.getDatabaseFieldNames()[i]);
			}
			return values;
		}

		public UpdateStatementProvider setObject(B object) {
			this.extraktor = new BeanPropertyAccess<B>(beanClassDescription,
					object);
			return this;
		}
	}

}

package dbtest.core;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import net.omikron.sql.template.Condition;
import dbtest.springProxy.DatabaseConnectionManager;

/*
 * TODO Merge with other bean editors
 */
class BeanDeleter<B> {

	private final DatabaseConnectionManager db;
	private final SqlBeanClassDescriptor<B> beanClassDescription;
	private final DeleteStatementProvider psCreator;

	public BeanDeleter(DatabaseConnectionManager db,
			SqlBeanClassDescriptor<B> beanClassDescription) {
		this.db = db;
		this.beanClassDescription = beanClassDescription;
		this.psCreator = generatePsCreator();
	}

	private DeleteStatementProvider generatePsCreator() {
		return new DeleteStatementProvider();
	}

	public void deleteBean(B b) {
		db.getTemplate().update(psCreator.setObject(b));
	}

	private class DeleteStatementProvider extends AbstractStatementProvider {

		BeanPropertyAccess<B> extraktor;

		@Override
		public void setValues(PreparedStatement ps) throws SQLException {
			ps.setObject(1, extraktor.getIdPropertyValue());
		}

		@Override
		public String getSql() {
			return db.getSqlBuilder().deleteStatement(
					beanClassDescription.getSourceTable(),
					Condition.fieldToWildcardCondition(beanClassDescription
							.getIdColumn()));
		}

		public DeleteStatementProvider setObject(B object) {
			this.extraktor = new BeanPropertyAccess<B>(beanClassDescription,
					object);
			return this;
		}
	}

}

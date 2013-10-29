package dbtest.core;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

abstract class AbstractStatementProvider implements
		IStatementProvider {

	@Override
	public PreparedStatement createPreparedStatement(Connection con)
			throws SQLException {
		PreparedStatement ps = con.prepareStatement(getSql());
		setValues(ps);
		return ps;
	}
}
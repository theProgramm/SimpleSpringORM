package dbtest.core;

import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.SqlProvider;

interface IStatementProvider extends PreparedStatementCreator,
		PreparedStatementSetter, SqlProvider {

}
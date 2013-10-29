package dbtest.springProxy;


import javax.sql.DataSource;

import net.omikron.sql.template.SqlBuilderBase;
import net.omikron.sql.template.driver.MySqlSyntax;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;

import com.mysql.jdbc.Driver;

/**
 * @author Patrick
 *
 */
public class DatabaseConnectionManager {

	private static final String URL = "jdbc:mysql://127.0.0.1:3306/datamanager";
	private static final Class<Driver> DRIVER = Driver.class;
	
	private final DataSource dataSource;
	private final JdbcTemplate template;
	private final SqlBuilderBase sqlBuilder = new MySqlSyntax();
	
	public DatabaseConnectionManager() {
		this.dataSource = generateDataSource();
		this.template = generateTemplate();
	}


	protected DataSource generateDataSource() {
		SimpleDriverDataSource ds = new SimpleDriverDataSource();
		ds.setDriverClass(DRIVER);
		ds.setUsername("root");
		ds.setPassword("");
		ds.setUrl(URL);
		return ds;
	}
	
	protected JdbcTemplate generateTemplate() {
		return new JdbcTemplate(dataSource);
	}


	public JdbcTemplate getTemplate() {
		return template;
	}


	public DataSource getDataSource() {
		return dataSource;
	}


	public SqlBuilderBase getSqlBuilder() {
		return sqlBuilder;
	}
	
}

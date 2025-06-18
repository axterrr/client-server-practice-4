package ua.edu.ukma.clientserver;

import javax.sql.DataSource;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.Properties;

import org.postgresql.ds.PGSimpleDataSource;

public class DaoFactory {

    private static final String PROPERTY_FILE = "application.properties";
    private static final String LOCAL_PROPERTY_FILE = "application-local.properties";

    private static DaoFactory daoFactory;
    private final DataSource dataSource;

    private DaoFactory() {
        PGSimpleDataSource dataSourceConfig = new PGSimpleDataSource();
        Properties props = new Properties();

        try (InputStream input = DaoFactory.class.getClassLoader().getResourceAsStream(PROPERTY_FILE)) {
            props.load(input);
            try (InputStream inputLocal = DaoFactory.class.getClassLoader().getResourceAsStream(LOCAL_PROPERTY_FILE)) {
                if (inputLocal != null) {
                    props.load(inputLocal);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Error during reading property file");
        }

        dataSourceConfig.setUrl(props.getProperty("database.url"));
        dataSourceConfig.setUser(props.getProperty("database.user"));
        dataSourceConfig.setPassword(props.getProperty("database.password"));

        dataSource = dataSourceConfig;
    }

    public GoodsDao goodsDao() {
        try {
            return new GoodsDao(dataSource.getConnection());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static DaoFactory getDaoFactory() {
        if (daoFactory == null) {
            daoFactory = new DaoFactory();
        }
        return daoFactory;
    }
}

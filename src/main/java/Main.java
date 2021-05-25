import oracle.jdbc.OracleConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import java.security.Security;

import java.sql.*;
import java.util.Properties;

@SpringBootApplication
public class Main {

    final static Logger LOG = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) throws ClassNotFoundException {

        for (int i = 0; i < args.length; i++) {
            LOG.info("arg {} = {}", i, args[i]);
        }

        Class.forName("oracle.jdbc.driver.OracleDriver");

        if (args.length != 1) {
            LOG.error("Invalid number of arguments: Must provide JDBC connect string jdbc:oracle:thin:@//<host>:<port>/<SID>");
            return;
        }
        Security.addProvider(new oracle.security.pki.OraclePKIProvider());
        Properties properties = new Properties();
        properties.setProperty(OracleConnection.CONNECTION_PROPERTY_THIN_NET_CONNECT_TIMEOUT, "10000");
        properties.setProperty("oracle.net.ssl_cipher_suites", "(TLS_RSA_WITH_AES_256_CBC_SHA)"); 
        properties.setProperty("oracle.net.ssl_server_dn_match", "true"); 
        properties.setProperty("oracle.net.authentication_services","(TCPS)");
        properties.setProperty("oracle.net.wallet_location", "(SOURCE=(METHOD=FILE)(METHOD_DATA=(DIRECTORY=/usr/lib/oracle/11.2/client64/lib/network/admin)))");
        try {
            LOG.info("****** Starting SSL JDBC Connection test *******");
            String sqlQuery = "select SYS_CONTEXT('USERENV','NETWORK_PROTOCOL') net_proto,sys_context('USERENV', 'AUTHENTICATED_IDENTITY') auth_identity,SYS_CONTEXT('USERENV','SESSION_USER') db_user,sys_context('USERENV','ENTERPRISE_IDENTITY') ent_identity from dual";

            Connection conn = DriverManager.getConnection(args[0], properties);
            conn.setAutoCommit(false);
            Statement statement = conn.createStatement();
            LOG.info("Running SQL query: [{}]", sqlQuery);
            ResultSet resultSet = statement.executeQuery(sqlQuery);

            while (resultSet.next()) {
                LOG.info("NET PROTOCOL : [{}]", resultSet.getString(1));
                LOG.info("AUTH_IDENTITY PROTOCOL : [{}]", resultSet.getString(2));
                LOG.info("DB_USER : [{}]", resultSet.getString(3));
                LOG.info("ENT_IDENTITY : [{}]", resultSet.getString(4));
            }

            statement.close();
            conn.close();

            statement.close();
            conn.close();

            LOG.info("JDBC connection test successful!");
        } catch (SQLException ex) {
            LOG.error("Exception occurred connecting to database: {}", ex.getMessage(), ex.getCause());
        }
    }
}

# Oracle SSL JDBC Tester

### This is a copy of aimtiaz11's JDBC tester, but ONLY for SSL (with SSO wallet)
https://github.com/aimtiaz11/oracle-jdbc-tester


##### SSO WALLET NEEDS TO BE IN /usr/lib/oracle/11.2/client64/lib/network/admin DIRECTORY 
##### (/usr/lib/oracle/11.2/client64/lib/network/admin/cwallet.sso)

##### Must specify the 
java -jar noah-ssl-jdbc-tester-1.3.jar jdbc:oracle:thin:@(DESCRIPTION=(ADDRESS=(PROTOCOL=tcps)(HOST=<HOSTNAME>)(PORT=<SSLPORT>))(CONNECT_DATA=(SERVICE_NAME=<SERVICENAME>))(SECURITY=(SSL_SERVER_CERT_DN='<CERT_DN_INFO>')))


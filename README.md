# Oracle SSL JDBC Tester for Windows and RHEL / Oracle Linux
### SSO auto-login wallets only are allowed, and database version 11g+
##### This in no way represents offical Oracle documentation, and views expressed are my own and do not represent anyone else

This is a copy of aimtiaz11's JDBC tester, but ONLY for SSL (with SSO wallet)
https://github.com/aimtiaz11/oracle-jdbc-tester

### PRE-REQS
###### WALLET DIRECTORY NEEDS TO BE IN SAME DIRECTORY AS JAR FILE
e.g.
```
ls
wallet noah-ssl-jdbc-tester-1.4.jar
ls wallet/
cwallet.sso  cwallet.sso.lck
```
Do some yum installs
```
yum install java -y
yum install telnet -y
yum install java-1.8.0-openjdk-devel -y
yum install jre1.8.x86_64 -y 
yum install jdk-16.x86_64 -y
```
Allow SSL port on database server's iptables
```
iptables -I INPUT -p tcp --dport <PORT> -j ACCEPT
service iptables save
```

# JAR command to execute
###### Must specify the connect string with the SSL_SERVER_CERT_DN parameter or it will fail!

For Linux
```java -jar noah-ssl-jdbc-tester-1.4.jar jdbc:oracle:thin:@(DESCRIPTION=(ADDRESS=(PROTOCOL=tcps)(HOST=<HOSTNAME>)(PORT=<SSLPORT>))(CONNECT_DATA=(SERVICE_NAME=<SERVICENAME>))(SECURITY=(SSL_SERVER_CERT_DN='<CERT_DN_INFO>')))```

For Windows
```
java -jar noah-ssl-jdbc-tester-1.4.jar 'jdbc:oracle:thin:@(DESCRIPTION=(ADDRESS=(PROTOCOL=tcps)(HOST=<HOSTNAME>)(PORT=<SSLPORT>))(CONNECT_DATA=(SERVICE_NAME=<SERVICENAME>))(SECURITY=(SSL_SERVER_CERT_DN="<CERT_DN_INFO>")))'
```

### Download release to run quick
https://github.com/LotToLearn/SSL_Tester_JDBC/releases/tag/1.4
```
wget https://github.com/LotToLearn/SSL_Tester_JDBC/releases/download/1.3/noah-ssl-jdbc-tester-1.3.jar
```
Make a directory called "wallet" in the same directory as the Java, and put your SSO key inside

### Manual install 
Install Maven
```
mkdir /usr/local/apache-maven
cd /usr/local/apache-maven
wget https://mirror.nodesdirect.com/apache/maven/maven-3/3.8.1/binaries/apache-maven-3.8.1-bin.zip
unzip apache-maven-3.8.1-bin.zip
cd apache-maven-3.8.1/bin
./mvn -version
```

Put in .bashrc
```
vi ~/.bashrc
export M2_HOME=/usr/local/apache-maven/apache-maven-3.8.1
export M2=$M2_HOME/bin
export PATH=$M2:$PATH

source the file to update the changes
source ~/.bashrc
```

wget the ZIP file
```
wget https://github.com/LotToLearn/SSL_Tester_JDBC/archive/refs/heads/main.zip
```
Unzip it
```
unzip main.zip
cd SSL_Tester_JDBC-main/
```
For wallet, you can change the direectory if wanted (optional, makes windows support easier)
```
properties.setProperty("oracle.net.wallet_location", "(SOURCE=(METHOD=FILE)(METHOD_DATA=(DIRECTORY=/usr/lib/oracle/11.2/client64/lib/network/admin)))");
```

Compile with Maven
```
cd /root/SSL_Tester_JDBC-main
mvn compile
```
```
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
[INFO] Total time:  8.297 s
[INFO] Finished at: 2021-05-25T15:51:48Z
[INFO] ------------------------------------------------------------------------
```
Package with Maven
```
mvn package
```
```
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
[INFO] Total time:  8.971 s
[INFO] Finished at: 2021-05-25T15:52:04Z
[INFO] ------------------------------------------------------------------------
```

cd to target and run
```
cd target/
java -jar noah-ssl-jdbc-tester-1.3.jar jdbc:oracle:thin:@(DESCRIPTION=(ADDRESS=(PROTOCOL=tcps)(HOST=150.136.213.119)(PORT=2484))(CONNECT_DATA=(SERVICE_NAME=Serv11G_iad1qn.sub03222016130.nhornervcn.oraclevcn.com))(SECURITY=(SSL_SERVER_CERT_DN='CN=server,C=US')))
```
```
15:52:22.594 [main] INFO Main - NET PROTOCOL : [tcps]
15:52:22.594 [main] INFO Main - AUTH_IDENTITY PROTOCOL : [CN=client,C=US]
15:52:22.594 [main] INFO Main - DB_USER : [CLIENT]
15:52:22.594 [main] INFO Main - ENT_IDENTITY : [cn=client,c=US]
15:52:22.600 [main] INFO Main - JDBC connection test successful!
```


# Server SQLNET, TNSNAMES, and LISTENER for reference
##### listener.ora
UPDATE BOTH IF HAVE GRID AND ORACLE IF YOU HAVE TWO!

```
ENCRYPTION_WALLET_LOCATION=(SOURCE=(METHOD=FILE)(METHOD_DATA=(DIRECTORY=/opt/oracle/dcs/commonstore/wallets/tde/$ORACLE_UNQNAME)))
SQLNET.IGNORE_ANO_ENCRYPTION_FOR_TCPS=TRUE
SQLNET.AUTHENTICATION_SERVICES=(BEQ,TCPS,TCP)
SSL_CLIENT_AUTHENTICATION = TRUE

# This Cipher only will work, needed for JDBC to work as well verify wallet works
SSL_CIPHER_SUITES = (TLS_RSA_WITH_AES_256_CBC_SHA)


WALLET_LOCATION =
   (SOURCE =
     (METHOD = FILE)
     (METHOD_DATA =
       (DIRECTORY = /wallets/server)
     )
   )

# Turns off Native Network Encryption (OCI Default, makes SSL work -- SSL won't work even with SQLNET.IGNORE_ANO_ENCRYPTION_FOR_TCPS=TRUE
SQLNET.ENCRYPTION_SERVER=REJECTED
SQLNET.CRYPTO_CHECKSUM_SERVER=REJECTED
SQLNET.ENCRYPTION_CLIENT=REJECTED
SQLNET.CRYPTO_CHECKSUM_CLIENT=REJECTED

SQLNET.INBOUND_CONNECT_TIMEOUT=120
SQLNET.ENCRYPTION_TYPES_SERVER=(AES256,AES192,AES128)
SQLNET.CRYPTO_CHECKSUM_TYPES_SERVER=(SHA1)
SQLNET.ENCRYPTION_TYPES_CLIENT=(AES256,AES192,AES128)
SQLNET.CRYPTO_CHECKSUM_TYPES_CLIENT=(SHA1)
```



##### listener.ora
```
ASMNET1LSNR_ASM=(DESCRIPTION=(ADDRESS_LIST=(ADDRESS=(PROTOCOL=IPC)(KEY=ASMNET1LSNR_ASM))))
ENABLE_GLOBAL_DYNAMIC_ENDPOINT_ASMNET1LSNR_ASM=ON	
VALID_NODE_CHECKING_REGISTRATION_ASMNET1LSNR_ASM=SUBNET
ENABLE_GLOBAL_DYNAMIC_ENDPOINT_LISTENER=ON
VALID_NODE_CHECKING_REGISTRATION_LISTENER=SUBNET

LISTENER=
  (ADDRESS_LIST=
    (ADDRESS=(PROTOCOL=IPC)(KEY=LISTENER))
    (ADDRESS=(PROTOCOL=TCP)(HOST=<HOSTNAME>)(PORT=1521))
    (ADDRESS=(PROTOCOL=TCPS)(HOST=<HOSTNAME>)(PORT=2484))
  )

WALLET_LOCATION =
   (SOURCE =
     (METHOD = FILE)
     (METHOD_DATA =
       (DIRECTORY = /wallets/server)
     )
   )

SSL_CLIENT_AUTHENTICATION = FALSE
```


##### tnsnames.ora
```
ssl =
  (DESCRIPTION =
   (ADDRESS = (PROTOCOL = TCPS)(HOST = <HOSTNAME>)(PORT = 2484))
    (CONNECT_DATA =
      (SERVER = DEDICATED)
      (SERVICE_NAME = <SERVICE_NAME>)
      (SECURITY=(SSL_SERVER_CERT_DN="CN=server,C=US")(IGNORE_ANO_ENCRYPTION_FOR_TCPS=TRUE))
    )
  )
```

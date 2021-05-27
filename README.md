[![GitHub issues open](https://img.shields.io/github/issues/network-tools/shconfparser.svg?maxAge=2592000)](https://github.com/LotToLearn/SSL_Tester_JDBC/issues)
#### The following is intended to outline a general product direction. It is intended for information purposes only. This in no way represents offical Oracle documentation, and views expressed are my own and do not represent anyone else. Issues and concerns will not be addressed by Oracle, but I will try my best to personally check the issues tab, feel free to reach out. 
# Oracle SSL JDBC Tester for Windows and RHEL / Oracle Linux
### SSO auto-login wallets only are allowed, and database version 11g+

This is a copy of aimtiaz11's JDBC tester, but his product did not work with SSL auto-login (passwordless) logins.

Nothing else online could be found, so I edited his to reflect these changes.

It will **ONLY** work with SSL passwordless JDBC client connect strings and a cwallet.sso file that is tied to your database with a CN credential as this program utilizes CN authentication only, without it will fail.
https://github.com/aimtiaz11/oracle-jdbc-tester

### PRE-REQS 
##### For both WINDOWS and LINUX
WALLET ***DIRECTORY*** NEEDS TO BE IN SAME DIRECTORY AS JAR FILE
e.g.
```
ls
wallet noah-ssl-jdbc-tester-1.4.jar
ls wallet/
cwallet.sso  cwallet.sso.lck
```
##### For the DATABASE SERVER
If your ***database server*** IPTABLES is blocking your SSL port, you need to unblock it and save the iptables

iptables is enabled by default on Oracle Linux DB instances provisioned in OCI, so this is a must do step
```
iptables -I INPUT -p tcp --dport <PORT> -j ACCEPT
service iptables save
```

##### LINUX 
```
yum install java -y
yum install telnet -y
yum install java-1.8.0-openjdk-devel -y
yum install jre1.8.x86_64 -y 
yum install jdk-16.x86_64 -y
```

##### WINDOWS
```
jre-8u291-windows-x64
jdk-16.0.1_windows-x64_bin
https://www.java.com/en/download/
https://www.oracle.com/java/technologies/javase-jdk16-downloads.html
```



### (RECOMMENDED) Downloading the .jar file
#### Step 1
##### Download the .jar file
https://github.com/LotToLearn/SSL_Tester_JDBC/releases/tag/1.4

***Linux*** you can use ***wget***
```
wget https://github.com/LotToLearn/SSL_Tester_JDBC/releases/download/1.4/noah-ssl-jdbc-tester-1.4.jar
```

#### Step 2
##### Make sure a ***wallet*** directory (match case) is in the ***directory where your .jar is located***

Put your ***cwallet.sso*** inside the ***wallet directory***

#### Step 3
##### Execute, Syntax is important as it's different for Windows/Linux
For ***LINUX***
```java -jar noah-ssl-jdbc-tester-1.4.jar jdbc:oracle:thin:@(DESCRIPTION=(ADDRESS=(PROTOCOL=tcps)(HOST=<HOSTNAME>)(PORT=<SSLPORT>))(CONNECT_DATA=(SERVICE_NAME=<SERVICENAME>))(SECURITY=(SSL_SERVER_CERT_DN='<CERT_DN_INFO>')))```

For ***WINDOWS***
```
java -jar noah-ssl-jdbc-tester-1.4.jar 'jdbc:oracle:thin:@(DESCRIPTION=(ADDRESS=(PROTOCOL=tcps)(HOST=<HOSTNAME>)(PORT=<SSLPORT>))(CONNECT_DATA=(SERVICE_NAME=<SERVICENAME>))(SECURITY=(SSL_SERVER_CERT_DN="<CERT_DN_INFO>")))'
```



### Manual install (ONLY FOR LINUX FOR NOW, WINDOWS WIP!!!)
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



# TO DO
<ol>
<li>Clean up README</li>
<li>Manual install for Windows</li>
<li>Link to DB SSL setup process</li>
</ol>

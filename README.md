DEPLOYMENT
Deployment to production still requires some manual tweaking before compilation, to deploy to production do the following:

1. Edit webapp/js/PHEDRS.js to set environment to prod
``
	// Environment can be "dev", "local", or "prod".
	environment: "prod"
``

2. Check ApplicationContext.xml to set the URL for the DEV database to the PROD database
``
	<bean id="DB_ORA_DS_Phedrs_Dev" class="org.apache.commons.dbcp2.BasicDataSource" destroy-method="close">
	<property name="driverClassName" value="oracle.jdbc.driver.OracleDriver"/>
	<property name="url" value="${db_ora_3.url}"></property>
	<property name="username" value="${db_ora_3.username}"></property>
	<property name="password" value="${db_ora_3.password}"></property>
	</bean>
``

3. Set src/main/resources/application.properties to to PROD, not DEV on line 2.
``
	env=PROD
``

4. In ApplicationContext.xml

-    <property name="dataSource" ref="DB_ORA_DS_Phedrs_Dev" />
+    <property name="dataSource" ref="DB_ORA_DS_Phedrs" />
     <property name="annotatedClasses">

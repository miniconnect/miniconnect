package hu.webarticum.miniconnect.rest;

import hu.webarticum.miniconnect.api.MiniSession;
import hu.webarticum.miniconnect.api.MiniSessionManager;
import hu.webarticum.miniconnect.jdbcadapter.JdbcAdapterSessionManager;
import io.micronaut.context.annotation.Factory;
import jakarta.inject.Singleton;

@Factory
public class Configuration {

    // TODO: use url from configuration
    @Singleton
    public MiniSession createMiniSession() {
        String inMemoryConnectionUrl = "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1;DATABASE_TO_UPPER=FALSE";
        MiniSessionManager sessionManager = new JdbcAdapterSessionManager(inMemoryConnectionUrl);
        MiniSession session = sessionManager.openSession();
        session.execute("CREATE TABLE data (" +
                "id INT NOT NULL AUTO_INCREMENT PRIMARY KEY, " +
                "label VARCHAR(255) NOT NULL, " +
                "description TEXT)");
        session.execute("INSERT INTO data VALUES (NULL, 'lorem', 'Lorem Ipsum')");
        session.execute("INSERT INTO data VALUES (NULL, 'hello', 'Hello World')");
        session.execute("INSERT INTO data VALUES (NULL, 'xxx', 'XXX YYY ZZZ')");
        session.execute("INSERT INTO data VALUES (NULL, 'abc', 'abc 123 ABC I  II III')");
        return session;
    }
    
}

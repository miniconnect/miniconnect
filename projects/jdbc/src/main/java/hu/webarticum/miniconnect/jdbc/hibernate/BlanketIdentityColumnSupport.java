package hu.webarticum.miniconnect.jdbc.hibernate;

import org.hibernate.dialect.identity.IdentityColumnSupportImpl;

public class BlanketIdentityColumnSupport extends IdentityColumnSupportImpl {
    
    @Override
    public boolean supportsIdentityColumns() {
        return true;
    }

    @Override
    public String getIdentitySelectString(String table, String column, int type) {
        return "CALL IDENTITY()"; // h2 compatible version
    }

    @Override
    public String getIdentityColumnString(int type) {
        return "NOT NULL AUTO_INCREMENT";
    }

    @Override
    public String getIdentityInsertString() {
        return "NULL";
    }
    
}

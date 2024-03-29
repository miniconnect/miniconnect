package hu.webarticum.miniconnect.jdbc.hibernate;

import java.util.Map;

import org.hibernate.LockOptions;
import org.hibernate.dialect.Dialect;
import org.hibernate.dialect.identity.IdentityColumnSupport;
import org.hibernate.engine.jdbc.dialect.spi.DialectResolutionInfo;

public class BlanketHibernateDialect extends Dialect {
    
    public BlanketHibernateDialect(DialectResolutionInfo info) { // NOSONAR: see comment below
        // FIXME: info can not be used with this hibernate version
        super();
    }

    
    @Override
    public String applyLocksToSql(String sql, LockOptions aliasedLockOptions, Map<String, String[]> keyColumnNames) {
        // not supported
        return sql;
    }
    
    @Override
    public IdentityColumnSupport getIdentityColumnSupport() {
        return new BlanketIdentityColumnSupport();
    }
    
    
}
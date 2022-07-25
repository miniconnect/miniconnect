package hu.webarticum.miniconnect.jdbc.hibernate;

import org.hibernate.dialect.Dialect;
import org.hibernate.engine.jdbc.dialect.spi.DialectResolutionInfo;

public class BlanketHibernateDialect extends Dialect {
    
    public BlanketHibernateDialect(DialectResolutionInfo info) { // NOSONAR: see comment below
        // FIXME: info can not be used with this hibernate version
        super();
    }
    
}
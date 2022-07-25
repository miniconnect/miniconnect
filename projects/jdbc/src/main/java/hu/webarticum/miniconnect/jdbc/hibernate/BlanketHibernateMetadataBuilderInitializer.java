package hu.webarticum.miniconnect.jdbc.hibernate;

import org.hibernate.boot.MetadataBuilder;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.spi.MetadataBuilderInitializer;
import org.hibernate.dialect.Dialect;
import org.hibernate.engine.jdbc.dialect.internal.DialectResolverSet;
import org.hibernate.engine.jdbc.dialect.spi.DialectResolutionInfo;
import org.hibernate.engine.jdbc.dialect.spi.DialectResolver;

import hu.webarticum.miniconnect.jdbc.provider.impl.BlanketDatabaseProvider;

public class BlanketHibernateMetadataBuilderInitializer implements MetadataBuilderInitializer {

    @Override
    public void contribute(MetadataBuilder metadataBuilder, StandardServiceRegistry serviceRegistry) {
        DialectResolver dialectResolver = serviceRegistry.getService(DialectResolver.class);

        if ((dialectResolver instanceof DialectResolverSet)) {
            ((DialectResolverSet) dialectResolver).addResolver(
                    BlanketHibernateMetadataBuilderInitializer::resolveDialect);
        }
    }

    private static Dialect resolveDialect(DialectResolutionInfo info) {
        if (info.getDatabaseName().equals(BlanketDatabaseProvider.DATABASE_PRODUCT_NAME)) {
            return new BlanketHibernateDialect(info);
        }
        return null;
    }

}
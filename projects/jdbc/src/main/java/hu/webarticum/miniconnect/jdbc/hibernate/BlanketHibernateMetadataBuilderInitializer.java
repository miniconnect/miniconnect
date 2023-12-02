package hu.webarticum.miniconnect.jdbc.hibernate;

import java.lang.reflect.Method;

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
            DialectResolverSet dialectResolverSet = (DialectResolverSet) dialectResolver;
            DialectResolver dialectResolverItem = BlanketHibernateMetadataBuilderInitializer::resolveDialect;
            try {
                dialectResolverSet.addResolver(dialectResolverItem);
            } catch (NoSuchMethodError e) {
                try {
                    // FIXME: this is for hibernate 6, but it requires java 11
                    Method method = dialectResolverSet.getClass().getDeclaredMethod(
                            "addResolver", DialectResolver[].class);
                    method.invoke(
                            dialectResolverSet,
                            new Object[] { new DialectResolver[] { dialectResolverItem } }); // NOSONAR
                } catch (ReflectiveOperationException ee) {
                    throw new IllegalArgumentException(ee);
                }
            }
        }
    }

    private static Dialect resolveDialect(DialectResolutionInfo info) {
        if (info.getDatabaseName().equals(BlanketDatabaseProvider.DATABASE_PRODUCT_NAME)) {
            return new BlanketHibernateDialect(info);
        }
        return null;
    }

}
package hu.webarticum.miniconnect.jdbc.provider.impl;

import hu.webarticum.miniconnect.api.MiniSession;
import hu.webarticum.miniconnect.jdbc.provider.TransactionIsolationLevel;

public class BlanketDatabaseProvider extends AbstractBlanketDatabaseProvider {

    // FIXME / TODO: dirty temporary solution for easy dialect detection in hibernate
    private static final String DATABASE_PRODUCT_NAME = "H2";

    // FIXME / TODO: dirty temporary solution for easy dialect detection in hibernate
    private static final String DATABASE_FULL_VERSION = "1.4.200";
    

    @Override
    public String getDatabaseProductName(MiniSession session) {
        return DATABASE_PRODUCT_NAME;
    }

    @Override
    public String getDatabaseFullVersion(MiniSession session) {
        return DATABASE_FULL_VERSION;
    }

    @Override
    public boolean isAutoCommit(MiniSession session) {
        return true;
    }

    @Override
    public void setAutoCommit(MiniSession session, boolean autoCommit) {
        // nothing to do
    }

    @Override
    public void commit(MiniSession session) {
        // nothing to do
    }

    @Override
    public void rollback(MiniSession session) {
        // nothing to do
    }

    @Override
    public int setSavepoint(MiniSession session) {
        throw new UnsupportedOperationException("Savepoints are not supported");
    }

    @Override
    public void setSavepoint(MiniSession session, String name) {
        // nothing to do
    }

    @Override
    public void rollbackToSavepoint(MiniSession session, int id) {
        // nothing to do
    }

    @Override
    public void rollbackToSavepoint(MiniSession session, String name) {
        // nothing to do
    }

    @Override
    public void releaseSavepoint(MiniSession session, int id) {
        // nothing to do
    }

    @Override
    public void releaseSavepoint(MiniSession session, String name) {
        // nothing to do
    }

    @Override
    public void setTransactionIsolationLevel(MiniSession session, TransactionIsolationLevel level) {
        // nothing to do
    }

    @Override
    public TransactionIsolationLevel getTransactionIsolationLevel(MiniSession session) {
        return TransactionIsolationLevel.NONE;
    }

}

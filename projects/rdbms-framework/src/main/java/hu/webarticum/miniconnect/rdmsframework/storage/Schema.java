package hu.webarticum.miniconnect.rdmsframework.storage;

public interface Schema extends NamedResource {

    public NamedResourceStore<Table> tables();

}

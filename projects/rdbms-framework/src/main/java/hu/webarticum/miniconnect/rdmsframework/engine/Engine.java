package hu.webarticum.miniconnect.rdmsframework.engine;

import hu.webarticum.miniconnect.rdmsframework.CheckableCloseable;

public interface Engine extends CheckableCloseable {

    public EngineSession openSession();

}

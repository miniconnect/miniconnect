
public interface TODO {

    /**
     * Complete the basic API
     */
    public void completeApi(); // TODO

    /**
     * Design and implement the client-server protocol
     */
    public void implementClientServer(); // TODO

    /**
     * Implement a dummy implementation
     * 
     * <ul>
     *   <li>single table data(id,label,description,created_at,updated_at))</li>
     *   <li>SELECT ( * | COUNT(*) | <...> ) FROM data [ WHERE <...> ] [ ORDER BY <...> [ LIMIT n ]</li>
     *   <li>UPDATE data SET col1 = value1 [, colN = valueN ] [ WHERE <...> ] [ LIMIT n ]</li>
     *   <li>INSERT [ ( <...> ) ] INTO data VALUES(<...>)</li>
     *   <li>DELETE FROM data WHERE <...></li>
     *   <li>TRUNCATE TABLE data</li>
     *   <li>SHOW TABLES</li>
     *   <li>DESCRIBE data</li>
     * </ul>
     */
    public void completeDummyDriver(); // TODO

    /**
     * Complete REPL
     * 
     * <ul>
     *   <li>pretty print reslut set as table</li>
     *   <li>print error messages</li>
     *   <li>(handle multiline queries?)</li>
     * </ul>
     */
    public void completeRepl(); // TODO

}

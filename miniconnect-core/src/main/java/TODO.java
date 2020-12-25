
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
     *   <li>better handling of quit vs semicolon etc.</li>
     *   <li>run the query on the connection</li>
     *   <li>print result set as table</li>
     *   <li>print error messages</li>
     * </ul>
     */
    public void completeRepl(); // TODO

    /**
     * Prepare for long time things
     * 
     * Subprojects:
     * 
     * <ul>
     *   <li>miniconnect-core</li>
     *   <li>miniconnect-jdbc (bridge for using miniConnect API through JDBD)</li>
     *   <li>miniconnect-? (implementation framework)</li>
     * </ul>
     */
    public void prepareLongTime(); // TODO

}

package hu.webarticum.miniconnect.messenger.impl;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import hu.webarticum.miniconnect.api.MiniSession;
import hu.webarticum.miniconnect.jdbcadapter.JdbcAdapterSession;
import hu.webarticum.miniconnect.jdbcadapter.SimpleJdbcLargeDataPutter;
import hu.webarticum.miniconnect.messenger.message.request.LargeDataHeadRequest;
import hu.webarticum.miniconnect.messenger.message.request.LargeDataPartRequest;
import hu.webarticum.miniconnect.messenger.message.request.QueryRequest;
import hu.webarticum.miniconnect.messenger.message.response.LargeDataSaveResponse;
import hu.webarticum.miniconnect.messenger.message.response.Response;
import hu.webarticum.miniconnect.messenger.message.response.ResultResponse;
import hu.webarticum.miniconnect.messenger.message.response.ResultSetEofResponse;
import hu.webarticum.miniconnect.messenger.message.response.ResultSetRowsResponse;
import hu.webarticum.miniconnect.messenger.message.response.ResultSetRowsResponse.CellData;
import hu.webarticum.miniconnect.util.data.ByteString;
import hu.webarticum.miniconnect.util.data.ImmutableList;

class SessionMessengerTest {
    
    private static final String JDBC_URL = "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1";
    
    private static final String JDBC_USERNAME = "";
    
    private static final String JDBC_PASSWORD = "";
    
    private static final String SET_STATEMENT = "SET @%s = ?";
    
    private static final String SELECT_STATEMENT = "SELECT UTF8TOSTRING(@%s) AS data";
    
    
    private Connection jdbcConnection;
    
    private MiniSession session;
    

    @Test
    void testExecute() throws Exception {
        try (Statement statement = jdbcConnection.createStatement()) {
            statement.execute("CREATE TABLE data (id INT, label TEXT)");
            statement.execute("INSERT INTO data (id, label) VALUES (7, 'seven-\u0171!')");
        }

        long sessionId = 2L;
        int exchangeId = 5;

        Map<Class<? extends Response>, Response> responses =
                Collections.synchronizedMap(new HashMap<>());
        CountDownLatch responseCountDownLatch = new CountDownLatch(3);
        SessionMessenger messenger = new SessionMessenger(sessionId, session);
        messenger.accept(
                new QueryRequest(sessionId, exchangeId, "SELECT * FROM data"),
                r -> {
                    responses.put(r.getClass(), r);
                    responseCountDownLatch.countDown();
                });
        
        responseCountDownLatch.await(10, TimeUnit.SECONDS);
        
        Response rawResultResponse = responses.get(ResultResponse.class);
        assertThat(rawResultResponse).isInstanceOf(ResultResponse.class);
        
        ResultResponse resultResponse = (ResultResponse) rawResultResponse;
        assertThat(resultResponse.columnHeaders().size()).isEqualTo(2);
        assertThat(resultResponse.hasResultSet()).isTrue();

        Response rawResultSetRowsResponse = responses.get(ResultSetRowsResponse.class);
        assertThat(rawResultSetRowsResponse).isInstanceOf(ResultSetRowsResponse.class);
        
        ResultSetRowsResponse resultSetRowsResponse =
                (ResultSetRowsResponse) rawResultSetRowsResponse;
        assertThat(resultSetRowsResponse.sessionId()).isEqualTo(2L);
        assertThat(resultSetRowsResponse.exchangeId()).isEqualTo(5);
        assertThat(resultSetRowsResponse.rowOffset()).isEqualTo(0L);
        
        ImmutableList<ImmutableList<CellData>> rows = resultSetRowsResponse.rows();
        assertThat(rows.size()).isEqualTo(1);
        
        ImmutableList<CellData> row = rows.get(0);
        assertThat(row.size()).isEqualTo(2);
        
        CellData idCell = row.get(0);
        assertThat(idCell.content().asBuffer().getInt()).isEqualTo(7);
        
        CellData labelCell = row.get(1);
        assertThat(labelCell.content().toString(StandardCharsets.UTF_8)).isEqualTo("seven-\u0171!");

        Response rawResultSetEofResponse = responses.get(ResultSetEofResponse.class);
        assertThat(rawResultSetEofResponse).isInstanceOf(ResultSetEofResponse.class);

        ResultSetEofResponse resultSetEofResponse =
                (ResultSetEofResponse) rawResultSetEofResponse;
        assertThat(resultSetEofResponse.endOffset()).isEqualTo(1L);
    }
    
    @Test
    void testPutLargeData() throws Exception {
        long sessionId = 3L;
        int exchangeId = 7;
        String variableName = "testvar";
        String contentPart1 = "part1,\u0171\u0171\u0171,";
        String contentPart2 = "part2,\u00E9\u00E9\u00E9";
        String fullContent = contentPart1 + contentPart2;
        ByteString contentBytesPart1 = ByteString.wrap(contentPart1.getBytes(StandardCharsets.UTF_8));
        ByteString contentBytesPart2 = ByteString.wrap(contentPart2.getBytes(StandardCharsets.UTF_8));
        
        long fullLength = contentBytesPart1.length() + contentBytesPart2.length();
        CompletableFuture<Response> responseFuture = new CompletableFuture<>();
        SessionMessenger messenger = new SessionMessenger(sessionId, session);
        messenger.accept(
                new LargeDataHeadRequest(sessionId, exchangeId, variableName, fullLength),
                responseFuture::complete);
        messenger.accept(new LargeDataPartRequest(
                sessionId, exchangeId, 0L, contentBytesPart1));
        messenger.accept(new LargeDataPartRequest(
                sessionId, exchangeId, contentBytesPart1.length(), contentBytesPart2));
        Response response = responseFuture.get(10, TimeUnit.SECONDS);
        
        assertThat(response).isInstanceOf(LargeDataSaveResponse.class);
        
        LargeDataSaveResponse saveResponse = (LargeDataSaveResponse) response;
        assertThat(saveResponse.success()).isTrue();
        assertThat(saveResponse.sessionId()).isEqualTo(sessionId);
        assertThat(saveResponse.exchangeId()).isEqualTo(exchangeId);
        
        String selectedValue;
        try (Statement statement = jdbcConnection.createStatement()) {
            String selectQuery = String.format(SELECT_STATEMENT, variableName);
            try (ResultSet resultSet = statement.executeQuery(selectQuery)) {
                resultSet.next();
                selectedValue = resultSet.getString(1);
            }
        }
        assertThat(selectedValue).isEqualTo(fullContent);
    }
    
    @BeforeEach
    void init() throws SQLException {
        jdbcConnection = DriverManager.getConnection(
                JDBC_URL, JDBC_USERNAME, JDBC_PASSWORD);
        SimpleJdbcLargeDataPutter largeDataPutter =
                new SimpleJdbcLargeDataPutter(SET_STATEMENT);
        session = new JdbcAdapterSession(jdbcConnection, largeDataPutter);
    }

    @AfterEach
    void uninit() throws IOException, SQLException {
        session.close();
        jdbcConnection.close();
    }

}

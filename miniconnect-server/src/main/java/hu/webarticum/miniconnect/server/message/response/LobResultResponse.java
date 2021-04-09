package hu.webarticum.miniconnect.server.message.response;

public class LobResultResponse {

    private final int lobId;

    private final String variableName;


    public LobResultResponse(int lobId, String variableName) {
        this.lobId = lobId;
        this.variableName = variableName;
    }


    public int getLobId() {
        return lobId;
    }

    public String getVariableName() {
        return variableName;
    }

}

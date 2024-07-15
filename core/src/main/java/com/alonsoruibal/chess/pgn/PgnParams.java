public class PgnParams {
    private Board board;
    private String whiteName;
    private String blackName;
    private String event;
    private String site;
    private String result;

    public PgnParams(Board board, String whiteName, String blackName) {
        this(board, whiteName, blackName, null, null, null);
    }

    public PgnParams(Board board, String whiteName, String blackName, String event, String site, String result) {
        this.board = board;
        this.whiteName = whiteName;
        this.blackName = blackName;
        this.event = event;
        this.site = site;
        this.result = result;
    }

    public Board getBoard() {
        return board;
    }

    public String getWhiteName() {
        return whiteName;
    }

    public String getBlackName() {
        return blackName;
    }

    public String getEvent() {
        return event;
    }

    public String getSite() {
        return site;
    }

    public String getResult() {
        return result;
    }
}

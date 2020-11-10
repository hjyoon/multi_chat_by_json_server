package multi_chat_by_json_server;

public class Data {
    private String op;
    private String data;

    public Data() {}

    public Data(String op, String data) {
        this.op = op;
        this.data = data;
    }

    public String getOp() {
        return op;
    }

    public String getData() {
        return data;
    }
}
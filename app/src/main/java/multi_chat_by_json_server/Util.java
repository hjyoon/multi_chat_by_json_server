package multi_chat_by_json_server;

import java.io.*;
import java.time.*;
import java.time.format.*;

public class Util {
    static public String time_now() {
        return "("+LocalDateTime.now().format(DateTimeFormatter.ofPattern("MM-dd HH:mm:ss"))+")";
    }
}
package server;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ServerTest {
    ArrayList<RunnableClient> CLIENTS = new ArrayList<>();
    @Test
    void removeClient() {
        RunnableClient client = new RunnableClient();

        CLIENTS.add(client);
        assertEquals(1, CLIENTS.size());

        Server.removeClient(client, CLIENTS);
        assertEquals(0, CLIENTS.size());
    }
}

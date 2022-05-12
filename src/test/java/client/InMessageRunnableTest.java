package client;

import org.junit.jupiter.api.Test;

import java.io.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class InMessageRunnableTest {
    @Test
    void log() {
        String readLine = null;
        String msg = "Тест пройден";
        File log = new File("target/test-classes/testLogs.txt");
        try (FileWriter fw = new FileWriter(log, true);
             BufferedWriter bw = new BufferedWriter(fw)) {
            bw.write(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            FileReader fr = new FileReader(log);
            BufferedReader reader = new BufferedReader(fr);
            readLine = reader.readLine();

        } catch (IOException e) {
            e.printStackTrace();
        }
        assertEquals(msg, readLine);
        log.deleteOnExit();
    }
}

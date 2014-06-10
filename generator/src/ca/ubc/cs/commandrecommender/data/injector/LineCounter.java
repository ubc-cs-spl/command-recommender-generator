package ca.ubc.cs.commandrecommender.data.injector;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by KeEr on 2014-06-10.
 */
public class LineCounter {

    public static void main(String args[]) throws IOException {
        String filePath = args[0];
        //establish connection
        BufferedReader reader = new BufferedReader(new InputStreamReader(
                new FileInputStream(filePath)));
        long i = 0L;
        try {
            while (true) {
                final String line = reader.readLine();
                if (line == null) break;
                i++;
            }
            System.out.println(i);
        } finally {
            reader.close();
        }
    }
}

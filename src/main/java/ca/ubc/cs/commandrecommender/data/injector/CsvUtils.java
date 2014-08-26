package ca.ubc.cs.commandrecommender.data.injector;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.List;

/**
 * Parser for csv
 */
public class CsvUtils {

    /**
     * Split the String parameter into substrings. The parameter is assumed to
     * be in CSV format. Comma separators are assumed. An entry that starts and
     * ends with double-quotes may contain commas or escaped double-quotes.
     * Double-quotes are escaped by putting two one-after-the-other. This method
     * assumes that there are no extraneous white spaces (i.e. leading or
     * trailing) in the input.
     * <p>
     * Note that we don't worry about trying to re-translate escaped characters
     * back into their unescaped form. We assume that this method is used exclusively
     * for displaying events in a preview pane, or for applying filters; we don't
     * need to translate the escaped characters in either of these cases.
     * <p>
     * The value: "first,\"\"\"second\"\", third\",fourth" will be parsed into
     * three strings: "first", "\"second\", third", and "fourth".
     * <p>
     * Note that callers can safely assume that all entries in the resulting
     * array will be non-<code>null</code>.
     *
     * @param line
     *            a {@link String}. Must not be <code>null</code>.
     *
     * @return an array of {@link String}s.
     */
    public static String[] splitLine(String line) {
        List<String> strings = new java.util.ArrayList<String>();
        try {
            splitLine(line, strings);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return strings.toArray(new String[strings.size()]);
    }

    private static void splitLine(String line, List<String> strings) throws IOException {
		/*
		 * There is a potential issue with this implementation in the case
		 * where a quoted-wrapped field starts with an escaped quote. i.e.
		 * the string \"\"\"value\"\"\" will be read as escaped-quote, followed
		 * by quote rather than as quote followed by escaped-quote as is
		 * intended. The net result is the same (evidenced in the test cases).
		 */
        Reader reader = new StringReader(line);
        int next;
        StringBuilder builder = new StringBuilder();
        boolean inQuote = false;
        while ((next = reader.read()) != -1) {
            if (next == '"') {
                reader.mark(1);
                if (reader.read() == '"') {
                    builder.append('"');
                } else {
                    reader.reset();
                    inQuote = !inQuote;
                }
            } else if (next == ',') {
                if (inQuote) {
                    builder.append(',');
                } else {
                    strings.add(builder.toString());
                    builder = new StringBuilder();
                }

            } else {
                builder.append((char)next);
            }
        }
        strings.add(builder.toString());
    }
}

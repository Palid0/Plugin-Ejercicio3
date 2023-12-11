package testsmell;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

/**
 * This class is utilized to write output to an HTML file.
 */
public class ResultsWriter {

    private String outputFile;
    private FileWriter writer;

    /**
     * Creates the file into which output it to be written into. Results from each file will be stored in a new file
     * @throws IOException
     */
    private ResultsWriter() throws IOException {
        outputFile = "Output_TestSmellReport.html";
        writer = new FileWriter(outputFile, false);
        writer.write("<html><head><title>TestSmeller - Test smells found:</title></head><body bgcolor='lightblue'><table border='1' align='center'>");
        writer.flush();
    }

    /**
     * Factory method that provides a new instance of the ResultsWriter
     * @return new ResultsWriter instance
     * @throws IOException
     */
    public static ResultsWriter createResultsWriter() throws IOException {
        return new ResultsWriter();
    }

    /**
     * Writes column names into the HTML file as table headers
     * @param columnNames the column names
     * @throws IOException
     */
    public void writeColumnName(List<String> columnNames) throws IOException {
        writer.append("<tr>");
        for (String name : columnNames) {
            writer.append("<th>").append(name).append("</th>");
        }
        writer.append("</tr>");
        writer.flush();
    }

    /**
     * Writes column values into the HTML file as a table row
     * @param columnValues the column values
     * @throws IOException
     */
    public void writeLine(List<String> columnValues) throws IOException {
        writer.append("<tr>");
        for (String value : columnValues) {
            writer.append("<td>").append(value).append("</td>");
        }
        writer.append("</tr>");
        writer.flush();
    }

    /**
     * Closes the table and HTML tags. Should be called after all rows have been written.
     * @throws IOException
     */
    public void close() throws IOException {
        writer.append("</table></body></html>");
        writer.flush();
        writer.close();
    }
}

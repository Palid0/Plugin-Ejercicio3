import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import testsmell.AbstractSmell;
import testsmell.ResultsWriter;
import testsmell.TestFile;
import testsmell.TestSmellDetector;
import thresholds.DefaultThresholds;

import java.io.*;
import java.nio.file.FileSystems;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Mojo(name = "TestSmeller")
public class Main extends AbstractMojo {
    public void execute() throws MojoExecutionException, MojoFailureException {
        TestSmellDetector testSmeller = new TestSmellDetector(new DefaultThresholds());
        String rootPath = "src/test";

        // Search for the directory, place it into javaTestDirectory
        String javaTestDirectory = "";
        File directory = new File(rootPath);
        if (directory.isDirectory()) {
            File[] files = directory.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isDirectory()) {
                        File[] innerFiles = file.listFiles();
                        if (innerFiles != null) {
                            for (File innerFile : innerFiles) {
                                if (innerFile.isFile() && innerFile.getName().endsWith(".java")) {
                                    javaTestDirectory = file.getAbsolutePath();
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        }

        File[] files = new File(javaTestDirectory).listFiles();
        String result = "";
        int counter = files.length;

        for (File file : files) {
            String filePath = javaTestDirectory + FileSystems.getDefault().getSeparator() + file.getName();
            String replacedPath = javaTestDirectory.replace("src" + FileSystems.getDefault().getSeparator() + "test", "src" + FileSystems.getDefault().getSeparator() + "main");

            String javaFileName = file.getName().substring(0, file.getName().length() - 9) + ".java";
            String fullPath = replacedPath + FileSystems.getDefault().getSeparator() + javaFileName;

            result += "Tests," + filePath + "," + fullPath + ((counter != 1) ? "\n" : "");
            counter--;
        }

        String[] linesItem;
        String[] lineItem;
        TestFile testFile;
        List<TestFile> testFiles = new ArrayList<>();
        // Use comma as separator instead of '\n'
        linesItem = result.split("\n");

        for (String line : linesItem) {
            lineItem = line.split(",");

            // Check if the test file has an associated production file
            String productionFilePath = (lineItem.length == 3) ? lineItem[2] : "";
            testFile = new TestFile(lineItem[0], lineItem[1], productionFilePath);
            testFiles.add(testFile);
        }


        /*
          Initialize the output file - Create the output file and add the column names
         */
        ResultsWriter resultsWriter = null;
        try {
            resultsWriter = ResultsWriter.createResultsWriter();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        List<String> columnNames;
        List<String> columnValues;

        columnNames = testSmeller.getTestSmellNames();
        columnNames.add(0, "App");
        columnNames.add(1, "TestClass");
        columnNames.add(2, "TestFilePath");
        columnNames.add(3, "ProductionFilePath");
        columnNames.add(4, "RelativeTestFilePath");
        columnNames.add(5, "RelativeProductionFilePath");
        columnNames.add(6, "NumberOfMethods");

        try {
            resultsWriter.writeColumnName(columnNames);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        /*
          Iterate through all test files to detect smells and then write the output
        */
        TestFile tempFile;
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date;
        for (TestFile file : testFiles) {
            date = new Date();
            System.out.println(dateFormat.format(date) + " Processing: " + file.getTestFilePath());
            System.out.println("Processing: " + file.getTestFilePath());

            //detect smells
            try {
                tempFile = testSmeller.detectSmells(file);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            //write output
            columnValues = new ArrayList<>();
            columnValues.add(file.getApp());
            columnValues.add(file.getTestFileName());
            columnValues.add(file.getTestFilePath());
            columnValues.add(file.getProductionFilePath());
            columnValues.add(file.getRelativeTestFilePath());
            columnValues.add(file.getRelativeProductionFilePath());
            columnValues.add(String.valueOf(file.getNumberOfTestMethods()));
            for (AbstractSmell smell : tempFile.getTestSmells()) {
                try {
                    columnValues.add(String.valueOf(smell.getNumberOfSmellyTests()));
                } catch (NullPointerException e) {
                    columnValues.add("");
                }
            }
            try {
                resultsWriter.writeLine(columnValues);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        System.out.println("end");
    }

}

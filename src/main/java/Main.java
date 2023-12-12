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
        File rootPath = new File("src/test");

        // Search for the directory, place it into javaTestDirectory
        List<String> javaTestDirectory = directoryFounder(rootPath);
  
        
        File[] files = new File(javaTestDirectory.get(0)).listFiles();
        String result = "";

        int counter = files.length;

        for (File file : files) {
            String filePath = javaTestDirectory.get(0) + FileSystems.getDefault().getSeparator() + file.getName();
            String replacedPath = javaTestDirectory.get(0).replace("src" + FileSystems.getDefault().getSeparator() + "test", "src" + FileSystems.getDefault().getSeparator() + "main");

            String javaFileName = file.getName().substring(0, file.getName().length() - 9) + ".java";
            String fullPath = replacedPath + FileSystems.getDefault().getSeparator() + javaFileName;

            result += "Tests," + filePath + "," + fullPath + ((counter != 1) ? "\n" : "");
            counter--;
        }
        System.out.print("hola3");
        String[] linesItem;
        String[] lineItem;
        TestFile testFile;
        List<TestFile> testFiles = new ArrayList<>();
        // Use comma as separator instead of '\n'
        linesItem = result.split("\n");
        System.out.print("hola4");
        for (String line : linesItem) {
            lineItem = line.split(",");

            // Check if the test file has an associated production file
            String productionFilePath = (lineItem.length == 3) ? lineItem[2] : "";
            testFile = new TestFile(lineItem[0], lineItem[1], productionFilePath);
            testFiles.add(testFile);
        }
        System.out.print("hola5");

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

    public static List<String> directoryFounder(File directorio) {
        List<String> directorios = new ArrayList<>();
        if (directorio.isDirectory()) {
            File[] archivos = directorio.listFiles();
            if (archivos != null) {
                for (File archivo : archivos) {
                    if (archivo.isDirectory()) {
                        List<String> subDirectorios = directoryFounder(archivo);
                        directorios.addAll(subDirectorios);
                    } else if (archivo.isFile() && archivo.getName().endsWith(".java")) {
                        directorios.add(directorio.getAbsolutePath());
                    }
                }
            }
        }
        return directorios;
    }

}

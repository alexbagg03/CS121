If you wish to convey anything to the TA, please put it here.


We were not sure whether we need to include code for figuring out the solutions to the questions. Therefore, we
decided to post our code below for reference.

-------------------------------------------------------------------------------------------------------------
For finding the longest page in terms of number of words, we wrote the following code to figure it out.

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;


public class LongestPage {

    public static class PageInfoPair {
        private String url;
        private Integer wordCount;

        PageInfoPair(String url, Integer wordCount) {
            this.url = url;
            this.wordCount = wordCount;
        }

        String getURL() {
            return this.url;
        }

        int getWordCount() {
            return this.wordCount;
        }
    }

    public static void main(String[] args) {

        int longestWordCount = 0;
        String longestFilename = null;
        String longestPageURL = null;

        File dir = new File(args[0]);

        for (File f : dir.listFiles()) {
            if (f.isFile()) {
                int count = wordCount(f).getWordCount();

                if (count > longestWordCount) {
                    longestWordCount = count;
                    longestFilename = f.getAbsolutePath();
                    longestPageURL = wordCount(f).getURL();
                }
            }
        }

        System.out.println("Longest Page File: " + longestFilename);
        System.out.println("Longest Page URL: " + longestPageURL);
        System.out.println("Longest Page Word Count: " + longestWordCount);
    }

    public static PageInfoPair wordCount (File file) {
        int wordCount = 0;
        String url = null;

        try {
            List<String> lines = Files.readAllLines(file.toPath());
            url = lines.get(0);
            String[] line = lines.get(lines.size() - 1).split(":");
            wordCount = Integer.parseInt(line[line.length - 1].trim());
        }

        catch (IOException ioe) {
            System.out.println("IOException. Please try again.");
        }

        return new PageInfoPair(url, wordCount);
    }
}

-------------------------------------------------------------------------------------------------------------
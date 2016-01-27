package ir.assignments.two;

import edu.uci.ics.crawler4j.crawler.CrawlConfig;
import edu.uci.ics.crawler4j.crawler.CrawlController;
import edu.uci.ics.crawler4j.fetcher.PageFetcher;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtConfig;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtServer;

/**
 * Controller class from library: https://github.com/yasserg/crawler4j.git
 *
 * Created for CS121 Assignment 2 by:
 * Alex Bagg (80332851)
 */
public class Controller {


    ///////////////////////////////////////////
    // MEMBERS
    ///////////////////////////////////////////
    private static final String crawlStorageFolder = "/Users/Alex/Documents/GitHub/CS121/CS121/Assignment2/data/crawl/root";
    private static final int numberOfCrawlers = 7;
    private static CrawlController mCrawlController;
    private static final String userAgentString = "UCI Inf141-CS121 crawler 80332851";


    ///////////////////////////////////////////
    // CONSTRUCTOR
    ///////////////////////////////////////////
    public Controller(String seed){
        /*
         * For each crawl, you need to add some seed urls. These are the first
         * URLs that are fetched and then the crawler starts following links
         * which are found in these pages
         */
        CrawlConfig config = new CrawlConfig();
        config.setCrawlStorageFolder(crawlStorageFolder);
        config.setPolitenessDelay(600);
        config.setUserAgentString(userAgentString);
        config.setResumableCrawling(true);
        config.setMaxDepthOfCrawling(3);

        /*
         * Instantiate the controller for this crawl.
         */
        PageFetcher pageFetcher = new PageFetcher(config);
        RobotstxtConfig robotstxtConfig = new RobotstxtConfig();
        RobotstxtServer robotstxtServer = new RobotstxtServer(robotstxtConfig, pageFetcher);

        try {
            mCrawlController = new CrawlController(config, pageFetcher, robotstxtServer);
            mCrawlController.addSeed(seed);

        } catch (Exception e) {
            e.printStackTrace();

        }

    }


    ///////////////////////////////////////////
    // METHODS
    ///////////////////////////////////////////
    public static void start(){
        /*
         * Start the crawl. This is a blocking operation, meaning that your code
         * will reach the line after this only when crawling is finished.
         */
        mCrawlController.start(Crawler.class, numberOfCrawlers);

    }

}

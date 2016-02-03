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
 * Vonnie Wu (71785156)
 * <---------------------------------------- ADD YOUR NAME / ID HERE
 */
public class Controller {

    ///////////////////////////////////////////
    // MEMBERS
    ///////////////////////////////////////////
    private static final String crawlStorageFolder = "../Assignment2/data/crawl/root";
    private static final int numberOfCrawlers = 7;
    private static final int politenessDelay = 600; // in milliseconds
    private static final int maxDepthOfCrawling = 3;
    private static CrawlController crawlController;
    private static final String userAgentString = "UCI Inf141-CS121 crawler 80332851 19367502 71785156";


    ///////////////////////////////////////////
    // CONSTRUCTOR
    ///////////////////////////////////////////
    /**
     * Constructor for the Controller that takes a seed url
     * to begin crawling with. The crawler configurations are then
     * set with a storage folder, politeness delay, user agent string,
     * and whether resumable crawling is enabled/disabled. An instance
     * of the CrawlController is then created and the given seed url
     * is added to it.
     *
     * @param seed - Initial seed url to begin crawling on
     */
    public Controller(String seed){
        CrawlConfig config = new CrawlConfig();
        config.setCrawlStorageFolder(crawlStorageFolder);
        config.setPolitenessDelay(politenessDelay);
        config.setUserAgentString(userAgentString);
        //TODO remove when done testing
        config.setMaxDepthOfCrawling(maxDepthOfCrawling);
        config.setResumableCrawling(false);

        PageFetcher pageFetcher = new PageFetcher(config);
        RobotstxtConfig robotstxtConfig = new RobotstxtConfig();
        RobotstxtServer robotstxtServer = new RobotstxtServer(robotstxtConfig, pageFetcher);

        try {
            crawlController = new CrawlController(config, pageFetcher, robotstxtServer);

            /*
             * For each crawl, you need to add some seed urls. These are the first
             * URLs that are fetched and then the crawler starts following links
             * which are found in these pages
             */
            crawlController.addSeed(seed);

        } catch (Exception e) {
            e.printStackTrace();

        }

    }


    ///////////////////////////////////////////
    // METHODS
    ///////////////////////////////////////////
    /**
     * Starts the crawl with the specific Crawler class and number of crawlers set.
     * This is a blocking operation, meaning that your code will reach the line after
     * this only when crawling is finished.
     */
    public static void start(){
        crawlController.start(Crawler.class, numberOfCrawlers);

    }

}

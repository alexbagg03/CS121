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
 * Nick Prendergast (19367502)
 * Vonnie Wu (71785156)
 * Aaron Lai (93309744)
 *
 */
public class Controller {

    ///////////////////////////////////////////
    // MEMBERS
    ///////////////////////////////////////////
    private static final String crawlStorageFolder = "../Assignment2/data/crawl/root";
    private static final int numberOfCrawlers = 7;
    private static final int politenessDelay = 600; // in milliseconds
    private static final int maxPagesToFetch = 4;
    private static CrawlController crawlController;
    private static final String userAgentString = "UCI Inf141-CS121 crawler 80332851 19367502 71785156 93309744";


    ///////////////////////////////////////////
    // METHODS
    ///////////////////////////////////////////
    /**
     * Takes a seed url to begin crawling with. The crawler configurations
     * are then set with a storage folder, politeness delay, user agent string,
     * and whether resumable crawling is enabled/disabled. An instance
     * of the CrawlController is then created, the given seed url
     * is added, and begins the crawl process.
     *
     * @param seed - Initial seed url to begin crawling on
     */
    public static void start(String seed){
        CrawlConfig config = new CrawlConfig();
        config.setCrawlStorageFolder(crawlStorageFolder);
        config.setPolitenessDelay(politenessDelay);
        config.setUserAgentString(userAgentString);
        //TODO remove when done testing
        config.setMaxPagesToFetch(maxPagesToFetch);
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
            crawlController.start(Crawler.class, numberOfCrawlers);

        } catch (Exception e) {
            e.printStackTrace();

        }

    }
    public static void stop(){
        crawlController.shutdown();
        crawlController.waitUntilFinish();

    }

}

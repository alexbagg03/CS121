package ir.assignments.two;

import edu.uci.ics.crawler4j.crawler.CrawlConfig;
import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.crawler.WebCrawler;
import edu.uci.ics.crawler4j.fetcher.PageFetcher;
import edu.uci.ics.crawler4j.parser.HtmlParseData;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtConfig;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtServer;
import edu.uci.ics.crawler4j.url.WebURL;

import java.io.*;
import java.util.*;
import java.util.regex.Pattern;

public class Crawler extends WebCrawler {

	///////////////////////////////////////////
	// MEMBERS
	///////////////////////////////////////////
	private final static Pattern FILTERS = Pattern.compile(".*(\\.(css|js|gif|jpg" + "|png|mp3|mp3|zip|gz))$");
	private final static String icsDomain = "http://www.ics.uci.edu";
	private static final String subdomainFileDir = "../Assignment2";
	private static final String subdomainFileName = "Subdomains.txt";
	private static final String crawledDataDir = "../Assignment2/crawledData";
	private static File subdomainFile;
	private static ArrayList<String> urlCollection;
	private static ArrayList<String> blackList;
	private static int idCounter = 1;

	/**
	 * This methods performs a crawl starting at the specified seed URL. Returns a
	 * collection containing all URLs visited during the crawl.
	 *
	 * @param seedURL - Seed URL to crawl
	 */
	public static Collection<String> crawl(String seedURL) {
		blackList = new ArrayList<String>();
		blackList.add("duttgroup.ics.uci.edu/");
		blackList.add("calendar.ics.uci.edu/");

		urlCollection = new ArrayList<String>();
		createSubdomainFile();

		Controller.start(seedURL);

		return urlCollection;

	}

	/**
	 * Specifies whether the given url should be crawled or not (based on your crawling logic).
	 * It sets up a RobotstxtConfig and RobotstxtServer to check whether the given URL is
	 * allowed to be visited.
	 * The crawler will ignore urls that have css, js, git, ... extensions and will only
	 * accept urls that start with "http://www.ics.uci.edu/".
	 *
	 * @param referringPage - Page referring to the given URL
	 * @param url - URL to be checked whether to visit or not
	 */
	@Override
	public boolean shouldVisit(Page referringPage, WebURL url) {
		CrawlConfig config = new CrawlConfig();
		PageFetcher pageFetcher = new PageFetcher(config);
		RobotstxtConfig robotstxtConfig = new RobotstxtConfig();
		RobotstxtServer robotstxtServer = new RobotstxtServer(robotstxtConfig, pageFetcher);

		// DO NOT visit URLs that are blacklisted
		if(blackList.contains(url.toString())){
			return false;

		}

		// If the robots.txt configuration is enabled and DOES NOT allow the given url
		// DO NOT visit it
		if(robotstxtConfig.isEnabled() && !robotstxtServer.allows(url)){
			return false;
		}

		String href = url.getURL().toLowerCase();
		return !FILTERS.matcher(href).matches() && href.startsWith(icsDomain);
	}

	/**
	 * This function is called when a page is fetched and ready
	 * to be processed. It gathers the data, text, html, and outgoing
	 * links of the page, then adds the URL as a key to the subdomainMap
	 * with the number of outgoing links as a value;
	 *
	 * @param page - Page being visited on a specific URL
	 */
	@Override
	public void visit(Page page) {
		String url = page.getWebURL().getURL();
		System.out.println("URL: " + url);
		urlCollection.add(url);

		if (page.getParseData() instanceof HtmlParseData) {
			HtmlParseData htmlParseData = (HtmlParseData) page.getParseData();
			String text = htmlParseData.getText();
			String html = htmlParseData.getHtml();
			Set<WebURL> links = htmlParseData.getOutgoingUrls();

			System.out.println("Text length: " + text.length());
			System.out.println("Html length: " + html.length());
			System.out.println("Number of outgoing links: " + links.size());

			if(!url.equals(icsDomain + "/")) {
				addSubdomainToFile(url, links.size());

			}

			savePageDataToDatabase(htmlParseData);

		}

	}

	/**
	 * Main function for running the crawler on the ICS domain.
	 * This is primarily used for testing.
	 *
	 * @param args
     */
	///////////////////////////////////////////
	// MAIN FUNCTION
	///////////////////////////////////////////
	public static void main(String[] args){
		crawl(icsDomain);
		sortSubdomainFile();
		Controller.stop();

	}


	///////////////////////////////////////////
	// METHODS
	///////////////////////////////////////////
	private static void createSubdomainFile(){
		subdomainFile = new File(subdomainFileDir, subdomainFileName);
		BufferedWriter writer = null;

		try {
			writer = new BufferedWriter(new FileWriter(subdomainFile));
			writer.write("");

		} catch (Exception e) {
			e.printStackTrace();

		} finally {
			try {
				writer.close();

			} catch (Exception e) {
				e.printStackTrace();

			}

		}

	}
	public static ArrayList<String> tokenizeString(String input){
			ArrayList<String> toReturn = new ArrayList<String>();
			try {
				Scanner tokenizer = new Scanner(input).useDelimiter("[^A-Za-z0-9]+");
				while (tokenizer.hasNext()) {
					toReturn.add(tokenizer.next().toLowerCase());
				}
			} catch (Exception e) {
				System.out.println(e);
			}
			return toReturn;
	}

	private static HashMap<String, Integer> countFrequencies(String input) {
		ArrayList<String> toReturn = tokenizeString(input);
		HashMap<String, Integer> toReturn = new HashMap<>();
		for (String toInsert : tokens) {
			if (toReturn.containsKey(toInsert))
				toReturn.get(toInsert) += 1;
			else
				toReturn.get(toInsert) = 1;
		}
	}

	private static void getWordInfo(String url, String urlText) {
		HashMap<String, int> wordFrequencies = countFrequencies(urlText);
		String urlString = url.replaceAll("[^A-Za-z0-9 ]", "");
		int wordCount = 0;
		BufferedWriter writer = null;

		try	{
			writer = new BufferedWriter(new FileWriter(url + "TEXT", true));
			writer.write(url);
			writer.newLine();
			for(Map.Entry<String, Integer> frequency : wordFrequencies.entrySet()) {
			for (Map.entry<string, int> frequency : wordFrequencies.entrySet()) {
				wordCount += frequency.getValue();
				writer.write(frequency.getKey() + ", " + frequency.getValue());
				writer.newLine();
			}
		} catch (Exception e) {
			e.printStackTrace();

		} finally {
			writer.close();

		}

	}
	private static void addSubdomainToFile(String url, int nLinks){
		BufferedWriter writer = null;

		try {
			writer = new BufferedWriter(new FileWriter(subdomainFile, true));
			writer.write(url + ", " + nLinks);
			writer.newLine();

		} catch (Exception e) {
			e.printStackTrace();

		} finally {
			try {
				writer.close();

			} catch (Exception e) {
				e.printStackTrace();

			}

		}

	}

	/**
	 * Code referenced for sorting items in a file:
	 * http://www.avajava.com/tutorials/lessons/how-do-i-alphabetically-sort-the-lines-of-a-file.html%3Bjsess..
	 */
	private static void sortSubdomainFile(){
		FileReader fileReader = null;
		ArrayList<String> subdomainList = new ArrayList<String>();

		try {
			fileReader = new FileReader(subdomainFileName);

			BufferedReader bufferedReader = new BufferedReader(fileReader);

			String line;
			while ((line = bufferedReader.readLine()) != null) {
				subdomainList.add(line);

			}

		} catch (IOException e) {
			e.printStackTrace();

		} finally {
			try {
				fileReader.close();

			} catch (IOException e) {
				e.printStackTrace();

			}

		}

		Collections.sort(subdomainList, SubdomainComparator);

		createSubdomainFile();

		BufferedWriter writer = null;

		try {
			writer = new BufferedWriter(new FileWriter(subdomainFileName, true));
			for(String subdomain : subdomainList) {
				writer.write(subdomain);
				writer.newLine();

			}

		} catch (Exception e) {
			e.printStackTrace();

		} finally {
			try {
				writer.close();

			} catch (Exception e) {
				e.printStackTrace();

			}

		}

	}
	private static String generateUniqueId(){
		UUID id = UUID.randomUUID();

		return id.toString();

	}
	private static void savePageDataToDatabase(HtmlParseData parseData){
		String text = parseData.getText();
		String html = parseData.getHtml();

		File pageFile = new File(crawledDataDir, String.valueOf(idCounter++));
		BufferedWriter writer = null;

		try {
			writer = new BufferedWriter(new FileWriter(pageFile));
			writer.write(html);
			writer.newLine();
			writer.write(text);

		} catch (Exception e) {
			e.printStackTrace();

		} finally {
			try {
				writer.close();

			} catch (Exception e) {
				e.printStackTrace();

			}

		}

	}


	///////////////////////////////////////////
	// SUBDOMAIN COMPARATOR
	///////////////////////////////////////////
	private static Comparator<String> SubdomainComparator = new Comparator<String>() {
		@Override
		public int compare(String sd1, String sd2) {
			return sd1.toLowerCase().compareTo(sd2.toLowerCase());

		}

	};

}

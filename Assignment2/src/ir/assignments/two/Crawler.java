package ir.assignments.two;

import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.crawler.WebCrawler;
import edu.uci.ics.crawler4j.parser.HtmlParseData;
import edu.uci.ics.crawler4j.url.WebURL;

import java.io.*;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.regex.Pattern;

public class Crawler extends WebCrawler {

	///////////////////////////////////////////
	// MEMBERS
	///////////////////////////////////////////
	private final static Pattern FILTERS = Pattern.compile(".*(\\.(css|js|gif|jpg" + "|png|mp3|mp3|zip|gz|wmv|pdf))$");
	private final static String icsDomain = "http://www.ics.uci.edu";
	private static final String subdomainFileDir = "./Assignment2/";
	private static final String subdomainFileName = "Subdomains.txt";
	private static final String crawledDataDir = "./Assignment2/crawledData";
	private static final String wordDir = "./Assignment2/words/";
	private static File subdomainFile;
	private static ArrayList<String> urlCollection;
	private static ArrayList<String> blackList;
	private static int idCounter = 1;
	private static PageWordCountPair longestPage = null;
	private static int wordCount = 0;

	private static class PageWordCountPair {
		private final Page page;
		private final int wordCount;

		public PageWordCountPair(Page page, int wordCount) {
			this.page = page;
			this.wordCount = wordCount;
		}

		public Page getPage() {
			return page;
		}

		public int getWordCount() {
			return wordCount;
		}
	}

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
		// DO NOT visit URLs that are blacklisted
		for (String toAvoid: blackList) {
			if (url.toString().contains(toAvoid)) {
				return false;
			}

		}

		String href = url.getURL().toLowerCase();
		return !FILTERS.matcher(href).matches() && href.contains("ics.uci.edu");
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
			wordCount = getWordInfo(url, text);
			Set<WebURL> links = htmlParseData.getOutgoingUrls();

			if (longestPage == null) {
				longestPage = new PageWordCountPair(page, wordCount);
			}

			else if (wordCount > longestPage.wordCount) {
				longestPage = new PageWordCountPair(page, wordCount);
			}


			System.out.println("Text length: " + text.length());
			System.out.println("Html length: " + html.length());
			System.out.println("Number of outgoing links: " + links.size());
			if(url.contains("ics.uci.edu")) {
				addSubdomainToFile(url, links.size());

			}

			savePageDataToDatabase(htmlParseData);

		}

	}

	/**
	 * Main function for running the crawler on the ICS domain.
	 * This is primarily used for testing.
	 *
	 * @param args Main function. Expects no args.
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
	// SUBDOMAIN METHODS
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
				if (writer != null) {
					writer.close();
				}

			} catch (Exception e) {
				e.printStackTrace();

			}

		}

	}

	/**
	 * Adds the given url and the number of outgoing links found
	 * to the Subdomains file.
	 * @param url String representing the URL
	 * @param nLinks Int representing the number of outgoing links found
     */
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
	private static void sortSubdomainFile() {
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
				if(fileReader != null)
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

			for (String subdomain : subdomainList) {
				writer.write(subdomain);
				writer.newLine();

			}

		} catch (IOException e) {
			e.printStackTrace();

		} finally {
			try {
				if(writer != null)
					writer.close();

			} catch (Exception e){
				e.printStackTrace();

			}

		}

	}


	///////////////////////////////////////////
	// FREQUENCY METHODS
	///////////////////////////////////////////

	/**
	 * This method is used to tokenize the strings of text into alphanumeric groups.
	 * In this case the url.getText() output.
	 * @param input The string that needs to be tokenized
	 * @return ArrayList<String> The list of tokens found.
     */
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

	/**
	 * This method takes a string and returns a HashMap of String, Integer
	 * entries the represent the words and their occurrences in the string.
	 * @param input String
	 * @return HashMap<String, Integer>
     */
	private static HashMap<String, Integer> countFrequencies(String input) {
		ArrayList<String> tokens = tokenizeString(input);
		HashMap<String, Integer> tokenFrequencies = new HashMap<String, Integer>();
		for (String token : tokens) {
			if (tokenFrequencies.containsKey(token)) {
				tokenFrequencies.put(token, tokenFrequencies.get(token) + 1);
			}

			else {
				tokenFrequencies.put(token, 1);
			}
		}

		return tokenFrequencies;

	}

	/**
	 * This function generates a word frequency file for the given url
	 * and writes it to a file for later processing.
	 *
	 * @param url String representing the url
	 * @param urlText The text of the URL (No HTML
     * @return int The total words found in the urlText
     */
	private static int getWordInfo(String url, String urlText) {
		HashMap<String, Integer> wordFrequencies = countFrequencies(urlText);
		String urlString = url.replaceAll("[^A-Za-z0-9 ]", "");
		int wordCount = 0;
		PrintWriter writer = null;
		try	{
			writer = new PrintWriter(new FileWriter(wordDir + urlString + "TEXT.txt", true));
			writer.write(url + ", " + idCounter);
			writer.write("\n");
			for (Map.Entry<String, Integer> frequency : wordFrequencies.entrySet()) {
				wordCount += frequency.getValue();
				writer.write(frequency.getKey() + ", " + frequency.getValue());
				writer.write("\n");
			}
			writer.write("##Word Count: " + wordCount);

		} catch (Exception e) {
			e.printStackTrace();

		} finally {
			try {
				if(writer != null)
					writer.close();

			} catch (Exception e) {
				e.printStackTrace();

			}
		}

		return wordCount;
	}


	///////////////////////////////////////////
	// CRAWLED DATA METHODS
	///////////////////////////////////////////

	/**
	 * This method writes a record of each webpage visted to the crawledData directory.
	 * @param parseData The object from the crawler that contains raw and parsed html data
     */
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
				if (writer != null) {
					writer.close();
				}

			} catch (Exception e) {
				e.printStackTrace();

			}

		}

	}


	///////////////////////////////////////////
	// SUBDOMAIN COMPARATOR
	///////////////////////////////////////////
	/**
	 * This comparator is used to sort the Subdomains file.
	 */
	private static Comparator<String> SubdomainComparator = new Comparator<String>() {
		@Override
		public int compare(String sd1, String sd2) {
			return sd1.toLowerCase().compareTo(sd2.toLowerCase());

		}

	};

}

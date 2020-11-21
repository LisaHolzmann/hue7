package hue7.bsp2;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.function.Predicate;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author bmayr
 */
public class WebCrawler7 implements ILinkHandler {

    private final Collection<String> visitedLinks = Collections.synchronizedSet(new HashSet<String>());
//    private final Collection<String> visitedLinks = Collections.synchronizedList(new ArrayList<String>());
    private String url;
    private ForkJoinPool mainPool;

    Predicate<Integer> stopCondition;
    private boolean distinct;
    private int size;

    public WebCrawler7(String startingURL, int maxThreads) {
        this.url = startingURL;
        this.mainPool = new ForkJoinPool(maxThreads);
        this.stopCondition = (t) -> {
            return t >= 1000;
        };
        this.distinct = true;
        this.size = 0;
    }

    public void setStopCondition(Predicate<Integer> stopCondition) {
        this.stopCondition = stopCondition;
    }

    private void startCrawling() {
        // ToDo: Invoke LinkFinderAction on threadpool
        LinkFinderAction lfa = new LinkFinderAction(url, this);
        mainPool.invoke(lfa);
    }

    @Override
    public int size() {
        if (distinct) {
            return visitedLinks.size();
        } else {
            return size;
        }
    }

    @Override
    public void addVisited(String s) {
        visitedLinks.add(s);
        size++;
    }

    @Override
    public boolean visited(String s) {
        return visitedLinks.contains(s);
    }

    public void setDistinct(boolean distinct) {
        this.distinct = distinct;
    }

    public boolean getDistinct() {
        return distinct;
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws Exception {
        //  new WebCrawler7("http://www.orf.at", 64).startCrawling();
        benchmark();

    }

    // Just override - we do not need this methode when using forkJoinPool
    @Override
    public void queueLink(String link) throws Exception {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    ForkJoinPool getPool() {
        return this.mainPool;
    }

    public static void benchmark() {

        long searchTime = System.currentTimeMillis();
        WebCrawler7 webCrawlerSearch = new WebCrawler7("http://www.orf.at", 64);
        webCrawlerSearch.setStopCondition((t) -> {
            return t >= 1500;
        });

        webCrawlerSearch.startCrawling();
        long result = System.currentTimeMillis() - searchTime;
        System.out.println("Search coverage: " + result / 1000 + "sec");

        ///////////////////////////////////////
        long processingTime = System.currentTimeMillis();
        WebCrawler7 webCrawlerProc = new WebCrawler7("http://www.orf.at", 64);
        webCrawlerProc.setDistinct(false);
        webCrawlerProc.setStopCondition((t) -> {
            return t >= 3000;
        });

        webCrawlerProc.startCrawling();
        long result2 = System.currentTimeMillis() - processingTime;
        System.out.println("Processing power: " + result2 + "ms");
    }
}

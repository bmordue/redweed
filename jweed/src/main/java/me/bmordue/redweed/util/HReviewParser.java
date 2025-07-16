package me.bmordue.redweed.util;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HReviewParser {

    public static List<Map<String, String>> parse(String html) {
        List<Map<String, String>> reviews = new ArrayList<>();
        Document doc = Jsoup.parse(html);
        Elements reviewElements = doc.select(".hreview");
        for (Element reviewElement : reviewElements) {
            Map<String, String> review = new HashMap<>();
            review.put("rating", reviewElement.select(".rating").first().text());
            review.put("description", reviewElement.select(".description").first().text());
            review.put("reviewer", reviewElement.select(".reviewer").first().text());
            reviews.add(review);
        }
        return reviews;
    }
}

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
            Element ratingEl = reviewElement.select(".rating").first();
            if (ratingEl != null) {
                review.put("rating", ratingEl.text());
            }
            Element descriptionEl = reviewElement.select(".description").first();
            if (descriptionEl != null) {
                review.put("description", descriptionEl.text());
            }
            Element reviewerEl = reviewElement.select(".reviewer").first();
            if (reviewerEl != null) {
                review.put("reviewer", reviewerEl.text());
            }            reviews.add(review);
        }
        return reviews;
    }
}

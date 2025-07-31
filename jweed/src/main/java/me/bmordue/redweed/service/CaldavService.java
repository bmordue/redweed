package me.bmordue.redweed.service;

import jakarta.inject.Singleton;
import me.bmordue.redweed.config.CaldavConfiguration;
import net.fortuna.ical4j.connector.dav.CalDavStore;
import net.fortuna.ical4j.connector.dav.CardDavStore;
import net.fortuna.ical4j.connector.dav.property.DavProperty;
import net.fortuna.ical4j.vcard.VCard;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

@Singleton
public class CaldavService {

    private static final Logger log = LoggerFactory.getLogger(CaldavService.class);

    private final CaldavConfiguration caldavConfiguration;

    public CaldavService(CaldavConfiguration caldavConfiguration) {
        this.caldavConfiguration = caldavConfiguration;
    }

    public List<String> fetchVCardResources() {
        List<String> vcards = new ArrayList<>();
        try {
            URL url = new URL(caldavConfiguration.getUrl());
            CardDavStore store = new CardDavStore(url, caldavConfiguration.getUsername(), caldavConfiguration.getPassword().toCharArray());
            store.connect();
            // Assuming the URL is the address book collection URL
            net.fortuna.ical4j.connector.CardCollection<VCard> cardCollection = store.getCollection(url.getPath());

            for (VCard card : cardCollection.getComponents()) {
                vcards.add(card.toString());
            }
            store.disconnect();
        } catch (Exception e) {
            log.error("Failed to fetch vCards from CalDAV server", e);
            throw new RuntimeException("Failed to fetch vCards from CalDAV server", e);
        }
        return vcards;
    }
}

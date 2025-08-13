package me.bmordue.redweed.service;

import me.bmordue.redweed.exception.ICalParsingException;
import net.fortuna.ical4j.data.CalendarBuilder;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.Component;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.property.*;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.DC;
import org.apache.jena.vocabulary.RDFS;

import java.io.StringReader;
import java.net.URI;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * Converts iCal data to RDF.
 */
public final class ICalToRdfConverter {

    // Event Ontology vocabulary
    private static final String EVENT_NS = "http://purl.org/NET/c4dm/event.owl#";
    private static final String ICAL_NS = "http://www.w3.org/2002/12/cal/ical#";
    private static final String TIME_NS = "http://www.w3.org/2006/time#";

    /**
     * Converts iCal data to an RDF model.
     *
     * @param ics the iCal data
     * @return the RDF model
     * @throws ICalParsingException if the iCal data cannot be parsed
     */
    public static Model convert(String ics) {
        if (ics == null || ics.trim().isEmpty()) {
            throw new ICalParsingException("iCal data cannot be null or empty", null);
        }

        Model model = ModelFactory.createDefaultModel();
        
        try {
            CalendarBuilder builder = new CalendarBuilder();
            Calendar calendar = builder.build(new StringReader(ics));
            
            // Process all VEVENT components
            for (Object component : calendar.getComponents(Component.VEVENT)) {
                if (component instanceof VEvent) {
                    processVEvent(model, (VEvent) component);
                }
            }
            
            return model;
            
        } catch (Exception e) {
            throw new ICalParsingException("Failed to parse iCal data", e);
        }
    }

    /**
     * Processes a VEVENT component and adds it to the RDF model.
     *
     * @param model  the RDF model
     * @param vEvent the VEVENT component
     */
    private static void processVEvent(Model model, VEvent vEvent) {
        // Generate URI for the event
        String eventUri = generateEventUri(vEvent);
        Resource event = model.createResource(eventUri);
        
        // Add type as Event
        event.addProperty(RDF.type, model.createResource(EVENT_NS + "Event"));
        
        // Process basic properties
        processSummary(model, event, vEvent);
        processDescription(model, event, vEvent);
        processDateTimes(model, event, vEvent);
        processLocation(model, event, vEvent);
        processOrganizer(model, event, vEvent);
        processAttendees(model, event, vEvent);
        processCategories(model, event, vEvent);
        processPriority(model, event, vEvent);
        processStatus(model, event, vEvent);
        processUid(model, event, vEvent);
    }

    /**
     * Generates a URI for an event based on its UID.
     */
    private static String generateEventUri(VEvent vEvent) {
        Uid uid = vEvent.getUid();
        if (uid != null && uid.getValue() != null && !uid.getValue().trim().isEmpty()) {
            // Clean the UID to make it a valid URI part
            String cleanUid = uid.getValue().replaceAll("[^a-zA-Z0-9-._~]", "_");
            return "urn:redweed:event:" + cleanUid;
        } else {
            return "urn:redweed:event:" + UUID.randomUUID();
        }
    }

    private static void processSummary(Model model, Resource event, VEvent vEvent) {
        Summary summary = vEvent.getSummary();
        if (summary != null && summary.getValue() != null) {
            event.addProperty(RDFS.label, summary.getValue());
            event.addProperty(model.createProperty(ICAL_NS + "summary"), summary.getValue());
        }
    }

    private static void processDescription(Model model, Resource event, VEvent vEvent) {
        Description description = vEvent.getDescription();
        if (description != null && description.getValue() != null) {
            event.addProperty(DC.description, description.getValue());
            event.addProperty(model.createProperty(ICAL_NS + "description"), description.getValue());
        }
    }

    private static void processDateTimes(Model model, Resource event, VEvent vEvent) {
        DtStart dtStart = vEvent.getStartDate();
        if (dtStart != null && dtStart.getDate() != null) {
            String startTime = dtStart.getDate().toString();
            event.addProperty(model.createProperty(TIME_NS + "hasBeginning"), startTime);
            event.addProperty(model.createProperty(ICAL_NS + "dtstart"), startTime);
        }

        DtEnd dtEnd = vEvent.getEndDate();
        if (dtEnd != null && dtEnd.getDate() != null) {
            String endTime = dtEnd.getDate().toString();
            event.addProperty(model.createProperty(TIME_NS + "hasEnd"), endTime);
            event.addProperty(model.createProperty(ICAL_NS + "dtend"), endTime);
        }
    }

    private static void processLocation(Model model, Resource event, VEvent vEvent) {
        Location location = vEvent.getLocation();
        if (location != null && location.getValue() != null) {
            event.addProperty(model.createProperty(EVENT_NS + "place"), location.getValue());
            event.addProperty(model.createProperty(ICAL_NS + "location"), location.getValue());
        }
    }

    private static void processOrganizer(Model model, Resource event, VEvent vEvent) {
        Organizer organizer = vEvent.getOrganizer();
        if (organizer != null) {
            String organizerValue = organizer.getValue();
            if (organizerValue != null) {
                event.addProperty(model.createProperty(EVENT_NS + "agent"), organizerValue);
                event.addProperty(model.createProperty(ICAL_NS + "organizer"), organizerValue);
            }
        }
    }

    private static void processAttendees(Model model, Resource event, VEvent vEvent) {
        vEvent.getProperties(Attendee.ATTENDEE).forEach(property -> {
            if (property instanceof Attendee) {
                Attendee attendee = (Attendee) property;
                String attendeeValue = attendee.getValue();
                if (attendeeValue != null) {
                    event.addProperty(model.createProperty(ICAL_NS + "attendee"), attendeeValue);
                }
            }
        });
    }

    private static void processCategories(Model model, Resource event, VEvent vEvent) {
        // Get categories from properties
        vEvent.getProperties(Categories.CATEGORIES).forEach(property -> {
            if (property instanceof Categories) {
                Categories categories = (Categories) property;
                String categoriesValue = categories.getValue();
                if (categoriesValue != null) {
                    event.addProperty(model.createProperty(ICAL_NS + "categories"), categoriesValue);
                }
            }
        });
    }

    private static void processPriority(Model model, Resource event, VEvent vEvent) {
        Priority priority = vEvent.getPriority();
        if (priority != null && priority.getValue() != null) {
            event.addProperty(model.createProperty(ICAL_NS + "priority"), priority.getValue());
        }
    }

    private static void processStatus(Model model, Resource event, VEvent vEvent) {
        Status status = vEvent.getStatus();
        if (status != null && status.getValue() != null) {
            event.addProperty(model.createProperty(ICAL_NS + "status"), status.getValue());
        }
    }

    private static void processUid(Model model, Resource event, VEvent vEvent) {
        Uid uid = vEvent.getUid();
        if (uid != null && uid.getValue() != null) {
            event.addProperty(model.createProperty(ICAL_NS + "uid"), uid.getValue());
        }
    }
}

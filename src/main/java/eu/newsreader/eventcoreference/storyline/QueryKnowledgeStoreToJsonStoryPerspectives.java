package eu.newsreader.eventcoreference.storyline;

import eu.newsreader.eventcoreference.input.EsoReader;
import eu.newsreader.eventcoreference.input.FrameNetReader;
import eu.newsreader.eventcoreference.input.TrigKSTripleReader;
import eu.newsreader.eventcoreference.util.EuroVoc;
import eu.newsreader.eventcoreference.util.Util;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

/**
 * Created by piek on 1/3/14.
 */
public class QueryKnowledgeStoreToJsonStoryPerspectives {

    static HashMap<String, ArrayList<String>> iliMap = new HashMap<String, ArrayList<String>>();
    static ArrayList<String> blacklist = new ArrayList<String>();
    static boolean ALL = false; /// if true we do not filter events
    static boolean SKIPPEVENTS = false; /// if true we we exclude perspective events from the stories
    static boolean MERGE = false;
    static String timeGran = "D";
    static String actionOnt = "";
    static int actionSim = 1;
    static int interSect = 1;
    static EsoReader esoReader = new EsoReader();
    static FrameNetReader frameNetReader = new FrameNetReader();
    static ArrayList<String> topFrames = new ArrayList<String>();
    static int fnLevel = 0;
    static int esoLevel = 0;
    static int climaxThreshold = 0;
    static String entityFilter = "";
    static Integer actorThreshold = -1;
    static int topicThreshold = 0;
    static int nEvents = 0;
    static int nActors = 0;
    static int nMentions = 0;
    static int nStories = 0;
    static String year = "";
    static String KSSERVICE = ""; //https://knowledgestore2.fbk.eu";
    static String KS = ""; //"nwr/wikinews-new";
    static String KSuser = ""; //"nwr/wikinews-new";
    static String KSpass = ""; //"nwr/wikinews-new";
    static String EVENTSCHEMA = "";
    static EuroVoc euroVoc = new EuroVoc();
    static EuroVoc euroVocBlackList = new EuroVoc();

    static public void main (String[] args) {
        String project = "NewsReader storyline";
        String pathToILIfile = "";
        String sparqlQuery = "";
        String eventQuery = "";
        String sourceQuery = "";
        String entityQuery = "";
        String kslimit = "500";
        String pathToRawTextIndexFile = "";
        String pathToFtDataFile = "";
        String blackListFile = "";
        String fnFile = "";
        String esoFile = "";
        String euroVocFile = "";
        String euroVocBlackListFile = "";
        fnLevel = 0;
        esoLevel = 0;
        for (int i = 0; i < args.length; i++) {
            String arg = args[i];
            if (arg.equals("--sparql") && args.length>(i+1)) {
                sparqlQuery = args[i+1];
            }
            else if (arg.equals("--event") && args.length>(i+1)) {
                eventQuery = args[i+1];
            }
            else if (arg.equals("--entity") && args.length>(i+1)) {
                entityQuery = args[i+1];
            }
            else if (arg.equals("--source") && args.length>(i+1)) {
                sourceQuery = args[i+1];
            }
            else if (arg.equals("--year") && args.length>(i+1)) {
                year = args[i+1];
            }
            else if (arg.equals("--ft") && args.length>(i+1)) {
                pathToFtDataFile = args[i+1];
            }
            else if (arg.equals("--time") && args.length>(i+1)) {
                timeGran = args[i+1];
            }
            else if (arg.equals("--actor-intersect") && args.length>(i+1)) {
                try {
                    interSect = Integer.parseInt(args[i+1]);
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }
            else if (arg.equals("--action-sim") && args.length>(i+1)) {
                try {
                    actionSim = Integer.parseInt(args[i+1]);
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }
            else if (arg.equals("--action-ont") && args.length>(i+1)) {
                actionOnt = args[i+1];
            }
            else if (arg.equals("--action-schema") && args.length>(i+1)) {
                EVENTSCHEMA = args[i+1];
            }
            else if (arg.equals("--merge")) {
                MERGE = true;
            }
            else if (arg.equals("--eurovoc") && args.length>(i+1)) {
                euroVocFile = args[i+1];
                euroVoc.readEuroVoc(euroVocFile,"en");
            }
            else if (arg.equals("--eurovoc-blacklist") && args.length>(i+1)) {
                euroVocBlackListFile = args[i+1];
                euroVocBlackList.readEuroVoc(euroVocBlackListFile, "en");
               // System.out.println("euroVocBlackList = " + euroVocBlackList.uriLabelMap.size());
            }
            else if (arg.equals("--service") && args.length>(i+1)) {
                KSSERVICE = args[i+1];
            }
            else if (arg.equals("--ks") && args.length>(i+1)) {
                KS = args[i+1];
            }
            else if (arg.equals("--ks-user") && args.length>(i+1)) {
                KSuser = args[i+1];
            }
            else if (arg.equals("--ks-pass") && args.length>(i+1)) {
                KSpass = args[i+1];
            }
            else if (arg.equals("--ks-limit") && args.length>(i+1)) {
                kslimit = args[i+1];
            }
            else if (arg.equals("--project") && args.length>(i+1)) {
                project = args[i+1];
            }
            else if (arg.equals("--ili") && args.length>(i+1)) {
                pathToILIfile = args[i+1];
            }
            else if (arg.equals("--raw-text") && args.length>(i+1)) {
                pathToRawTextIndexFile = args[i+1];
            }
            else if (arg.equals("--black-list") && args.length>(i+1)) {
                blackListFile = args[i+1];
            }
            else if (arg.equals("--actor-cnt") && args.length>(i+1)) {
                actorThreshold = Integer.parseInt(args[i+1]);
            }
            else if (arg.equals("--all")){
                ALL = true;
            }
            else if (arg.equals("--frame-relations") && args.length>(i+1)) {
                fnFile = args[i+1];
            }
            else if (arg.equals("--frame-level") && args.length>(i+1)) {
                try {
                    fnLevel = Integer.parseInt(args[i+1]);
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }
            else if (arg.equals("--eso-relations") && args.length>(i+1)) {
                esoFile = args[i+1];
            }
            else if (arg.equals("--eso-level") && args.length>(i+1)) {
                try {
                    esoLevel = Integer.parseInt(args[i+1]);
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }
            else if (arg.equals("--climax-level") && args.length>(i+1)) {
                try {
                    climaxThreshold = Integer.parseInt(args[i+1]);
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }
            else if (arg.equals("--topic-level") && args.length>(i+1)) {
                try {
                    topicThreshold = Integer.parseInt(args[i+1]);
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }
        }
        /*System.out.println("climaxThreshold = " + climaxThreshold);
        System.out.println("topicThreshold = " + topicThreshold);
        System.out.println("actionOnt = " + actionOnt);
        System.out.println("actionSim = " + actionSim);
        System.out.println("actorThreshold = " + actorThreshold);
        System.out.println("actor interSect = " + interSect);
        System.out.println("max results for KnowledgeStore = " + kslimit);
        System.out.println("pathToRawTextIndexFile = " + pathToRawTextIndexFile);
        System.out.println("MERGE = " + MERGE);*/
        if (!blackListFile.isEmpty()) {
            blacklist = Util.ReadFileToStringArrayList(blackListFile);
        }

        if (!fnFile.isEmpty()) {
            frameNetReader.parseFile(fnFile);
            topFrames = frameNetReader.getTopsFrameNetTree();
            frameNetReader.flatRelations(fnLevel);
        }
        if (!esoFile.isEmpty()) {
            esoReader.parseFile(esoFile);
        }
        iliMap = Util.ReadFileToStringHashMap(pathToILIfile);

        if (!KSSERVICE.isEmpty()) {
            if (KSuser.isEmpty()) {
                TrigKSTripleReader.setServicePoint(KSSERVICE, KS);
            }
            else {
                TrigKSTripleReader.setServicePoint(KSSERVICE, KS, KSuser, KSpass);
            }
        }
        if (!kslimit.isEmpty()) {
            TrigKSTripleReader.limit = kslimit;
        }

        long startTime = System.currentTimeMillis();

        if (!sparqlQuery.isEmpty()) {
            System.out.println(" * querying KnowledgeStore with SPARQL = " + sparqlQuery);
            TrigKSTripleReader.readTriplesFromKs(sparqlQuery);
        }
        else if (!eventQuery.isEmpty() || !entityQuery.isEmpty() || !sourceQuery.isEmpty()) {
            if (!eventQuery.isEmpty()) {
                System.out.println(" * querying KnowledgeStore for event = " + eventQuery);
            }
            if (!entityQuery.isEmpty()) {
                System.out.println(" * querying KnowledgeStore for entity = " + entityQuery);
            }
            if (!sourceQuery.isEmpty()) {
                System.out.println(" * querying KnowledgeStore for source = " + sourceQuery);
            }

            else if (!entityQuery.isEmpty() && eventQuery.isEmpty()) {
                TrigKSTripleReader.readTriplesFromKSforEntity(entityQuery);

/*
                if (ALL) {
                    TrigKSTripleReader.readTriplesFromKSforEntity(entityQuery);
                   // TrigKSTripleReader.readTriplesFromKSforSurfaceSubForm(entityQuery, "");
                }
                else {
                   // TrigKSTripleReader.readTriplesFromKSforEntityEventType(entityQuery, EVENTSCHEMA.toLowerCase());
                  //  TrigKSTripleReader.readTriplesFromKSforSurfaceSubForm(entityQuery, EVENTSCHEMA.toLowerCase());
                }
*/
            }
            else if (entityQuery.isEmpty() && !eventQuery.isEmpty()) {
                TrigKSTripleReader.readTriplesFromKSforEvents(eventQuery);
            }
            else if (!entityQuery.isEmpty() && !eventQuery.isEmpty()) {
                TrigKSTripleReader.readTriplesFromKSforEntityAndEvent(entityQuery, eventQuery);
            }
        }

        long estimatedTime = System.currentTimeMillis() - startTime;

        System.out.println(" * Time elapsed to get results from KS:"+estimatedTime/1000.0);

        try {
            ArrayList<JSONObject> jsonObjects = JsonStoryUtil.getJSONObjectArray(TrigKSTripleReader.trigTripleData,
                    ALL,SKIPPEVENTS, EVENTSCHEMA, blacklist, iliMap, fnLevel, frameNetReader, topFrames, esoLevel, esoReader);
            System.out.println(" * Events in SEM-RDF = " + jsonObjects.size());
            if (blacklist.size()>0) {
                jsonObjects = JsonStoryUtil.filterEventsForBlackList(jsonObjects, blacklist);
             //   System.out.println("Events after blacklist filter= " + jsonObjects.size());
            }
            if (actorThreshold>0) {
                jsonObjects = JsonStoryUtil.filterEventsForActors(jsonObjects, entityFilter, actorThreshold);
             //   System.out.println("Events after actor count filter = " + jsonObjects.size());
            }

/*
            jsonObjects = JsonStoryUtil.removePerspectiveEvents(trigTripleData, jsonObjects);
            System.out.println("Events after removing perspective events = " + jsonObjects.size());
*/

            jsonObjects = JsonStoryUtil.createStoryLinesForJSONArrayList(jsonObjects,
                        topicThreshold,
                        climaxThreshold,
                        entityFilter, MERGE,
                        timeGran,
                        actionOnt,
                        actionSim,
                        interSect);
         //   System.out.println("Events after storyline filter = " + jsonObjects.size());
            //JsonStoryUtil.augmentEventLabelsWithArguments(jsonObjects);

            JsonStoryUtil.minimalizeActors(jsonObjects);
           // System.out.println("eurovoc = " + euroVoc.uriLabelMap.size());
            if (euroVoc.uriLabelMap.size()>0) {
                JsonStoryUtil.renameStories(jsonObjects, euroVoc, euroVocBlackList);
            }
            ArrayList<JSONObject> rawTextArrayList = new ArrayList<JSONObject>();
            ArrayList<JSONObject> structuredEvents = new ArrayList<JSONObject>();
            if (jsonObjects.size()>0) {
                TrigKSTripleReader.integrateAttributionFromKs(jsonObjects);
            }


            if (!pathToFtDataFile.isEmpty()) {
                HashMap<String, ArrayList<ReadFtData.DataFt>> dataFtMap = ReadFtData.readData(pathToFtDataFile);
                structuredEvents = ReadFtData.convertFtDataToJsonEventArray(dataFtMap);
            }

            if (!pathToRawTextIndexFile.isEmpty()) {
               // rawTextArrayList = Util.ReadFileToUriTextArrayList(pathToRawTextIndexFile);
                MentionResolver.ReadFileToUriTextArrayList(pathToRawTextIndexFile, jsonObjects);
            }
            else {
                if (!eventQuery.isEmpty() || !entityQuery.isEmpty() || !sparqlQuery.isEmpty()) {
                  //  rawTextArrayList = MentionResolver.createRawTextIndexFromMentions(jsonObjects, KS, KSuser, KSpass);
                 //   System.out.println("Getting the text snippets for: " + jsonObjects.size()+ " events");
                    MentionResolver.createSnippetIndexFromMentions(jsonObjects, KSSERVICE, KS, KSuser, KSpass);
                }

            }
            nEvents = jsonObjects.size();
            nActors = JsonStoryUtil.countActors(jsonObjects);
            nMentions = JsonStoryUtil.countMentions(jsonObjects);
            nStories = JsonStoryUtil.countGroups(jsonObjects);

            JsonSerialization.writeJsonObjectArrayWithStructuredData("", "", project,
                    jsonObjects, rawTextArrayList, nEvents, nStories, nActors, nMentions, "polls", structuredEvents);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        System.out.println(" * story_cnt = " + nStories);
        System.out.println(" * event_cnt = " + nEvents);
        System.out.println(" * mention_cnt = "+ nMentions);
        System.out.println(" * actor_cnt = " + nActors);
    }

    static void splitStories (ArrayList<JSONObject> events,
                              ArrayList<JSONObject> rawTextArrayList,
                              ArrayList<JSONObject> structuredEvents,
                              String project,
                              String trigFolder
    ) {

        HashMap<String, ArrayList<JSONObject>> storyMap = new HashMap<String, ArrayList<JSONObject>>();
        for (int i = 0; i < events.size(); i++) {
            JSONObject event = events.get(i);
            try {
                String group = event.getString("group");
                if (storyMap.containsKey(group)) {
                    ArrayList<JSONObject> groupEvents = storyMap.get(group);
                    groupEvents.add(event);
                    storyMap.put(group,groupEvents);
                }
                else {
                    ArrayList<JSONObject> groupEvents = new ArrayList<JSONObject>();
                    groupEvents.add(event);
                    storyMap.put(group,groupEvents);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        Set keySet = storyMap.keySet();
        Iterator<String> keys = keySet.iterator();
        while ((keys.hasNext())) {
            String group = keys.next();
            ArrayList<JSONObject> groupEvents = storyMap.get(group);
            int nActors = JsonStoryUtil.countActors(groupEvents);
            int nMentions = JsonStoryUtil.countMentions(groupEvents);
           // System.out.println("group = " + group);
            JsonSerialization.writeJsonObjectArrayWithStructuredData(trigFolder, group, project,
                    groupEvents, rawTextArrayList, groupEvents.size(), 1, nActors, nMentions, "polls", structuredEvents);

        }
    }


}

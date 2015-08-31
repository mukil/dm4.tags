package de.deepamehta.plugins.tags;

import java.util.logging.Logger;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.Consumes;
import javax.ws.rs.WebApplicationException;

import de.deepamehta.core.Topic;
import de.deepamehta.core.model.TopicModel;
import de.deepamehta.core.RelatedTopic;
import de.deepamehta.core.model.ChildTopicsModel;
import de.deepamehta.core.model.RelatedTopicModel;
import de.deepamehta.core.model.SimpleValue;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import de.deepamehta.core.osgi.PluginActivator;
import de.deepamehta.core.service.Inject;
import de.deepamehta.core.service.ResultList;
import de.deepamehta.core.storage.spi.DeepaMehtaTransaction;
import de.deepamehta.plugins.tags.service.TaggingService;
import de.deepamehta.plugins.workspaces.service.WorkspacesService;
import java.util.*;


/**
 * A basic plugin-service for fetching topics in DeepaMehta 4 by type and <em>one</em> or <em>many</em> tags.
 *
 * @author Malte Reißig (<malte@mikromedia.de>)
 * @website http://github.com/mukil/dm4.tags
 * @version 1.3.9-SNAPSHOT compatible with DeepaMehta 4.6.x
 *
 */

@Path("/tag")
@Consumes("application/json")
@Produces("text/html")
public class TaggingPlugin extends PluginActivator implements TaggingService {

    private Logger log = Logger.getLogger(getClass().getName());

    // --- DeepaMehta Standard URIs

    private final static String CHILD_URI = "dm4.core.child";
    private final static String PARENT_URI = "dm4.core.parent";
    private final static String AGGREGATION = "dm4.core.aggregation";

    // --- Additional View Model URIs

    public static final String VIEW_RELATED_COUNT_URI = "view_related_count";
    public static final String VIEW_CSS_CLASS_COUNT_URI = "view_css_class";

    @Inject /** Used by Migration3 */
    WorkspacesService workspaceService;
    
    /**
     * Fetches all topics of given type "aggregating" the "Tag" with the given <code>tagId</code>.
     *
     * @param   tagId               An id ot a "dm4.tags.tag"-Topic
     * @param   relatedTopicTypeUri A type_uri of a composite (e.g. "org.deepamehta.resources.resource")
     *                              which aggregates one or many "dm4.tags.tag".
     *
     * Note:                        This method provides actually no real benefit for developers familiar with the
     *                              getRelatedTopics() of the deepamehta-core API. It's just a convenient call.
     */

    @GET
    @Path("/{tagId}/{relatedTypeUri}")
    @Produces("application/json")
    @Override
    public ResultList<RelatedTopic> getTopicsByTagAndTypeURI(@PathParam("tagId") long tagId,
        @PathParam("relatedTypeUri") String relatedTopicTypeUri) {
        ResultList<RelatedTopic> all_results = null;
        try {
            Topic givenTag = dms.getTopic(tagId);
            all_results = givenTag.getRelatedTopics(AGGREGATION, CHILD_URI,
                    PARENT_URI, relatedTopicTypeUri, 0);
            return all_results;
        } catch (Exception e) {
            throw new WebApplicationException(new RuntimeException("Something went wrong fetching tagged topics", e));
        }
    }

    /**
     * Fetches all topics of given type "aggregating" all given "Tag"-<code>Topics</code>.
     *
     * @param   tags                A JSONObject containing JSONArray ("tags") of "Tag"-Topics is expected
     *                              (e.g. { "tags": [ { "id": 1234 } ] }).
     * @param   relatedTopicTypeUri A type_uri of a composite (e.g. "org.deepamehta.resources.resource")
     *                              which must aggregate one or many "dm4.tags.tag".
     */

    @POST
    @Path("/by_many/{relatedTypeUri}")
    @Consumes("application/json")
    @Produces("application/json")
    @Override
    public ResultList<RelatedTopic> getTopicsByTagsAndTypeUri(String tags, @PathParam("relatedTypeUri")
            String relatedTopicTypeUri) {
        ResultList<RelatedTopic> result = null;
        try {
            JSONObject tagList = new JSONObject(tags);
            if (tagList.has("tags")) {
                JSONArray all_tags = tagList.getJSONArray("tags");
                // 1) if this method is called with more than 1 tag, we proceed with
                if (all_tags.length() > 1) {
                    // 2) fetching all topics related to the very first tag given
                    JSONObject tagOne = all_tags.getJSONObject(0);
                    long first_id = tagOne.getLong("id");
                    Topic givenTag = dms.getTopic(first_id);
                    result = givenTag.getRelatedTopics(AGGREGATION, CHILD_URI, PARENT_URI,
                            relatedTopicTypeUri, 0);
                    // 3) Iterate over all topics tagged with this (one) tag
                    Set<RelatedTopic> missmatches = new LinkedHashSet<RelatedTopic>();
                    Iterator<RelatedTopic> iterator = result.iterator();
                    while (iterator.hasNext()) {
                        // 4) To check on each resource if it does relate to ALL given tags
                        RelatedTopic resource = iterator.next();
                        remove:
                        for (int i=1; i < all_tags.length(); i++) {
                            JSONObject tag = all_tags.getJSONObject(i);
                            long t_id = tag.getLong("id");
                            // Topic tag_to_check = dms.getTopic(t_id, false);
                            if (!hasRelatedTopicTag(resource, t_id)) { // if just one tag is missing, mark for removal
                                missmatches.add(resource);
                                break remove;
                            }
                        }
                    }
                    // 5) remove all "not-matching" items from our initial resultset
                    for (Iterator<RelatedTopic> it = missmatches.iterator(); it.hasNext();) {
                        RelatedTopic topic = it.next();
                        result.getItems().remove(topic);
                        // 6) check if any "not-matching" items is still part of our resultset (doubling)
                        if (result.getItems().contains(topic)) {
                            log.warning("DATA INCONSISTENCY:" + topic.getId() + " has two associations to the first "
                                + "given-tag ("+givenTag.getSimpleValue() +")");
                        }
                    }
                    return result;
                } else {
                    // fixme: tags-array may contain < 0 items
                    JSONObject tagOne = all_tags.getJSONObject(0);
                    long first_id = tagOne.getLong("id");
                    return getTopicsByTagAndTypeURI(first_id, relatedTopicTypeUri); // and pass it on
                }
            }
            throw new IllegalArgumentException("no tags given");
        } catch (JSONException ex) {
            throw new RuntimeException("error while parsing given parameters", ex);
        } catch (WebApplicationException e) {
            throw new RuntimeException("something went wrong", e);
        }
    }

    /**
     * Produces some JSON data (array) to produce an interactive tag cloud.
     * Getting {"value", "type_uri", "id" and "related_count:"} values of (interesting) topics in range.
     * 
     * @param   relatedTopicTypeUri Type URI of related Topic Type counted.
     */

    @GET
    @Path("/with_related_count/{related_type_uri}")
    @Produces("application/json")
    public String getViewTagsModelWithRelatedCount(@PathParam("related_type_uri") String relatedTopicTypeUri) {
        //
        JSONArray results = new JSONArray();
        try {
            // 1) Fetch Resultset of Resources
            log.info("Counting all related topics of type \"" + relatedTopicTypeUri + "\"");
            ArrayList<Topic> prepared_topics = new ArrayList<Topic>();
            ResultList<RelatedTopic> all_tags = dms.getTopics(TAG_URI, 0);
            log.info("Identified " + all_tags.getSize() + " tags");
            // 2) Prepare view model of each result item
            Iterator<RelatedTopic> resultset = all_tags.getItems().iterator();
            while (resultset.hasNext()) {
                Topic in_question = resultset.next();
                int count = in_question.getRelatedTopics(AGGREGATION, CHILD_URI, PARENT_URI,
                        relatedTopicTypeUri, 0).getSize();
                enrichTopicViewModelAboutRelatedCount(in_question, count);
                prepared_topics.add(in_question);
            }
            // 3) sort all result-items by the number of related-topics (of given type)
            Collections.sort(prepared_topics, new Comparator<Topic>() {
                public int compare(Topic t1, Topic t2) {
                    int one = t1.getChildTopics().getInt(VIEW_RELATED_COUNT_URI);
                    int two = t2.getChildTopics().getInt(VIEW_RELATED_COUNT_URI);
                    if ( one < two ) return 1;
                    if ( one > two ) return -1;
                    return 0;
                }
            });
            // 4) Turn over to JSON Array and add a computed css-class (indicating the "weight" of a tag)
            for (Topic item : prepared_topics) { // 2) prepare resource items
                enrichTopicViewModelAboutCSSClass(item, item.getChildTopics().getInt(VIEW_RELATED_COUNT_URI));
                results.put(item.toJSON());
            }
            return results.toString();
        } catch (Exception e) {
            throw new RuntimeException("something went wrong", e);
        }
    }

    @Override
    public Topic createTagTopic(String name, String definition, boolean lowerCase) throws IllegalArgumentException {
        Topic topic = null;
        // 1 check for existence
        String strippedName = name.trim();
        if (lowerCase) strippedName = name.toLowerCase();
        Topic existingTag = dms.getTopic(TAG_LABEL_URI, new SimpleValue(strippedName));
        if (existingTag != null) {
            throw new IllegalArgumentException("A Tag with the name \""+strippedName+"\" already exists - NOT CREATED");
        }
        // 2 create
        DeepaMehtaTransaction tx = dms.beginTx();
        try {
            topic = dms.createTopic(new TopicModel(TAG_URI, new ChildTopicsModel()
                .put(TAG_LABEL_URI, strippedName).put(TAG_DEFINITION_URI, definition)));
            tx.success();
        } finally {
            tx.finish();
        }
        return topic;
    }
    
    /** 
     * @param   name    label of given tag
     * @param   caseSensitive   flag if to use toLowerCase when getting
     * @return  Topic    null if no tag with given name was found
     */
    @Override
    public Topic getTagTopic(String name, boolean caseSensitive) {
        Topic tagTopic = null;
        String tagName = name.trim();
        if (!caseSensitive) tagName = tagName.toLowerCase();
        Topic labelTopic = dms.getTopic(TAG_LABEL_URI, new SimpleValue(tagName));
        if (labelTopic != null) {
            tagTopic = labelTopic.getRelatedTopic("dm4.core.composition", "dm4.core.child", 
                "dm4.core.parent", TAG_URI);
        }
        return tagTopic;
    }


    /** Private Helper Methods */

    private void enrichTopicViewModelAboutRelatedCount(Topic resource, int count) {
        ChildTopicsModel resourceModel = resource.getChildTopics().getModel();
        resourceModel.put(VIEW_RELATED_COUNT_URI, count);
    }

    private void enrichTopicViewModelAboutCSSClass(Topic resource, int related_count) {
        ChildTopicsModel resourceModel = resource.getChildTopics().getModel();
        String className = "few";
        if (related_count > 5) className = "some";
        if (related_count > 15) className = "quitesome";
        if (related_count > 25) className = "more";
        if (related_count > 50) className = "many";
        if (related_count > 70) className = "manymore";
        resourceModel.put(VIEW_CSS_CLASS_COUNT_URI, className);
    }

    private boolean hasRelatedTopicTag(RelatedTopic resource, long tagId) {
        ChildTopicsModel topicModel = resource.getChildTopics().getModel();
        if (topicModel.has(TAG_URI)) {
            List<RelatedTopicModel> tags = topicModel.getTopics(TAG_URI);
            for (int i = 0; i < tags.size(); i++) {
                TopicModel resourceTag = tags.get(i);
                if (resourceTag.getId() == tagId) return true;
            }
        }
        return false;
    }

}

package de.deepamehta.tags;

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
import de.deepamehta.core.RelatedTopic;
import de.deepamehta.core.model.ChildTopicsModel;
import de.deepamehta.core.model.RelatedTopicModel;
import de.deepamehta.core.model.SimpleValue;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import de.deepamehta.core.osgi.PluginActivator;
import de.deepamehta.core.service.Inject;
import de.deepamehta.core.service.accesscontrol.AccessControlException;
import de.deepamehta.core.storage.spi.DeepaMehtaTransaction;
import de.deepamehta.workspaces.WorkspacesService;

import java.util.*;


/**
 * A basic plugin-service for fetching topics in DeepaMehta 4 by type and <em>one</em> or <em>many</em> tags.
 *
 * @author Malte Reißig (<malte@mikromedia.de>)
 * @version 1.3.10 compatible with DeepaMehta 4.8
 */

@Path("/tag")
@Consumes("application/json")
public class TaggingPlugin extends PluginActivator implements TaggingService {

    private Logger log = Logger.getLogger(getClass().getName());

    // --- DeepaMehta Standard URIs

    private final static String CHILD_URI = "dm4.core.child";
    private final static String PARENT_URI = "dm4.core.parent";
    private final static String AGGREGATION = "dm4.core.aggregation";

    // --- Service used in Migration3 to fix Workspace Assignments

    @Inject
    private WorkspacesService workspaceService;

    /**
     * Convenience method to fetch a list of topics (of a given <code>Topic Type</code>) using the "Tag"
     * with the given <code>topic id</code>.
     *
     * Note: This method provides no real benefit for developers familiar with the getRelatedTopics() of the
     * deepamehta-core API. It's just a convenient call.
     *
     * @param   tagId               A topic id ot a "dm4.tags.tag"-Topic
     * @param   relatedTopicTypeUri A Topic Type URI identifying a type of topics. The <code>Topic Type
     *                              Definition</code> is expected to have an <code>Aggregation Definition</code> with
     *                              cardinality set to <code>Many</code> to the Topic Type Tag (typeUri="dm4.tags.tag").
     * @return  A list of <code>RelatedTopic</code>s of the given type (identified by typeUri) and associated with the given Tag (identified by topic id).
     */
    @GET
    @Path("/{tagId}/{relatedTypeUri}")
    @Produces("application/json")
    @Override
    public List<RelatedTopic> getTopicsByTagAndTypeURI(@PathParam("tagId") long tagId,
        @PathParam("relatedTypeUri") String relatedTopicTypeUri) {
        List<RelatedTopic> all_results = null;
        try {
            Topic givenTag = dm4.getTopic(tagId);
            all_results = givenTag.getRelatedTopics(AGGREGATION, CHILD_URI, PARENT_URI, relatedTopicTypeUri);
            return all_results;
        } catch (Exception e) {
            throw new WebApplicationException(new RuntimeException("Something went wrong fetching tagged topics", e));
        }
    }

    /**
     * Convenience method to fetch a list of topics (of a given <code>Topic Type</code>) using a list of "Tags".
     *
     * @param   tags                A String of a JSONObject containing a JSONArray (under the
     *                              </code>key="tags"</code>) of "dm4.tags.tag"-Topics is expected.
     *                              For example: <code>"{ "tags": [ { "id": 1234 } ] }"</code>).
     * @param   relatedTopicTypeUri A type_uri of a composite (e.g. "org.deepamehta.resources.resource")
     *                              which must aggregate one or many "dm4.tags.tag".
     * @return  A list of <code>RelatedTopic</code>s of the given type (identified by typeUri) and associated with a list of tags (identified by string encoded JSON Array of topic ids).     */
    @POST
    @Path("/by_many/{relatedTypeUri}")
    @Consumes("application/json")
    @Produces("application/json")
    @Override
    public List<RelatedTopic> getTopicsByTagsAndTypeUri(String tags, @PathParam("relatedTypeUri")
            String relatedTopicTypeUri) {
        List<RelatedTopic> result = null;
        try {
            JSONObject tagList = new JSONObject(tags);
            if (tagList.has("tags")) {
                JSONArray all_tags = tagList.getJSONArray("tags");
                // 1) if this method is called with more than 1 tag, we proceed with
                if (all_tags.length() > 1) {
                    // 2) fetching all topics related to the very first tag given
                    JSONObject tagOne = all_tags.getJSONObject(0);
                    long first_id = tagOne.getLong("id");
                    Topic givenTag = dm4.getTopic(first_id);
                    result = givenTag.getRelatedTopics(AGGREGATION, CHILD_URI, PARENT_URI, relatedTopicTypeUri);
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
                            // Topic tag_to_check = dm4.getTopic(t_id, false);
                            if (!hasRelatedTopicTag(resource, t_id)) { // if just one tag is missing, mark for removal
                                missmatches.add(resource);
                                break remove;
                            }
                        }
                    }
                    // 5) remove all "not-matching" items from our initial resultset
                    for (Iterator<RelatedTopic> it = missmatches.iterator(); it.hasNext();) {
                        RelatedTopic topic = it.next();
                        result.remove(topic);
                        // 6) check if any "not-matching" items is still part of our resultset (doubling)
                        if (result.contains(topic)) {
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
     * Generate a JSONArray with properties helpful to produce an interactive tag cloud.
     * 
     * @param   relatedTopicTypeUri Type URI of related Topic Type counted.
     * @return  String  Containing a JSON Object with some properties helpful for visualization (including CSS class
     * names) all tags regarding a specific topic type (identified by typeUri) which aggregates one or many tags.
     */
    @GET
    @Path("/with_related_count/{related_type_uri}")
    @Produces("application/json")
    @Override
    public List<TagViewModel> getViewTagsModelWithRelatedCount(@PathParam("related_type_uri") String relatedTopicTypeUri) {
        //
        try {
            // 1) Fetch Resultset of Resources
            log.info("Counting all related topics of type \"" + relatedTopicTypeUri + "\"");
            ArrayList<TagViewModel> result = new ArrayList<TagViewModel>();
            List<Topic> all_tags = dm4.getTopicsByType(TAG);
            log.info("Fetching " + all_tags.size() + " tags overall"); // ### Optimize..
            // 2) Prepare view model of each result item
            Iterator<Topic> resultset = all_tags.iterator();
            while (resultset.hasNext()) {
                Topic in_question = resultset.next();
                int count = in_question.getRelatedTopics(AGGREGATION, CHILD_URI, PARENT_URI,
                    relatedTopicTypeUri).size();
                TagViewModel tagView = new TagViewModel();
                tagView.setTopicModel(in_question);
                tagView.setViewRelatedCount(count);
                tagView.setViewStyleClass();
                result.add(tagView);
            }
            // 3) sort all result-items by the number of related-topics (of given type)
            Collections.sort(result, new Comparator<TagViewModel>() {
                public int compare(TagViewModel t1, TagViewModel t2) {
                    int one = t1.getViewRelatedCount();
                    int two = t2.getViewRelatedCount();
                    if ( one < two ) return 1;
                    if ( one > two ) return -1;
                    return 0;
                }
            });
            return result;
        } catch (Exception e) {
            throw new RuntimeException("something went wrong", e);
        }
    }

    /**
     * Creates a topic of type "dm4.tags.tag" after checking for existence by name and trimming the name.
     * If a tag with the given name ("dm4.tags.label") already exists, an <code>IllegalArgumentException</code> is
     * thrown.
     *
     * @param name          Label of the tag.
     * @param definition    Definition the label stands for.
     * @param lowerCase     Convert given name with a <code>.toLowerCase()</code> call for the existence check.
     *
     * @return Topic        The complete tag topic created.
     **/
    @Override
    public Topic createTagTopic(String name, String definition, boolean lowerCase) throws IllegalArgumentException {
        Topic topic = null;
        // 1 check for existence
        Topic existingTag = getTagTopic(name, true);
        if (existingTag != null) {
            throw new IllegalArgumentException("A Tag with the name \""+name.trim()+"\" already exists - NOT CREATED");
        }
        // 2 create
        DeepaMehtaTransaction tx = dm4.beginTx();
        try {
            topic = dm4.createTopic(mf.newTopicModel(TAG, mf.newChildTopicsModel()
                .put(LABEL_URI, name.trim()).put(DEFINITION_URI, definition)));
            tx.success();
        } finally {
            tx.finish();
        }
        return topic;
    }
    
    /**
     * Returns a topic (typeUri="dm4.tags.tag") with the given name.
     *
     * @param   name    label of given tag
     * @param   caseSensitive   flag if to use toLowerCase when getting
     * @return  Topic   The topic or <code>null</code> if no topic was found under the given name.
     */
    @Override
    public Topic getTagTopic(String name, boolean caseSensitive) {
        Topic tagTopic = null;
        String tagName = name.trim();
        if (!caseSensitive) tagName = tagName.toLowerCase();
        Topic labelTopic = null;
        try {
            // This getTopic-call might throw an AccessControlException wrapped into a RuntimeException
            labelTopic = dm4.getTopicByValue(LABEL_URI, new SimpleValue(tagName));
            if (labelTopic != null) {
                tagTopic = labelTopic.getRelatedTopic("dm4.core.composition", "dm4.core.child", 
                    "dm4.core.parent", TAG);
            }
        } catch (RuntimeException ex) {
            if (hasNestedAccessControlException(ex)) {
                log.warning("A Tag topic was found (value="+name+", caseSensitive="+caseSensitive+") but the requesting"
                    + " user has no permission to access and use label ("+labelTopic+")or tag topic ("+tagTopic+") "
                    + ex.getCause().getLocalizedMessage());
            } else {
                throw new RuntimeException(ex);
            }
        }
        return tagTopic;
    }


    /** Private Helper Methods */

    private boolean hasNestedAccessControlException(Throwable e) {
        while (e != null) {
            if (e instanceof AccessControlException) {
                return true;
            }
            e = e.getCause();
        }
        return false;
    }

    private boolean hasRelatedTopicTag(RelatedTopic resource, long tagId) {
        ChildTopicsModel topicModel = resource.getChildTopics().getModel();
        if (topicModel.getTopicsOrNull(TAG) != null) {
            List<? extends RelatedTopicModel> tags = topicModel.getTopics(TAG);
            for (int i = 0; i < tags.size(); i++) {
                RelatedTopicModel resourceTag = tags.get(i);
                if (resourceTag.getId() == tagId) return true;
            }
        }
        return false;
    }

}

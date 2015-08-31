package de.deepamehta.plugins.tags.service;


import de.deepamehta.core.RelatedTopic;
import de.deepamehta.core.Topic;
import de.deepamehta.core.service.PluginService;
import de.deepamehta.core.service.ResultList;
import org.codehaus.jettison.json.JSONArray;

/**
 * A basic plugin-service for fetching topics in DeepaMehta 4.
 *
 * @author Malte Reißig (<malte@mikromedia.de>)
 * @website http://github.com/mukil/dm4.tags
 * @version 1.3.9-SNAPSHOT compatible with DeepaMehta 4.6.x
 *
 */

public interface TaggingService extends PluginService {
    
    public static final String TAG_URI = "dm4.tags.tag";
    public static final String TAG_LABEL_URI = "dm4.tags.label";
    public static final String TAG_DEFINITION_URI = "dm4.tags.definition";

    ResultList<RelatedTopic> getTopicsByTagAndTypeURI(long tagId, String relatedTypeUri);

    ResultList<RelatedTopic> getTopicsByTagsAndTypeUri(String tags, String relatedTypeUri);

    String getViewTagsModelWithRelatedCount(String relatedTypeUri);

    Topic createTagTopic(String name, String definition, boolean lowerCase);

    Topic getTagTopic(String name, boolean caseSensitive);

}

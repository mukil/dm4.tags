package de.deepamehta.plugins.tags;


import de.deepamehta.core.RelatedTopic;
import de.deepamehta.core.Topic;
import java.util.List;

/**
 * A basic plugin-service for fetching and creating tags in DeepaMehta 4.
 *
 * @author Malte Reißig (<malte@mikromedia.de>)
 * @website http://github.com/mukil/dm4.tags
 * @version 1.3.9-SNAPSHOT compatible with DeepaMehta 4.7.x
 *
 */

public interface TaggingService {
    
    public static final String TAG = "dm4.tags.tag";
    public static final String LABEL_URI = "dm4.tags.label";
    public static final String DEFINITION_URI = "dm4.tags.definition";

    List<RelatedTopic> getTopicsByTagAndTypeURI(long tagId, String relatedTypeUri);

    List<RelatedTopic> getTopicsByTagsAndTypeUri(String tags, String relatedTypeUri);

    String getViewTagsModelWithRelatedCount(String relatedTypeUri);

    Topic createTagTopic(String name, String definition, boolean lowerCase);

    Topic getTagTopic(String name, boolean caseSensitive);

}

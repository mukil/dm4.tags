package de.deepamehta.tags;


import de.deepamehta.core.RelatedTopic;
import de.deepamehta.core.Topic;
import java.util.List;

/**
 * A basic plugin-service for fetching and creating tags in DeepaMehta 4.
 *
 * @author Malte Reißig (<malte@mikromedia.de>)
 * @version 1.3.10 compatible with DeepaMehta 4.8
 */
public interface TaggingService {
    
    public static final String TAG = "dm4.tags.tag";
    public static final String LABEL_URI = "dm4.tags.label";
    public static final String DEFINITION_URI = "dm4.tags.definition";

    List<RelatedTopic> getTopicsByTagAndTypeURI(long tagId, String relatedTypeUri);

    List<RelatedTopic> getTopicsByTagsAndTypeUri(String tags, String relatedTypeUri);

    List<TagViewModel> getViewTagsModelWithRelatedCount(String relatedTypeUri);

    Topic createTagTopic(String name, String definition, boolean lowerCase);

    Topic getTagTopic(String name, boolean caseSensitive);

}

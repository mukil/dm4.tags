package systems.dmx.tags2;


import java.util.List;
import systems.dmx.core.RelatedTopic;
import systems.dmx.core.Topic;

/**
 * A basic plugin-service for fetching and creating tags in DeepaMehta 4.
 *
 * @author Malte Reißig (<malte@mikromedia.de>)
 * @version 1.3.11 compatible with DeepaMehta 4.9
 */
public interface TaggingService {
    
    public static final String TAG = "dmx.tags.tag2";
    public static final String LABEL_URI = "dmx.tags.label";
    public static final String DEFINITION_URI = "dmx.tags.definition";

    List<RelatedTopic> getTopicsByTagAndTypeURI(long tagId, String relatedTypeUri);

    List<RelatedTopic> getTopicsByTagsAndTypeUri(String tags, String relatedTypeUri);

    List<TagViewModel> getViewTagsModelWithRelatedCount(String relatedTypeUri);

    Topic createTagTopic(String name, String definition, boolean lowerCase);

    Topic getTagTopic(String name, boolean caseSensitive);

}

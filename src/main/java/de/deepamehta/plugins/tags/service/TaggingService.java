package de.deepamehta.plugins.tags.service;


import de.deepamehta.core.RelatedTopic;
import de.deepamehta.core.Topic;
import de.deepamehta.core.service.PluginService;
import de.deepamehta.core.service.ResultList;

/**
 * A basic plugin-service for fetching topics in DeepaMehta 4.
 *
 * @author Malte Reißig (<malte@mikromedia.de>)
 * @website http://github.com/mukil/dm4.tags
 * @version 1.3.8 compatible with DeepaMehta 4.4
 *
 */

public interface TaggingService extends PluginService {

  ResultList<RelatedTopic> getTopicsByTagAndTypeURI(long tagId, String relatedTypeUri);

  ResultList<RelatedTopic> getTopicsByTagsAndTypeUri(String tags, String relatedTypeUri);

  String getViewTagsModelWithRelatedCount(String relatedTypeUri);
  
  Topic createTagTopic(String name, String definition);
  
  Topic getTagTopic(String name, boolean caseSensitive);

}

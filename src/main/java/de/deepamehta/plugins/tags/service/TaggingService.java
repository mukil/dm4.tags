package de.deepamehta.plugins.tags.service;


import de.deepamehta.core.RelatedTopic;
import de.deepamehta.core.ResultSet;
import de.deepamehta.core.model.TopicModel;
import de.deepamehta.core.service.ClientState;
import de.deepamehta.core.service.PluginService;

/**
 * A basic plugin-service for fetching topics in DeepaMehta 4.
 *
 * @author Malte Reißig (<malte@mikromedia.de>)
 * @website http://github.com/mukil/dm4.tags
 * @version 1.1
 *
 */

public interface TaggingService extends PluginService {

  ResultSet<RelatedTopic> getTopicsByTagAndTypeURI(long tagId, String relatedTypeUri, ClientState clientState);

  ResultSet<RelatedTopic> getTopicsByTagsAndTypeUri(String tags, String relatedTypeUri, ClientState clientState);

}

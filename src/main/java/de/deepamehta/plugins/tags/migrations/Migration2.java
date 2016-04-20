package de.deepamehta.plugins.tags.migrations;

import de.deepamehta.core.TopicType;
import de.deepamehta.core.ViewConfiguration;
import de.deepamehta.core.service.Migration;
import static de.deepamehta.plugins.tags.TaggingPlugin.TAG;

import java.util.logging.Logger;

/**
 * This migration extends existing (and new) "dm4.tags"-Installations about the new webclient renderers.
 */
public class Migration2 extends Migration {

    private Logger logger = Logger.getLogger(getClass().getName());

    @Override
    public void run() {
        // 
        TopicType tagType = dm4.getTopicType(TAG);
        ViewConfiguration viewConfig = tagType.getViewConfig();
        viewConfig.addSetting("dm4.webclient.view_config",
                "dm4.webclient.simple_renderer_uri", "dm4.tags.tag_simple_view");
        viewConfig.addSetting("dm4.webclient.view_config",
                "dm4.webclient.multi_renderer_uri", "dm4.tags.tag_multi_view");

    }

}
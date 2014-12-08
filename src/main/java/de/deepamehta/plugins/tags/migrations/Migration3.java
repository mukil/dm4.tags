package de.deepamehta.plugins.tags.migrations;

import de.deepamehta.core.Topic;
import de.deepamehta.core.TopicType;
import de.deepamehta.core.model.SimpleValue;
import de.deepamehta.core.model.TopicRoleModel;
import de.deepamehta.core.model.AssociationModel;
import de.deepamehta.core.service.Migration;

import java.util.logging.Logger;

public class Migration3 extends Migration {

    private Logger logger = Logger.getLogger(getClass().getName());

    private String TAG_URI = "dm4.tags.tag";
    private String TAG_LABEL_URI = "dm4.tags.label";
    private String TAG_DEFINITION_URI = "dm4.tags.definition";

    private String WS_DEFAULT_URI = "de.workspaces.deepamehta";

    @Override
    public void run() {

        TopicType tagType = dms.getTopicType(TAG_URI);
        TopicType tagLabelType = dms.getTopicType(TAG_LABEL_URI);
        TopicType tagDefinitionType = dms.getTopicType(TAG_DEFINITION_URI);
        //
        assignWorkspace(tagType);
        assignWorkspace(tagLabelType);
        assignWorkspace(tagDefinitionType);

    }

    // === Workspace ===

    /** Assign types to default workspace in any case. */

    private void assignWorkspace(Topic topic) {
        Topic defaultWorkspace = dms.getTopic("uri", new SimpleValue(WS_DEFAULT_URI));
        dms.createAssociation(new AssociationModel("dm4.core.aggregation",
            new TopicRoleModel(topic.getId(), "dm4.core.parent_type"),
            new TopicRoleModel(defaultWorkspace.getId(), "dm4.core.child_type")
        ));
    }

}
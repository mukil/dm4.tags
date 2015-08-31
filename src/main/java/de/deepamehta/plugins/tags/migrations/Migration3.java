package de.deepamehta.plugins.tags.migrations;

import de.deepamehta.core.Topic;
import de.deepamehta.core.TopicType;
import de.deepamehta.core.service.Inject;
import de.deepamehta.core.service.Migration;
import static de.deepamehta.plugins.tags.TaggingPlugin.TAG_DEFINITION_URI;
import static de.deepamehta.plugins.tags.TaggingPlugin.TAG_LABEL_URI;
import static de.deepamehta.plugins.tags.TaggingPlugin.TAG_URI;
import de.deepamehta.plugins.workspaces.service.WorkspacesService;

import java.util.logging.Logger;

public class Migration3 extends Migration {

    private Logger logger = Logger.getLogger(getClass().getName());
    
    @Inject
    WorkspacesService workspaceService = null;

    @Override
    public void run() {
        TopicType tagType = dms.getTopicType(TAG_URI);
        TopicType tagLabelType = dms.getTopicType(TAG_LABEL_URI);
        TopicType tagDefinitionType = dms.getTopicType(TAG_DEFINITION_URI);
        //
        assignToDefaultWorkspace(tagType);
        assignToDefaultWorkspace(tagLabelType);
        assignToDefaultWorkspace(tagDefinitionType);

    }

    // === Workspace ===

    /** Assign types to default workspace in any case. */

    private void assignToDefaultWorkspace(Topic topic) {
        Topic defaultWorkspace = workspaceService.getWorkspace(
                WorkspacesService.DEEPAMEHTA_WORKSPACE_URI);
        workspaceService.assignToWorkspace(topic, defaultWorkspace.getId());
    }

}
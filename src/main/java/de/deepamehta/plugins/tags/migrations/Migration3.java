package de.deepamehta.plugins.tags.migrations;

import de.deepamehta.core.Topic;
import de.deepamehta.core.TopicType;
import de.deepamehta.core.service.Inject;
import de.deepamehta.plugins.tags.TaggingService;
import de.deepamehta.plugins.workspaces.WorkspacesService;
import de.deepamehta.core.service.Migration;

import java.util.logging.Logger;

public class Migration3 extends Migration {

    @Inject
    private WorkspacesService workspaceService;

    @Override
    public void run() {

        Topic deepaMehtaWs = workspaceService.getWorkspace(WorkspacesService.DEEPAMEHTA_WORKSPACE_URI);
        //
        TopicType tagType = dms.getTopicType(TaggingService.TAG_URI);
        TopicType tagLabelType = dms.getTopicType(TaggingService.TAG_LABEL_URI);
        TopicType tagDefinitionType = dms.getTopicType(TaggingService.TAG_DEFINITION_URI);
        //
        workspaceService.assignTypeToWorkspace(tagType, deepaMehtaWs.getId());
        workspaceService.assignTypeToWorkspace(tagLabelType, deepaMehtaWs.getId());
        workspaceService.assignTypeToWorkspace(tagDefinitionType, deepaMehtaWs.getId());

    }

}
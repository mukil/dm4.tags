package de.deepamehta.tags.migrations;

import de.deepamehta.core.Topic;
import de.deepamehta.core.TopicType;
import de.deepamehta.core.model.IndexMode;
import de.deepamehta.core.service.Inject;
import de.deepamehta.tags.TaggingService;
import de.deepamehta.workspaces.WorkspacesService;
import de.deepamehta.core.service.Migration;


/**
 * This migration does type assignment to the "DeepaMehta" Workspace and adds an IndexMode.FULLTEY_KEY to "Tag Names".
 * */
public class Migration3 extends Migration {

    @Inject
    private WorkspacesService workspaceService;

    @Override
    public void run() {

        // 1) Assign types to "DeepaMehta" workspace
        Topic deepaMehtaWs = workspaceService.getWorkspace(WorkspacesService.DEEPAMEHTA_WORKSPACE_URI);
        TopicType tagType = dm4.getTopicType(TaggingService.TAG);
        TopicType tagLabelType = dm4.getTopicType(TaggingService.LABEL_URI);
        TopicType tagDefinitionType = dm4.getTopicType(TaggingService.DEFINITION_URI);
        workspaceService.assignTypeToWorkspace(tagType, deepaMehtaWs.getId());
        workspaceService.assignTypeToWorkspace(tagLabelType, deepaMehtaWs.getId());
        workspaceService.assignTypeToWorkspace(tagDefinitionType, deepaMehtaWs.getId());

        // 2) Add "Tag Name" Fulltext key index
        tagLabelType.addIndexMode(IndexMode.FULLTEXT_KEY);

    }

}
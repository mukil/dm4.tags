package systems.dmx.tags2.migrations;

import systems.dmx.core.Topic;
import systems.dmx.core.TopicType;
import systems.dmx.core.service.Inject;
import systems.dmx.core.service.Migration;
import systems.dmx.tags2.TaggingService;
import systems.dmx.workspaces.WorkspacesService;


/**
 * This migration does type assignment to the "DeepaMehta" Workspace and adds an IndexMode.FULLTEY_KEY to "Tag Names".
 * */
public class Migration2 extends Migration {

    @Inject
    private WorkspacesService workspaceService;

    @Override
    public void run() {

        // 1) Assign types to "DMX" workspace
        Topic dmxWs = workspaceService.getWorkspace(WorkspacesService.DMX_WORKSPACE_URI);
        TopicType tagType = dmx.getTopicType(TaggingService.TAG);
        TopicType tagLabelType = dmx.getTopicType(TaggingService.LABEL_URI);
        TopicType tagDefinitionType = dmx.getTopicType(TaggingService.DEFINITION_URI);
        workspaceService.assignTypeToWorkspace(tagType, dmxWs.getId());
        workspaceService.assignTypeToWorkspace(tagLabelType, dmxWs.getId());
        workspaceService.assignTypeToWorkspace(tagDefinitionType, dmxWs.getId());

    }

}
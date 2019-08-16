package systems.dmx.tags2;


import java.util.logging.Level;
import java.util.logging.Logger;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import systems.dmx.core.JSONEnabled;
import systems.dmx.core.Topic;

/**
 *
 * @author malted
 */
public class TagViewModel implements JSONEnabled {

    // --- Additional View Model URIs

    public static final String VIEW_RELATED_COUNT_URI = "view_related_count";
    public static final String VIEW_CSS_CLASS_COUNT_URI = "view_css_class";

    JSONObject json = new JSONObject();

    public void setTopicModel(Topic object) {
        json = object.toJSON();
    }

    public long getTopicId() {
        try {
            return json.getLong("id");
        } catch (JSONException ex) {
            Logger.getLogger(TagViewModel.class.getName()).log(Level.SEVERE, null, ex);
            return -1;
        }
    }

    public void setViewRelatedCount(int relatedCount) {
        try {
            json.put(VIEW_RELATED_COUNT_URI, relatedCount);
        } catch (JSONException ex) {
            Logger.getLogger(TagViewModel.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void setViewStyleClass() {
        String className = "few";
        if (getViewRelatedCount() > 5) className = "some";
        if (getViewRelatedCount() > 15) className = "quitesome";
        if (getViewRelatedCount() > 25) className = "more";
        if (getViewRelatedCount() > 50) className = "many";
        if (getViewRelatedCount() > 70) className = "manymore";
        try {
            json.put(VIEW_CSS_CLASS_COUNT_URI, className);
        } catch (JSONException ex) {
            Logger.getLogger(TagViewModel.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public int getViewRelatedCount() {
        try {
            return json.getInt(VIEW_RELATED_COUNT_URI);
        } catch (JSONException ex) {
            Logger.getLogger(TagViewModel.class.getName()).log(Level.SEVERE, null, ex);
        }
        return -1;
    }

    @Override
    public JSONObject toJSON() {
        return json;
    }

}

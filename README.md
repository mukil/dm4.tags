
# DeepaMehta 4 Tags Module

A DeepaMehta 4 plugin introducing the TopicType _Tag_ (`dm4.tags.tag`) to extend any DeepaMehta 4 _Topic_- or _Association Type_ about a GUI to enter _one_ or _many_ related `Tag/s`.

## Download & Installation

You can download a bundle file for installation at [download.deepamehta.de/nightly](http://download.deepamehta.de).

Place the downloaded `dm42-deepamehta-tags-1.3.6.jar`-file in the `bundles` folder of your DeepaMehta installation and restart DeepaMehta.

## Usage Tags as a user (interactive setup)

Before you are able to start **using** `Tags` you must accomplish the following, non-trivial task of telling your DeepaMehta installation what exactly is going to be tagged and if it will be tagged with just one or with many tags. So here we go.

'''First''', decide what type of information you want to tag with, e.g. "beerware". In this example we are going to enrich `Web Resources` and `Notes` about `Tags` so information of both types can share the same `tags` (and thus can be grouped and navigated together)!

'''Then''', reveal the Topic Type `Web Resource` whose edit form we intend to extend about _many_ input fields of type `Tag`. The easiest way to do so is to select an existing `Web Resource` topic and reveal the topic representing it's type (which is related to all web resource in DeepaMehta via an association of type `Instantiation`). Thus the searched for item is visible every time you select a _web resource_ in DeepaMehta. The Topic Type `Web Resource` is represented by a blue type icon and simply named, guess what `Web Resource`.

Whe you've selected the blue _Topic Type_ called `Web Resource` you additionally need to reveal the newly installed Topic Type `Tag` so you are able to associate both types with each other. In our example the association between this blue `Web Resource` and this blue `Tag` topic needs to be set up to represent the following details about the relationship between these 2 items: A `Tag` plays the `Child Type` related to a `Web Resource` which plays the `Parent Type` respectively. The type of their relationshop is an `Aggregation Definition`. 

An `Aggregation Definition` is just the expression for: "one single tag can be _part_ of one or many web resources without being deleted when the web resource is deleted". So the `Aggregation Definition` is the correct choice for us here because it expresses that a tag "lives" independently from a web resource. If you can see, have a look at the screenshot I produced from this stage, the association is selected and the picture renders exactly what i've described up to now.

![screenshot1](https://github.com/mukil/dm4.tags/raw/master/configuring_dm4.tagging-1.1.png)
![screenshot2](https://github.com/mukil/dm4.tags/raw/master/configuring_dm4.tagging.1.1_Bild2.png)

After having come so far, all your existing and new `Web Resources` can be _edited_ so that they contain a single `Tag`. And if you want to add _many_ tags to your web resources you just have to `Edit` the Topic Type `Web Resource` itself and set the value `One` (which appears next to `Tag` in the edit form) to the value `Many` and save your newly extended Topic Type `Web Resource`.

Congratulations! You just told DeepaMehta the way you would like to organize and think about a `Web Resource`. Each of your web resources can now also be made of `Tags`. And thus, a web resource tagged with many tags can be found under many different labels, or let's say: in more than one folder of your firefox bookmarks-toolbar when you're trying to find it. Done this, done that. Now you can do the exactly the same with the Topic Type `Note`. You can now start to re-use the blue Topic Type `Tag` in as manys other types of information in DeepaMehta 4.

## Use Tags as a developer

Example: To setup this renderer as part of your model/migration you need to reference the `dm4.tags.tag` uri as an aggregated child topic of your composite topic. The easiest way to do so in an imperative migration would look like the following:

<pre>

public class Migration1 extends Migration {

    @Override
    public void run() {

        /** Enrich topicmap type `Web Resource` about many `Tag` fields */
        TopicType webResource = dms.getTopicType("dm4.webbrowser.web_resource");
        webResource.addAssocDef(new AssociationDefinitionModel("dm4.core.aggregation_def",
            "dm4.webbrowser.web_resource", "dm4.tags.tag", "dm4.core.one", "dm4.core.many"));
    }

}
</pre>


## GNU Public License

This DeepaMehta plugin is released under the terms of the GNU General Public License in Version 3.0, 2007. You can find a copy of that [here](http://www.gnu.org/licenses/gpl).

## Icons License

The label icon used by this plugin is "Free for commercial use" (Include link to authors website) and was designed by [Freeiconsweb](http://www.freeiconsweb.com/).

## Version History

**1.3.7-SNAPSHOT**, UPCOMING
- Introduced service method to generate a simple tag-cloud

**1.3.6**, Feb 28, 2014
- Finally fixing "icon-missing" issue which was not fixed in the 1.3.5 commit.

**1.3.5**, Feb 28, 2014
- Fixes "icon-missing" issue introduced in 1.3.4

**1.3.4**, Feb 28, 2014
- Compatible with DeepaMehta 4.2

1.2, 1.3.0, 1.3.1, 1.3.2, 1.3.3 - No information available

**1.1**, Mar 28, 2013
- Installing the `Tag` bundle automatically assigns the Type `Tag` to the default workspace `DeepaMehta`.
- updated READMe to DMs new Child / Parent wording

**1.1-SNAPSHOT**, Feb 28, 2013

- extended Tag to be composed of a "Label" and a "Definition"
- updated screenshot documentation in README

Note: Users of previous version must reset their DB.

**1.0**, Dec 3, 2012

initialization of this plugin.

- containing a migration registering `dm4.tags.tag`
- a detailed readme containing a short modeling tutorial and an imperative migration example for developers
- upload of binary release

-------------------------------
Author: Malte Reißig, 2012-2014


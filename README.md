
# DeepaMehta 4 Tags Module

A DeepaMehta 4 plugin realizing the use-case of tagging. This module allows users to tag topics. It comes with "auto-completion", allowing you to simply create and re-use (reference existing) tags.

It does so through introducing the Topic Type _Tag_ (`dm4.tags.tag`) with which one can _extend_ any DeepaMehta 4 _Topic_- or _Association Type_ interactively.

## Download & Installation

You can download a bundle file for installation at [download.deepamehta.de/nightly](http://download.deepamehta.de).

Copy the downloaded `dm4x-deepamehta-tags-1.x.x.jar`-file into the `bundles` folder of your DeepaMehta installation and restart DeepaMehta.

### Configuration

Note: The plugin comes with a custom renderer for _many_ `Tag/s`, so make sure you configure _Tags_ to be a "Child Type" of _many_, otherwise you won't see the auto-completion GUI.

Reveal the 

![screenshot1](https://github.com/mukil/dm4.tags/raw/master/help-configuring_notes-for-tagging-screen-1.png)

## Usage Tags as a user (interactive setup)

Before you are able to start **using** `Tags` you must tell your DeepaMehta installation which type of information you want to tag. So here we go.

In this example we are going to enrich `Web Resources` and `Notes` about `Tags` so information of both types can share the same `tags`. This way our bookmarks and notes share the same taxonomy!

Reveal the _Topic Type_ `Web Resource` (via "By Type"-Search). The _Topic Type_ is also visible every time you select a _Web Resource_ in DeepaMehta. In DeepaMehta 4 Topic Types always have a blue squared icon.

Whe you've selected the _Topic Type_ `Web Resource` you additionally need to reveal the newly installed Topic Type `Tag` so you are able to associate both types with each other. To draw an assocation between two topics you can use the context menu (available via the right mouse button) above one of the topics. In our example the association between these two topics needs to be set up with the following details: The type of their relationshop is an `Aggregation Definition` and a `Tag` plays the `Child Type` related to a `Web Resource` which plays the `Parent Type` respectively.

An `Aggregation Definition` is just the expression for: "one single tag can be _part_ of one or many web resources without being deleted when the web resource is deleted". So the `Aggregation Definition` is the correct choice for us here because it expresses that a tag "lives" independently from a web resource. When you select your newly configured association your screen should render exactly the following image.

![screenshot2](https://github.com/mukil/dm4.tags/raw/master/configuring_dm4.tagging-1.1.png)

To get the auto-completion field on your `Web resources` we must configure it to potentially reference _many_ tags and therefore we just have to `Edit` the Topic Type `Web Resource` itself and set the value `One` (which appears next to `Tag` in the edit form) to the value `Many`. Ok, and we're done.


![screenshot3](https://github.com/mukil/dm4.tags/raw/master/configuring_dm4.tagging.1.1_Bild2.png)

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

**1.3.10**, Jul 26, 2016
- Assigns types to "DeepaMehta" workspace during installation
- Adds fulltext index to "Tag Name"
- Compatible with DeepaMehta 4.8

**1.3.7**, Nov 17, 2014
- Introduced service method to generate a simple tag-cloud
- Compatible with DeepaMehta 4.3

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


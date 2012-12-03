# DeepaMehta Tags Module

A module for users who want to interactively extend a Topic or Association Types of their choice about _one_ or _many_ simple `Tag` field/s. To use this module solely makes it easier for developers of other plugins to share a taxonomy between various types of information, e.g. `Web Resource` and `Note`. The advantage of using this module over creating your own `Tag` type interactively is, developer of other plugins, e.g. a mail-application can rely on the namespace opened up by this pĺugin. This plugins was solely developed for backwards compatibility reasons, to simulare hierarchical structures like known from file-browsers or browser bookmarks ("reasons of familiarity for internet users who joined the network after the millenium"). It maybe useless very soon and it's sole existence remains to be discussed. At the same time it's an hommage to the first human being leaving a hand-mark made out of natural purpur-color on the surface stone over 30 thousand years ago, the latest `tag` know to humankind just stating `i was here` by tagging the stone.

# Install Tags for DeepaMehta 4

Download the DeepaMehta Tagging 1.0 Bundle. A [download is provided here](https://github.com/downloads/mukil/dm4.tags/deepamehta-tags-1.0.jar) in this repository, also filed under `Downloads`.

Place the downloaded file `deepamehta-tags-1.0.jar` in the `bundles`-folder of your deepamehta installation and restart DeepaMehta.

# Configure Tags for DeepaMehta 4 interactively

To start **using** `Tags` you must decide what type of information you want to start tagging. In this  example we are going to enrich `Web Resources` and `Notes` about `Tags` so instances of both types share the same `taxonomy` and thus can be grouped and navigated together via their shared `Tag`.

First, reveal the Topic Type `Web Resource` whoes edit form we intend to extend about _many_ input fields of type `Tag`. The easiest way to do so is to select an existing `Web Resource` topic and reveal the topic representing it's type, which is related to all web resource in DeepaMehta via an association of type `Instantiation` and thus visible in every info page of a web resource, represented by a blue type icon and simply named `Web Resource`. All this words may be irritating the first time, but if you once made it, all the magic of database development is gone, it's mostly about using the most describing terms/ identifying the best items, just like in tagging.

Whe you've selected the blue _Topic Type_ called `Web Resource` you additionally need to reveal the newly installed Topic Type `Tag` to be able to associate both types with each other. In our example, after being drawn on canvas, the association between this blue `Web Resource` and this blue `Tag` topic needs to be set up to represent the following details about the relationship between these 2 items: A `Tag` plays the `Part Type` related to a `Web Resource` which plays the `Whole Type` respectively. The type of their relationshop is an `Aggregation Definition`. An `Aggregation Definition` is just the expression for, if a single tag can be _part_ of one or many web resources without being deleted when the web resource is. In other words, the `Aggregation Definition` is the correct choice for us because it expresses that a tag "lives" independently from a web resource. If you can see, have a look at the screenshot I produced from this stage, the association is selected and the picture renders exactly what i've described up to now.

![screenshot](https://github.com/mukil/dm4.tags/raw/master/tagged_screen_640w.png)

After having come so far, all your existing and new `Web Resources` can be _edited_ so that they contain a single `Tag`. And if you want to add _many_ tags to your web resources you just have to `Edit` the Topic Type `Web Resource` itself and set the value `One` (which appears next to `Tag` in the edit form) to the value `Many` and save your newly extended Topic Type `Web Resource`. 

Congratulations! You just told DeepaMehta the way you would like to organize and think about a `Web Resource`. Each of your web resources can now also be made of `Tags`. And thus, a web resource tagged with many tags can be found under many different labels, or let's say: in more than one folder of your firefox bookmarks-toolbar when you're trying to find it.

# Use Tags as a developer 

Example: To setup this renderer as part of your model/migration you need to reference the `dm4.tags.tag` uri as an aggregated child topic of your composite topic. The easiest way to do so in an imperative migration would look like the following:

<pre>

public class Migration1 extends Migration {

    // -------------------------------------------------------------------------------------------------- Public Methods

    @Override
    public void run() {

        /** Enrich topicmap type `Web Resource` about many `Tag` fields */
        TopicType webResource = dms.getTopicType("dm4.webbrowser.web_resource", null);
        webResource.addAssocDef(new AssociationDefinitionModel("dm4.core.aggregation_def",
            "dm4.webbrowser.web_resource", "dm4.tags.tag", "dm4.core.one", "dm4.core.many"));
    }

}
</pre>

# GNU Public License

This DeepaMehta plugin is released under the terms of the GNU General Public License in Version 3.0, 2007. You can find a copy of that [here](http://www.gnu.org/licenses/gpl).

# Changelog

1.0-SNAPSHOT Dec 3, 2012

- initial commit and readme with installation hints

Copyright 2012, Malte Reißig


var TAG_URI = "dm4.tags.tag"

dm4c.add_multi_renderer('dm4.tags.tag_multi_view', {

    render_info: function (page_models, $parent) {

        var list = $('<ul class="tag-list">')
        for (var i = 0; i < page_models.length; i++) {
            var item = page_models[i].object
            if (typeof item !== "undefined") {
                if (item.id !== -1) {
                    var name = item.value
                    // give tag-item some standard behaviour
                    $listItem = $('<div id="' +item.id+ '"><img src="/de.deepamehta.tags/images/tag_32.png" '
                        + 'width="20" alt="Tag: '+name+'" title="Show Tag: '+name+'">'
                        + '<span class="tag-name">' + name + '</span></div>')
                    $listItem.click(function(e) {
                        var topicId = this.id
                        dm4c.do_reveal_related_topic(topicId, "show")
                    })
                    list.append($('<li class="tag-item">').html($listItem))
                }
            }
        }
        $parent.append('<div class="field-label">Tags</div>').append(list)

    },

    render_form: function (page_models, $parent) {

        var topicTags = page_models
        var allReadableTags = fetchAllTagTopics()
        var inputFieldValue = ""
        var commaCount = 1

        // assemble input line, adding all existing tags into our input-line
        for (var exist in topicTags) {
            var element = topicTags[exist]
            inputFieldValue += element.value
            if (commaCount < topicTags.length) inputFieldValue += ", "
            commaCount++
        }
        $parent.append('<div class="field-label">Tags (comma separated)</div>').append(
            '<input type="text" class="tags" value="' +inputFieldValue+ '"></input>')

        // activate third party library
        setupJQueryUIAutocompleteField('input.tags')

        // assemble tag topics to be returned
        return function () {
            var new_model = []
            var enteredTags = processTagInputField("input.tags")
            var resultingTags = []
            // create all new and collect existing tag (topics)
            for (var label in enteredTags) {
                var name = enteredTags[label]
                var tag = getMatchingTagTopic(name, allReadableTags)
                if (!tag) {
                    // create new topic
                    var newTag = dm4c.create_topic(TAG_URI, {"dm4.tags.label": name, "dm4.tags.definition" : ""})
                    resultingTags.push(newTag)
                } else {
                    // add existing topic to results
                    resultingTags.push(tag)
                }
            }
            // identify all tags (which were formerly there but are not in our input-field anymore these are)
            // to to be removed by reference
            for (var el in topicTags) {
                var element = topicTags[el].object.value
                var elementId = topicTags[el].object.id
                if (getMatchingTagTopic(element, resultingTags) == undefined) { // if
                    new_model.push(dm4c.DEL_ID_PREFIX + elementId)
                }
            }
            // build up model containg reference to all entered tags
            for (var item in resultingTags) {
                var topic_id = resultingTags[item].id
                if (topic_id !== -1) {
                    new_model.push(dm4c.REF_ID_PREFIX + topic_id)
                }
            }
            return new_model
        }

        function setupJQueryUIAutocompleteField (identifier) {
            $(identifier).bind("keydown", function( event ) {
                if ( event.keyCode === $.ui.keyCode.TAB && $( this ).data( "ui-autocomplete" ).menu.active ) {
                    event.preventDefault()
                } else if (event.keyCode === $.ui.keyCode.ENTER) {
                    // fixme: event.preventDefault()
                }
            }).autocomplete({minLength: 2,
                source: function( request, response ) {
                    console.log("Request", request, "Term Undefined?", request.term, "AllReadable Tags", allReadableTags)
                    // delegate back to autocomplete, but extract the last term
                    // As of 4.xx .filter throws an error...
                    response( $.ui.autocomplete.filter( allReadableTags, extractLast( request.term ) ) )
                },
                focus: function() {
                    // prevent value inserted on focus
                    return false;
                },
                select: function( event, ui ) {
                    var terms = split( this.value )
                    // remove the current input
                    terms.pop()
                    // add the selected item
                    terms.push( ui.item.value )
                    // add placeholder to get the comma-and-space at the end
                    terms.push( "" )
                    this.value = terms.join( ", " )
                    return false
                }
            })

            function split( val ) {return val.split( /,\s*/ ) }

            function extractLast( term ) {return split( term ).pop() }

        }

        function fetchAllTagTopics() {
            return dm4c.restc.get_topics(TAG_URI, false, false, 0)
            return dm4c.restc.get_topics(TAG_URI, false, false, 0)
        }

        function getMatchingTagTopic(label, listOfTagTopics) {
            for (var item in listOfTagTopics) {
                var tag = listOfTagTopics[item]
                if (tag.value.toLowerCase() === label.toLowerCase()) return tag
            }
            return undefined
        }

        function processTagInputField(fieldIdentifier) {
            // do a parameter check
            if ($(fieldIdentifier).children() == 0) {
                throw new Error ("Bad identifier given, can't access input field value")
            }
            // split user input into an array strictly by "," thus comma values in tag names are not permitted and cut
            var tagline = $(fieldIdentifier).val().split( /,\s*/ )
            if (!tagline) throw new Error("Tagging field got somehow broken, could not access text value")
            // iterate over all tag labels given and remove duplicates
            var uniqueLabels = []
            for (var i=0; i < tagline.length; i++) {
                var tagInput = tagline[i]
                // credits for the regexp go to user Bracketworks in:
                // http://stackoverflow.com/questions/154059/how-do-you-check-for-an-empty-string-in-javascript#154068
                if (tagInput.match(/\S/) !== null) { // remove empty strings
                    // we allow each tag just once
                    var qualified = true
                    for (var k=0; k < uniqueLabels.length; k++) {
                        var existingTag = uniqueLabels[k]
                        if (existingTag.toLowerCase() === tagInput.toLowerCase()) qualified = false
                    }
                    // so we make sure to add each tag just once (to our resultset)
                    if (qualified) uniqueLabels.push(tagInput)
                }
            }
            return uniqueLabels
        }

    }
})

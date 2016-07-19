
dm4c.add_simple_renderer('dm4.tags.tag_simple_view', {

    render_info: function (page_models, $parent) {

        // console.log("DEBUG: rendering simple tags INFO")
        // this is never used/called since tag is a composite object
        // see https://trac.deepamehta.de/ticket/540

        $parent.append('<div class="field-label warning">To get <em>Tags</em> working they must be set-up to be '
            + '<em>many</em> childs of your desired Topic Type.</div>')

    },

    render_form: function (page_models, $parent) {

        // console.log("DEBUG: rendering simple tags FORM")
        // this is never used/called since tag is a composite object
        // see https://trac.deepamehta.de/ticket/540

        $parent.append('<div class="field-label warning">To get <em>Tags</em> working they must be set-up to be '
            + '<em>many</em> childs of your desired Topic Type.</div>')

        return function () {
            return page_models
        }
    }
})

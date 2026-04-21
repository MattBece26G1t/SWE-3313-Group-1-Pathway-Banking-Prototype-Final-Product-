/**
 * HelpEntry represents one piece of help content in the program.
 *
 * Each HelpEntry object ties together:
 * - the section it belongs to, like dashboard or settings
 * - the type of help it is, like faq or interface
 * - the actual text that should be shown on the Help screen
 *
 * this class is just a clean way to store one help record
 * instead of juggling 3 separate strings everywhere.
 */

package org.example.pathwayver1;

public class HelpEntry {

    private String topic; // The section this help text belongs to
    private String category; // The help category for that section
    private String content; // The actual text content that gets displayed

    /*
     * Builds one help entry with all of its needed info.
     * This gets used when HelpManager loads all the built-in help text.
     */
    public HelpEntry(String topic, String category, String content) {
        this.topic = topic;
        this.category = category;
        this.content = content;
    }

    // Returns the section name this help entry belongs to.
    public String getTopic() {
        return topic;
    }

    /*
     * Returns the type of help this entry is.
     * Example: "faq" or "interface"
     */
    public String getCategory() {
        return category;
    }

    //  Returns the actual help text that should be shown on screen.
    public String getContent() {
        return content;
    }
}
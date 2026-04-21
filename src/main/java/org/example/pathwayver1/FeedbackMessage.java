/*
 * FeedbackMessage is just a small data class for scenario feedback.
 *
 * Each object stores one finished piece of feedback after the user makes
 * a scenario choice. So instead of passing around a bunch of separate values,
 * the program wraps them into one object and reuses that wherever needed.
 *
 * This class keeps:
 * - the actual feedback text shown to the user
 * - the category of feedback
 * - which scenario it came from
 * - the date it was created
 */

package org.example.pathwayver1;

public class FeedbackMessage {

    private String messageText; // Main feedback text, usually the outcome/explanation tied to a scenario choice
    private String feedbackType; // "Positive", "Cautionary", "Negative"
    private String relatedScenarioID; // Lets the program trace this feedback back to the scenario that created it
    private String date;  // Simple date stamp for when the feedback was generated

    /*
     * Builds one feedback message with all the values already known.
     * This gets used right after a scenario response is evaluated.
     */
    public FeedbackMessage(String messageText, String feedbackType,
                           String relatedScenarioID, String date) {
        this.messageText = messageText;
        this.feedbackType = feedbackType;
        this.relatedScenarioID = relatedScenarioID;
        this.date = date;
    }

    //  Returns the actual feedback text that will be shown to the user.
    public String getMessageText() {
        return messageText;
    }

    /*
     * Returns the overall feedback category.
     * The rest of the program uses this to decide things like
     * popup color or how the result should be interpreted.
     */
    public String getFeedbackType() {
        return feedbackType;
    }

    // Returns the scenario ID this feedback belongs to.
    public String getRelatedScenarioID() {
        return relatedScenarioID;
    }

    // Returns the date the feedback was created.
    public String getDate() {
        return date;
    }
}
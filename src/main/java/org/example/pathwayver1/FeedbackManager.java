/*
 * FeedbackManager is in charge of creating and storing scenario feedback.
 *
 * The main point of this class is to take the player's scenario choice,
 * look at the outcome/reward tied to that choice, and turn it into a
 * FeedbackMessage object that the rest of the program can show.
 *
 * So this class does two main things:
 * 1. builds feedback after a scenario choice is made
 * 2. keeps a history of feedback messages that were generated
 */

package org.example.pathwayver1;

import java.util.ArrayList;
import java.time.LocalDate;

public class FeedbackManager {

    // Stores every feedback message generated during the user's session
    private ArrayList<FeedbackMessage> feedbackHistory;

    //  Starts with an empty feedback history.
    public FeedbackManager() {
        this.feedbackHistory = new ArrayList<>();
    }

    /*
     * Builds a feedback message based on the scenario choice the user picked.
     *
     * choiceIndex lines up with the scenario's option lists, so the same index
     * is used to grab:
     * - the outcome text
     * - the reward value
     *
     * Then the reward amount is used to label the feedback as:
     * - Positive if money was gained
     * - Negative if money was lost
     * - Cautionary if there was no direct gain/loss
     *
     * After the FeedbackMessage is created, it also gets saved into the history list.
     */
    public FeedbackMessage generateScenarioFeedback(Scenario scenario, int choiceIndex) {
        String outcome = scenario.getOutcomes().get(choiceIndex);
        double reward = scenario.getRewards().get(choiceIndex);

        // Decide the overall tone of the feedback based on the reward tied to the choice
        String feedbackType;
        if (reward > 0) {
            feedbackType = "Positive";
        }
        else if (reward < 0) {
            feedbackType = "Negative";
        }
        else {
            feedbackType = "Cautionary";
        }

        FeedbackMessage feedback = new FeedbackMessage(
                outcome,
                feedbackType,
                scenario.getScenarioID(),
                LocalDate.now().toString()
        );

        // Save it so the app can keep a record of what feedback has been generated
        recordFeedback(feedback);
        return feedback;
    }

    /*
     * Adds one feedback message to the history list.
     * This is separated out so feedback can be stored manually too
     * if another part of the program ever needs that.
     */
    public void recordFeedback(FeedbackMessage feedback) {
        feedbackHistory.add(feedback);
    }

    // Returns the full feedback history.
    public ArrayList<FeedbackMessage> getFeedbackHistory() {
        return feedbackHistory;
    }

    /*
     * Filters the feedback history down to just one scenario.
     * This is useful if the program ever wants to look back at
     * all feedback tied to a specific scenario ID.
     */
    public ArrayList<FeedbackMessage> getFeedbackByScenario(String scenarioID) {
        ArrayList<FeedbackMessage> results = new ArrayList<>();
        for (FeedbackMessage fm : feedbackHistory) {
            if (fm.getRelatedScenarioID().equals(scenarioID)) {
                results.add(fm);
            }
        }
        return results;
    }
}
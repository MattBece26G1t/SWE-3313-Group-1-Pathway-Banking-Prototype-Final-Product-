/**
 * Scenario represents one financial decision activity in the program.
 *
 * Each scenario stores the basic information needed to present the activity
 * to the user, including its title, description, category, difficulty level,
 * response options, outcomes, and reward values.
 *
 * The design of this class is intentionally simple. It mainly acts as a data
 * model for the scenario system, while larger operations such as filtering,
 * completion tracking, and mandatory scenario handling are managed by
 * ScenarioManager and the related controllers.
 */

package org.example.pathwayver1;

import java.util.ArrayList;

public class Scenario {

    // Unique identifier used to distinguish one scenario from another
    private String scenarioID;
    // Short title shown in the scenario browser
    private String title;
    // Full scenario prompt shown when the activity is opened
    private String description;
    // General category, such as Emergency Expense or Everyday Spending
    private String category;
    // Difficulty level used to separate Beginner and Advanced content
    private String difficultyLevel;

    /*
     * These 3 lists are tied together by index.
     * For example:
     * option at index 0 -> outcome at index 0 -> reward at index 0
     * That is why addOption() adds to all 3 lists at the same time.
     */
    private ArrayList<String> options;
    private ArrayList<String> outcomes;
    private ArrayList<Double> rewards;

    // Marks whether this scenario can be triggered as a mandatory interruption
    private boolean mandatory;
    // Tracks the user's progress on this scenario
    private String completionStatus; // "Available", "In Progress", "Completed"

    /**
     * Creates a new scenario with its main identifying information.
     * New scenarios start with an "Available" status and empty lists for
     * options, outcomes, and rewards.
     */
    public Scenario(String scenarioID, String title, String description,
                    String category, String difficultyLevel, boolean mandatory) {
        this.scenarioID = scenarioID;
        this.title = title;
        this.description = description;
        this.category = category;
        this.difficultyLevel = difficultyLevel;
        this.mandatory = mandatory;
        this.completionStatus = "Available";
        this.options = new ArrayList<>();
        this.outcomes = new ArrayList<>();
        this.rewards = new ArrayList<>();
    }

    /**
     * Adds one response option to the scenario.
     * This method also adds the matching outcome text and reward value at the
     * same index so the program can later evaluate the user's choice correctly.
     */
    public void addOption(String option, String outcome, double reward) {
        options.add(option);
        outcomes.add(outcome);
        rewards.add(reward);
    }

    // Returns the scenario's unique ID.
    public String getScenarioID() {
        return scenarioID;
    }

    // Returns the scenario title
    public String getTitle() {
        return title;
    }

    // Returns the full scenario description
    public String getDescription() {
        return description;
    }

    // Returns the category this scenario belongs to
    public String getCategory() {
        return category;
    }

    // Returns the difficulty level of the scenario
    public String getDifficultyLevel() {
        return difficultyLevel;
    }

    // Returns the list of response options shown to the user
    public ArrayList<String> getOptions() {
        return options;
    }

    // Returns the list of outcome messages tied to the scenario options
    public ArrayList<String> getOutcomes() {
        return outcomes;
    }

    // Returns the list of reward values tied to the scenario options
    public ArrayList<Double> getRewards() {
        return rewards;
    }

    // Returns whether this scenario is marked as mandatory
    public boolean isMandatory() {
        return mandatory;
    }

    //  Updates whether the scenario is mandatory
    public void setMandatory(boolean mandatory) {
        this.mandatory = mandatory;
    }

    // Returns the current completion state of the scenario
    public String getCompletionStatus() {
        return completionStatus;
    }

    /**
     * Updates the completion state of the scenario.
     * This is used by the scenario flow to move a scenario between Available,
     * In Progress, and Completed as the user interacts with it.
     */
    public void setCompletionStatus(String status) {
        this.completionStatus = status;
    }
}
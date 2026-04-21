/**
 * ScenarioManager is the main class that stores and organizes all scenarios
 * used by the program.
 *
 * This class does a few important jobs:
 * - creates the full built-in scenario list when the program starts
 * - returns subsets of scenarios based on things like difficulty or category
 * - tracks which scenarios have been completed
 * - handles reset logic for replaying scenarios
 * - helps the rest of the program find the next mandatory scenario
 *
 * Most of the large size of this file comes from loadScenarios(), since that
 * method hardcodes all scenario data directly into the program instead of
 * loading it from a file or database.
 */

package org.example.pathwayver1;

import java.util.ArrayList;

public class ScenarioManager {

    private ArrayList<Scenario> scenarios; // Stores every scenario available in the program
    private ArrayList<String> completedScenarios; // Stores the IDs of scenarios the user has completed

    // Creates a ScenarioManager and loads all built-in scenarios into memory
    public ScenarioManager() {
        this.scenarios = new ArrayList<>();
        this.completedScenarios = new ArrayList<>();
        loadScenarios();
    }

    /**
     * Builds the full scenario list used by the application.
     *
     * Each scenario is created with:
     * - an ID
     * - a title
     * - a description
     * - a category
     * - a difficulty level
     * - a mandatory flag
     *
     * After that, each possible response option is added along with the outcome
     * text and reward value tied to that response.
     */
    private void loadScenarios() {
        // S001 - Beginner - Everyday Spending - Piggy Bank reward
        Scenario s1 = new Scenario("S001", "Birthday Money Decision",
                "You just received $20 for your birthday! What would you like to do with it?",
                "Everyday Spending", "Beginner", false);
        s1.addOption("Save all of it in your savings account",
                "Great choice! Saving your money helps it grow over time. You earned a Piggy Bank!",
                20.0);
        s1.addOption("Spend it all on a new toy",
                "The toy is fun, but now you have no money left. Next time, consider saving at least some of it.",
                -20.0);
        s1.addOption("Save half and spend half",
                "Good balance! You got to enjoy some of your money while still saving for the future.",
                10.0);
        s1.addOption("Give it all to a friend",
                "That's very generous! But make sure you're also taking care of your own financial needs first.",
                0.0);
        scenarios.add(s1);

        // S002 - Advanced - Emergency Expense - Mandatory
        Scenario s2 = new Scenario("S002", "Unexpected Bike Repair",
                "You rely on your bike to get to school every day. After riding home today, you noticed the chain broke and the front tire is flat. A local repair shop quoted you $40 for the full fix, but your friend says they can try to fix just the chain for free, though the tire would still need replacing later for $25.",
                "Emergency Expense", "Advanced", true);
        s2.addOption("Pay the full $40 at the repair shop and get everything fixed now",
                "Smart decision! Fixing everything at once means your bike is fully working and you avoid future costs.",
                -40.0);
        s2.addOption("Ignore the repairs for now and walk to school until you save more money",
                "Walking works for now, but your bike will keep getting worse. Delaying repairs often makes them more expensive later.",
                0.0);
        s2.addOption("Let your friend fix the chain for free and save the tire repair for later",
                "You saved money now, but you'll still need to pay $25 later for the tire.",
                -25.0);
        s2.addOption("Withdraw $25 from your Debit Account and buy a used tire to replace it yourself",
                "Creative thinking! But make sure you know how to do the repair properly.",
                -25.0);
        scenarios.add(s2);

        // S003 - Beginner - Income Opportunity - Savings Journal reward
        Scenario s3 = new Scenario("S003", "Part-Time Chore Paycheck",
                "Your neighbor offered to pay you $15 every week for helping with yard work. What do you do with your first paycheck?",
                "Income Opportunity", "Beginner", false);
        s3.addOption("Deposit all $15 into your savings account",
                "Excellent! Building a savings habit early is one of the best financial decisions. You earned a Savings Journal!",
                15.0);
        s3.addOption("Spend $10 and save $5",
                "Not bad! You enjoyed some of your earnings while still putting something away.",
                5.0);
        s3.addOption("Spend all $15 on snacks and games",
                "It felt good in the moment, but now you have nothing to show for your hard work.",
                -15.0);
        s3.addOption("Save $10 and spend $5",
                "Great job! Saving more than you spend is a strong financial habit.",
                10.0);
        scenarios.add(s3);

        // S004 - Advanced - Bills and Expenses - Gaming Sub reward
        Scenario s4 = new Scenario("S004", "Monthly Gaming Subscription",
                "You've been paying $10/month for a gaming subscription. You haven't played in three weeks and your savings are getting low. What do you do?",
                "Bills and Expenses", "Advanced", false);
        s4.addOption("Cancel the subscription and save the money",
                "Smart move! Cutting unused subscriptions is one of the easiest ways to save money.",
                10.0);
        s4.addOption("Keep it because you might play again soon",
                "Paying for something you're not using wastes money. Cancel and resubscribe when you actually want it.",
                -10.0);
        s4.addOption("Downgrade to a cheaper plan",
                "Good compromise! You're reducing costs while keeping access. You now have a Gaming Subscription.",
                5.0);
        scenarios.add(s4);

        // S005 - Advanced - Emergency Expense - Mandatory
        Scenario s5 = new Scenario("S005", "Phone Screen Cracked",
                "Your phone screen cracked and it costs $80 to repair. You have $120 in your debit account and $30 in savings. What do you do?",
                "Emergency Expense", "Advanced", true);
        s5.addOption("Pay $80 from your debit account to fix it right away",
                "Getting it fixed quickly prevents further damage. Just be careful with your remaining balance.",
                -80.0);
        s5.addOption("Use a screen protector to cover the crack and save the money",
                "This is a temporary fix. The crack could get worse and cost more to repair later.",
                0.0);
        s5.addOption("Take $50 from debit and $30 from savings to cover the cost",
                "You got it fixed, but pulling from savings should be a last resort.",
                -80.0);
        s5.addOption("Wait until you earn more money before fixing it",
                "Waiting is risky — the damage could get worse. But if you're careful, this could work short-term.",
                0.0);
        scenarios.add(s5);

        // S006 - Beginner - Everyday Spending
        Scenario s6 = new Scenario("S006", "Lunch Money Choices",
                "You have $10 for lunch this week. It's Monday and your friends want to go to an expensive restaurant that costs $8. What do you do?",
                "Everyday Spending", "Beginner", false);
        s6.addOption("Go to the restaurant and spend $8",
                "It was fun, but now you only have $2 for the rest of the week.",
                -8.0);
        s6.addOption("Suggest a cheaper place and spend $4",
                "Great thinking! You still had fun with your friends and saved money.",
                -4.0);
        s6.addOption("Bring lunch from home and save all $10",
                "Maximum savings! Packing lunch is one of the best ways to keep money in your pocket.",
                0.0);
        s6.addOption("Split a meal with a friend for $4 each",
                "Clever compromise! You saved money while still enjoying the restaurant.",
                -4.0);
        scenarios.add(s6);

        // S007 - Beginner - Emergency Expense - Mandatory
        Scenario s7 = new Scenario("S007", "Lost Lunch Money",
                "Oh no! You lost your lunch money at school and you're really hungry. You have $8 in your wallet. The cafeteria lunch costs $5, a vending machine snack costs $2, or you could ask a friend to share.",
                "Emergency Expense", "Beginner", true);
        s7.addOption("Buy the full cafeteria lunch for $5",
                "Good choice! A full meal keeps you energized for the rest of the day.",
                -5.0);
        s7.addOption("Get a vending machine snack for $2",
                "You saved money, but a small snack won't keep you full.",
                -2.0);
        s7.addOption("Ask a friend to share their lunch",
                "That works today, but you can't always rely on others. Having emergency money is important.",
                0.0);
        scenarios.add(s7);

        // S008 - Advanced - Emergency Expense - Mandatory
        Scenario s8 = new Scenario("S008", "Flat Tire Emergency",
                "You're riding to an important event and your tire goes flat. A nearby shop can fix it for $15 right now, or you can walk and fix it tomorrow for $10. You have $25 in your debit account.",
                "Emergency Expense", "Advanced", true);
        s8.addOption("Pay $15 to fix it now and ride to the event",
                "Time is money! You made it to your event and your bike is fixed.",
                -15.0);
        s8.addOption("Walk to the event and fix it tomorrow for $10",
                "You saved $5 but arrived late and tired. Sometimes convenience is worth the extra cost.",
                -10.0);
        s8.addOption("Skip the event entirely and fix the tire tomorrow",
                "You saved $5 but missed the event completely. Missing opportunities to save small amounts isn't always best.",
                -10.0);
        scenarios.add(s8);

        // S009 - Beginner - Emergency Expense - Mandatory - Repair Kit reward
        Scenario s9 = new Scenario("S009", "Broken School Supplies",
                "Your backpack strap broke and your pencil case fell in a puddle! You need supplies for class tomorrow. A new backpack costs $20, a pencil set costs $5, or you could try to fix what you have.",
                "Emergency Expense", "Beginner", true);
        s9.addOption("Buy a new backpack for $20",
                "Brand new and reliable! But that's a big expense. Could you have fixed the strap instead?",
                -20.0);
        s9.addOption("Fix the strap and just buy new pencils for $5",
                "Smart thinking! Repairing instead of replacing saves money. You earned a Repair Kit!",
                -5.0);
        s9.addOption("Try to fix everything yourself for free",
                "Great effort to save money! Just make sure the fixes hold up.",
                0.0);
        s9.addOption("Borrow supplies from a friend for now",
                "Works temporarily, but don't put off necessary expenses too long.",
                0.0);
        scenarios.add(s9);

        // S010 - Advanced - Income Opportunity - Seller Badge reward
        Scenario s10 = new Scenario("S010", "Selling Old Items",
                "You're cleaning your room and found old toys and games you don't use anymore. A friend offers $30 for everything, an online listing might get you $50 but takes time, or you could donate them.",
                "Income Opportunity", "Advanced", false);
        s10.addOption("Sell everything to your friend for $30",
                "Quick and easy money! Sometimes a guaranteed sale is better than waiting for more.",
                30.0);
        s10.addOption("List them online and wait for $50",
                "Patience can pay off! You got more money but had to wait. You earned an Online Seller Badge!",
                50.0);
        s10.addOption("Donate everything to charity",
                "Very generous! You didn't earn money, but you helped others.",
                0.0);
        scenarios.add(s10);

        // S011 - Beginner - Bills and Expenses - Streaming Sub reward
        Scenario s11 = new Scenario("S011", "Streaming Service Decision",
                "Your family asked if you want your own streaming subscription. It costs $8/month. You get $20 allowance per month. What do you do?",
                "Bills and Expenses", "Beginner", false);
        s11.addOption("Subscribe and enjoy unlimited movies",
                "Entertainment is nice, but $8 is 40% of your allowance going to one thing. You now have a Streaming Subscription.",
                -8.0);
        s11.addOption("Share your family's account instead",
                "Smart move! Why pay for something you can get for free?",
                0.0);
        s11.addOption("Subscribe but set a reminder to cancel if you don't use it",
                "Good planning! Being aware of your spending habits is important. You now have a Streaming Subscription.",
                -8.0);
        scenarios.add(s11);

        // S012 - Advanced - Bills and Expenses - Electric Utility reward
        Scenario s12 = new Scenario("S012", "Electric Bill Due",
                "Your electric bill came in at $12 this cycle. You've been leaving lights on and charging devices all day. You have $40 in your debit account. How do you handle this?",
                "Bills and Expenses", "Advanced", false);
        s12.addOption("Pay the full $12 right away and start saving electricity",
                "Responsible choice! Paying on time avoids late fees. Plus, saving electricity reduces future bills. You now manage an Electric Utility.",
                -12.0);
        s12.addOption("Ignore the bill and hope it goes away",
                "Bills don't disappear! Ignoring them leads to late fees and bigger problems. You now owe this Electric Utility.",
                -12.0);
        s12.addOption("Pay half now and half next cycle",
                "Splitting payments can work, but make sure you actually pay the rest. You now manage an Electric Utility.",
                -6.0);
        scenarios.add(s12);

        // S013 - Beginner - Bills and Expenses - Water Utility reward
        Scenario s13 = new Scenario("S013", "Water Bill Surprise",
                "Your water bill is $8 this cycle. Your mom says you've been taking really long showers. You have $30 in your wallet. What do you do?",
                "Bills and Expenses", "Beginner", false);
        s13.addOption("Pay the $8 and start taking shorter showers",
                "Great thinking! Pay what you owe and reduce future costs by changing habits. You now manage a Water Utility.",
                -8.0);
        s13.addOption("Complain about it and refuse to pay",
                "The bill is still due whether you like it or not. Refusing to pay makes things worse. You now owe this Water Utility.",
                -8.0);
        s13.addOption("Pay the $8 but don't change your habits",
                "You paid on time, but your next bill will be just as high. Changing habits saves money long-term. You now manage a Water Utility.",
                -8.0);
        scenarios.add(s13);

        // S014 - Advanced - Emergency Expense - Mandatory - Contact transfer to Alex
        Scenario s14 = new Scenario("S014", "Friend Wants Money Back",
                "Alex lent you $20 last week for a school project. Now they need it back urgently for their own emergency. You have $35 in your debit account.",
                "Emergency Expense", "Advanced", true);
        s14.addOption("Transfer $20 to Alex right away",
                "Honoring your debts is important! Alex trusted you and you came through. Transfer $20 to Alex.",
                -20.0);
        s14.addOption("Tell Alex you'll pay next week",
                "Delaying repayment damages trust. If someone lent you money, pay it back when they ask.",
                0.0);
        s14.addOption("Offer to pay $10 now and $10 later",
                "Partial payment is better than nothing, but you should try to pay the full amount when asked. Transfer $10 to Alex.",
                -10.0);
        scenarios.add(s14);

        // S015 - Beginner - Emergency Expense - Mandatory - Contact transfer to Sister
        Scenario s15 = new Scenario("S015", "Sibling Needs Help",
                "Your sister needs $10 to buy supplies for a school project due tomorrow. She promises to pay you back from her allowance. You have $25 in your wallet.",
                "Emergency Expense", "Beginner", true);
        s15.addOption("Give her the $10 right away",
                "Family helps family! You supported your sister when she needed it. Transfer $10 to Sister.",
                -10.0);
        s15.addOption("Say no because it's your money",
                "It's your right, but helping family builds trust. She might not help you when you need it later.",
                0.0);
        s15.addOption("Lend her $5 and suggest she find the rest elsewhere",
                "A compromise! You helped some but kept most of your money. Transfer $5 to Sister.",
                -5.0);
        scenarios.add(s15);

        // S016 - Beginner - Income Opportunity
        Scenario s16 = new Scenario("S016", "Lemonade Stand Profit",
                "You set up a lemonade stand and made $12 today! Your supplies cost $4. What do you do with your $8 profit?",
                "Income Opportunity", "Beginner", false);
        s16.addOption("Save all $8 profit",
                "Great discipline! Saving your profits helps you grow your money over time.",
                8.0);
        s16.addOption("Reinvest $4 in better supplies for tomorrow",
                "Smart business thinking! Investing in your business could mean bigger profits later.",
                4.0);
        s16.addOption("Spend all $8 on treats",
                "You worked hard but now have nothing saved. Try to save at least some of your earnings.",
                -8.0);
        s16.addOption("Save $6 and spend $2 on a treat",
                "Nice balance! You rewarded yourself while still saving most of your profit.",
                6.0);
        scenarios.add(s16);

        // S017 - Advanced - Income Opportunity - Headphones reward
        Scenario s17 = new Scenario("S017", "Garage Sale Weekend",
                "Your family is having a garage sale. You find old headphones worth about $25. A buyer offers $15 cash, another says they'll pay $25 but needs to come back tomorrow. What do you do?",
                "Income Opportunity", "Advanced", false);
        s17.addOption("Sell for $15 cash now",
                "Guaranteed money in hand! Sometimes the sure thing is the smart choice.",
                15.0);
        s17.addOption("Wait for the $25 buyer tomorrow",
                "Patience paid off! You got the full value. You earned Headphones to sell later!",
                25.0);
        s17.addOption("Keep the headphones for yourself",
                "They're nice headphones, but you missed a chance to earn money from something you weren't using. You kept the Headphones.",
                0.0);
        scenarios.add(s17);

        // S018 - Advanced - Everyday Spending
        Scenario s18 = new Scenario("S018", "Impulse Buy at the Mall",
                "You're at the mall and see a cool jacket on sale for $35, marked down from $50. You have $60 in your debit account but need $30 for groceries this week. What do you do?",
                "Everyday Spending", "Advanced", false);
        s18.addOption("Buy the jacket — it's a great deal!",
                "The sale was tempting, but now you only have $25 left and need $30 for groceries. Impulse buying can hurt your budget.",
                -35.0);
        s18.addOption("Walk away and stick to your grocery budget",
                "Strong willpower! You prioritized needs over wants. That $35 stays in your account for things that matter.",
                0.0);
        s18.addOption("Buy the jacket and figure out groceries later",
                "Living beyond your means creates problems. Always cover necessities before luxuries.",
                -35.0);
        s18.addOption("Take a photo and come back next payday if it's still there",
                "Smart compromise! If you still want it later and can afford it, go for it. No rush on sales.",
                0.0);
        scenarios.add(s18);

        // S019 - Beginner - Everyday Spending
        Scenario s19 = new Scenario("S019", "School Fundraiser",
                "Your school is having a fundraiser selling candy bars for $2 each. You have $10. Your friends are all buying some. What do you do?",
                "Everyday Spending", "Beginner", false);
        s19.addOption("Buy 5 candy bars for $10",
                "You spent everything on candy! Fun in the moment, but now your wallet is empty.",
                -10.0);
        s19.addOption("Buy 1 candy bar for $2 and save the rest",
                "Great balance! You supported the fundraiser and kept most of your money.",
                -2.0);
        s19.addOption("Don't buy any and save all $10",
                "Maximum savings! Though sometimes it's okay to spend a little for a good cause.",
                0.0);
        s19.addOption("Buy 2 candy bars — one for you and one for a friend",
                "Generous and reasonable! You spent $4 but still have $6 saved.",
                -4.0);
        scenarios.add(s19);

        // S020 - Advanced - Bills and Expenses - Phone Plan Sub reward
        Scenario s20 = new Scenario("S020", "Phone Plan Decision",
                "You're old enough to manage your own phone plan. A basic plan costs $10/month, a premium plan costs $20/month with unlimited data. You use your phone mostly for texting. What do you choose?",
                "Bills and Expenses", "Advanced", false);
        s20.addOption("Get the basic $10 plan",
                "Smart choice! You matched your plan to your actual usage. No point paying for features you don't need. You now have a Phone Plan.",
                -10.0);
        s20.addOption("Get the premium $20 plan",
                "Unlimited data is nice, but you're paying double for features you barely use. You now have a Phone Plan.",
                -20.0);
        s20.addOption("Stay on your family's plan for free",
                "Best deal possible! Free is always the best price. Use that money for savings instead.",
                0.0);
        scenarios.add(s20);

        // S021 - Beginner - Emergency Expense - Mandatory - Contact transfer to Mom
        Scenario s21 = new Scenario("S021", "Mom Needs Grocery Help",
                "Your mom is short on cash for groceries this week and asks if you can help with $15. You have $40 in your wallet. She always takes care of you.",
                "Emergency Expense", "Beginner", true);
        s21.addOption("Give her the full $15",
                "Family first! Mom always supports you. Helping her out is the right thing. Transfer $15 to Mom.",
                -15.0);
        s21.addOption("Give her $10 and keep $5 for yourself",
                "You helped, but could you have given more? She'd do anything for you. Transfer $10 to Mom.",
                -10.0);
        s21.addOption("Say you don't have any money",
                "Mom knows you have money. Being dishonest about finances damages trust.",
                0.0);
        scenarios.add(s21);

        // S022 - Advanced - Emergency Expense - Mandatory - Contact transfer to Jordan
        Scenario s22 = new Scenario("S022", "Jordan's Emergency",
                "Your friend Jordan got stranded and needs $12 for a ride home. They promise to pay you back next week. You have $30 in your debit account.",
                "Emergency Expense", "Advanced", true);
        s22.addOption("Send Jordan $12 right away",
                "Good friend! Safety comes first and you helped someone in need. Transfer $12 to Jordan.",
                -12.0);
        s22.addOption("Send $6 and tell them to ask someone else for the rest",
                "Partial help is better than none, but your friend is still stuck. Transfer $6 to Jordan.",
                -6.0);
        s22.addOption("Tell them to figure it out themselves",
                "Everyone needs help sometimes. Being there for friends builds strong relationships.",
                0.0);
        scenarios.add(s22);

        // S023 - Beginner - Emergency Expense - Mandatory - Contact transfer to Dad
        Scenario s23 = new Scenario("S023", "Dad's Parking Meter",
                "Dad forgot his wallet and his parking meter is about to expire. He needs $5 quick or he'll get a $50 ticket. You have $18 in your wallet.",
                "Emergency Expense", "Beginner", true);
        s23.addOption("Send Dad $5 immediately",
                "Quick thinking! $5 now saved Dad $50 in tickets. Smart financial decision! Transfer $5 to Dad.",
                -5.0);
        s23.addOption("Tell Dad to move the car instead",
                "That could work, but what if he can't? Sometimes spending a little prevents a much bigger cost.",
                0.0);
        scenarios.add(s23);
    }

    /**
     * Returns all scenarios that are currently marked Available and match the
     * requested difficulty.
     * If difficulty is "All", then difficulty is ignored and every available
     * scenario is returned.
     */
    public ArrayList<Scenario> getAvailableScenarios(String difficulty) {
        ArrayList<Scenario> available = new ArrayList<>();
        for (Scenario s : scenarios) {
            if (s.getCompletionStatus().equals("Available")) {
                if (difficulty.equals("All") || s.getDifficultyLevel().equalsIgnoreCase(difficulty)) {
                    available.add(s);
                }
            }
        }
        return available;
    }

    //  Returns the list of completed scenario IDs.
    public ArrayList<String> getCompletedScenarios() {
        return completedScenarios;
    }

    /**
     * Finds a scenario by its ID.
     * Returns null if no matching scenario exists.
     */
    public Scenario getScenarioByID(String id) {
        for (Scenario s : scenarios) {
            if (s.getScenarioID().equals(id)) {
                return s;
            }
        }
        return null;
    }

    /**
     * Returns the outcome text tied to one selected response
     * choiceIndex must match one of the scenario's stored options
     */
    public String evaluateResponse(Scenario scenario, int choiceIndex) {
        return scenario.getOutcomes().get(choiceIndex);
    }

    // Returns the reward or penalty tied to one selected response
    public double getRewardAmount(Scenario scenario, int choiceIndex) {
        return scenario.getRewards().get(choiceIndex);
    }

    /**
     * Marks a scenario as completed and records its ID in completedScenarios.
     * The extra completedScenarios list is useful because some parts of the
     * program want a quick record of completion without scanning the full list.
     */
    public void markCompleted(Scenario scenario) {
        scenario.setCompletionStatus("Completed");
        if (!completedScenarios.contains(scenario.getScenarioID())) {
            completedScenarios.add(scenario.getScenarioID());
        }
    }

    /**
     * Returns the next mandatory scenario that is still available.
     * If all mandatory scenarios are already completed, this method resets all
     * mandatory scenarios back to Available and then returns the first one again.
     * This method does not consider user difficulty. That logic is handled in
     * getMandatoryPendingForDifficulty().
     */
    public Scenario getMandatoryPending() {
        for (Scenario s : scenarios) {
            if (s.isMandatory() && s.getCompletionStatus().equals("Available")) {
                return s;
            }
        }

        boolean allCompleted = true;
        for (Scenario s : scenarios) {
            if (s.isMandatory() && !s.getCompletionStatus().equals("Completed")) {
                allCompleted = false;
                break;
            }
        }

        if (allCompleted) {
            for (Scenario s : scenarios) {
                if (s.isMandatory()) {
                    s.setCompletionStatus("Available");
                }
            }

            for (Scenario s : scenarios) {
                if (s.isMandatory()) {
                    return s;
                }
            }
        }

        return null;
    }

    /**
     * Returns the next mandatory scenario that is still available and also
     * matches the user's difficulty level.
     *
     * This is the method used when the program triggers mandatory interruptions
     * during normal use. Child mode users skip Advanced mandatory scenarios.
     * Just like getMandatoryPending(), this method resets the mandatory set if
     * all eligible mandatory scenarios for that user have already been completed.
     */
    public Scenario getMandatoryPendingForDifficulty(String difficultyLevel) {
        for (Scenario s : scenarios) {
            if (s.isMandatory() && s.getCompletionStatus().equals("Available")) {
                if (difficultyLevel.equals("Child-mode") && s.getDifficultyLevel().equalsIgnoreCase("Advanced")) {
                    continue;
                }
                return s;
            }
        }

        boolean allCompleted = true;
        for (Scenario s : scenarios) {
            if (s.isMandatory()) {
                if (difficultyLevel.equals("Child-mode") && s.getDifficultyLevel().equalsIgnoreCase("Advanced")) {
                    continue;
                }
                if (!s.getCompletionStatus().equals("Completed")) {
                    allCompleted = false;
                    break;
                }
            }
        }

        if (allCompleted) {
            for (Scenario s : scenarios) {
                if (s.isMandatory()) {
                    if (difficultyLevel.equals("Child-mode") && s.getDifficultyLevel().equalsIgnoreCase("Advanced")) {
                        continue;
                    }
                    s.setCompletionStatus("Available");
                }
            }
            for (Scenario s : scenarios) {
                if (s.isMandatory() && s.getCompletionStatus().equals("Available")) {
                    return s;
                }
            }
        }

        return null;
    }

    /**
     * Resets one scenario back to Available and removes its ID from the
     * completedScenarios list.
     * This is used when the user chooses to replay completed scenarios.
     */
    public void resetScenario(Scenario scenario) {
        scenario.setCompletionStatus("Available");
        completedScenarios.remove(scenario.getScenarioID());
    }

    // Returns only the scenarios that match a specific difficulty
    public ArrayList<Scenario> filterByDifficulty(String difficulty) {
        ArrayList<Scenario> filtered = new ArrayList<>();
        for (Scenario s : scenarios) {
            if (s.getDifficultyLevel().equalsIgnoreCase(difficulty)) {
                filtered.add(s);
            }
        }
        return filtered;
    }

    // Returns only the scenarios that match a specific category
    public ArrayList<Scenario> filterByCategory(String category) {
        ArrayList<Scenario> filtered = new ArrayList<>();
        for (Scenario s : scenarios) {
            if (s.getCategory().equalsIgnoreCase(category)) {
                filtered.add(s);
            }
        }
        return filtered;
    }

    // Returns the full scenario list
    public ArrayList<Scenario> getAllScenarios() {
        return scenarios;
    }
}
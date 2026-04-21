/**
 * UserAccount is the main model for a single Pathway Banking user.
 *
 * It stores the user's profile information, login credentials, date of birth,
 * banking accounts, wallet access, notifications, scenarios, assets, help data,
 * and a few system flags used across the application.
 *
 * This class also acts as a validation hub for registration and settings edits.
 * Most setter methods enforce the formatting rules expected by the UI, so the
 * controllers can pass user input here and rely on clear exceptions when
 * something is invalid.
 */

package org.example.pathwayver1;

import java.util.ArrayList;

public class UserAccount
{
    // [Core profile information]

    // UserAccount Attributes/Fields //

    //=====Name fields====
    private String firstName;
    private String lastName;

    // Each user can own multiple bank accounts (Debit by default, then others later)
    private ArrayList<Account> accounts = new ArrayList<>();
    //======================

    //===Date of Birth fields===
    private int bMonth;
    private int bDay;
    private int bYear;
    //===========================

    //===Electric Communication fields=====
    private String email;
    private String phoneNumber; // only for digits
    private String phoneNumberRaw; // only for presentation (exactly as typed)
    //============================

    //===Address fields===
    private String street;
    private String city;
    private String state;
    private String zipCode;
    private String country;
    //=====================

    //===User Credential fields====
    private String userID;
    private String password;
    //==============================


    // CONSTRUCTORS

    // UserAccount Default Constructor //
    public UserAccount()
    {

    }

    // UserAccount Overloaded Constructor. Full constructor used when rebuilding a saved user from persisted data. //
    public UserAccount(String fN, String lM, String e,
                       String pN, String stre, String ci,
                       String sta, String zi, String co,
                       int bM, int bD, int bY,
                       String uID, String pass)
    {
        //=====Name fields====
        setFirstName(fN);
        setLastName(lM);

        //===Date of Birth fields===
        setDOB(bM, bD, bY);

        //===Electric Communication fields=====
        setEmail(e);
        setPhoneNumber(pN);

        //===Address fields===
        setStreet(stre);
        setCity(ci);
        setState(sta);
        setZipCode(zi);
        setCountry(co);

        //===User Credential fields====
        setUserID(uID);
        setPassword(pass);
    }

    // Account methods helpers //

    // Returns the list of banking accounts owned by this user.
    public ArrayList<Account> getAccounts(){
        return accounts;
    }
    // Adds a new banking account to the user's account list.
    public void addAccount(Account account){
        accounts.add(account);
    }

    // UserAccount Get/Set methods //

    //               NAME FIELDS

    // =======first name get/set=====
    public String getFirstName()
    {
        return firstName;
    }
    /**
     * Validates and stores the user's first name.
     *
     * Allowed characters:
     * letters, spaces, hyphens, and apostrophes.
     */
    public void setFirstName(String fname)
    {
        if (fname == null || fname.trim().isEmpty())
        {
            throw new IllegalArgumentException("First name field cannot be empty.");
        }

        fname = fname.trim();

        for(int i = 0; i < fname.length(); i++)
        {
            char c = fname.charAt(i);

            if(!Character.isLetter(c) && c != '-' && c != '\'' && c != ' ')
            {
                throw new IllegalArgumentException("First name may only contain letters, spaces, hyphens, or apostrophes.");
            }
        }


        this.firstName = fname.trim();
    }
    // =====================================


    // =========last name get/set===========
    public String getLastName()
    {
        return lastName;
    }
    /**
     * Validates and stores the user's last name.
     *
     * Allowed characters:
     * letters, spaces, hyphens, and apostrophes.
     */
    public void setLastName(String lname)
    {
        if (lname == null || lname.trim().isEmpty())
        {
            throw new IllegalArgumentException("Last name field cannot be empty.");
        }

        lname = lname.trim();

        for(int i = 0; i < lname.length(); i++)
        {
            char c = lname.charAt(i);

            if(!Character.isLetter(c) && c != '-' && c != '\'' && c != ' ')
            {
                throw new IllegalArgumentException("Last name may only contain letters, spaces, hyphens, or apostrophes.");
            }
        }

        this.lastName = lname.trim();
    }
    //================================

    //DOB FIELDS

    //========DOB get/set===========
    public int getBMonth()
    {
        return bMonth;
    }

    public int getBDay()
    {
        return bDay;
    }

    public int getBYear()
    {
        return bYear;
    }

    // DOB parsing helpers.
    // Parses and validates the string entry provided by user for month, day, and year

    public static int parseBirthMonth(String m)
    {
        if(m == null || m.trim().isEmpty())
            throw new IllegalArgumentException("Birth month field cannot be empty.");

        if(!m.matches("-?\\d+"))
            throw new IllegalArgumentException("Month must contain digits only.");

        int month = Integer.parseInt(m);

        if(month < 1 || month > 12)
            throw new IllegalArgumentException("Month must be between 1 and 12.");

        return month;
    }

    public static int parseBirthDay(String d)
    {
        if(d == null || d.trim().isEmpty())
            throw new IllegalArgumentException("Birth day field cannot be empty.");

        if(!d.matches("-?\\d+"))
            throw new IllegalArgumentException("Day must contain digits only.");

        int day = Integer.parseInt(d);

        if(day < 1 || day > 31)
            throw new IllegalArgumentException("Day must be between 1 and 31.");

        return day;
    }

    public static int parseBirthYear(String y)
    {
        if(y == null || y.trim().isEmpty())
            throw new IllegalArgumentException("Birth year field cannot be empty.");

        if(!y.matches("\\d{4}"))
            throw new IllegalArgumentException("Year must be four digits.");

        int year = Integer.parseInt(y);

        int currentYear = java.time.LocalDate.now().getYear();

        if(year < 1900 || year > currentYear)
            throw new IllegalArgumentException("Invalid birth year.");

        return year;
    }

    /**
     * Validates and stores the full date of birth.
     *
     * This also enforces the minimum supported age for the application.
     */
    public void setDOB(int mo, int da, int ye)
    {
        try
        {
            // LocalDate handles real calendar validation for us
            java.time.LocalDate dob = java.time.LocalDate.of(ye, mo, da);

            int age = java.time.Period.between(
                    dob,
                    java.time.LocalDate.now()
            ).getYears();

            if(age < 5)
            {
                throw new IllegalArgumentException("User must be at least 5 years old.");
            }

            this.bMonth = mo;
            this.bDay = da;
            this.bYear = ye;
        }
        catch(java.time.DateTimeException e)
        {
            throw new IllegalArgumentException("Invalid birth date.");
        }
    }

    /**
     * Calculates the user's current age from their stored birth date.
     *
     * This is mainly used to determine difficulty access in the app.
     */
    public int getAge()
    {
        java.time.LocalDate dob = java.time.LocalDate.of(bYear, bMonth, bDay);

        return java.time.Period.between(dob, java.time.LocalDate.now()).getYears();
    }
    //=======================================

    // ELECTRIC COMMUNICATION FIELDS

    //====== email get/set================
    public String getEmail()
    {
        return email;
    }
    /**
     * Validates and stores the user's email address.
     *
     * This is intentionally simple validation suited for the project scope.
     */
    public void setEmail(String emm)
    {
        if (emm == null || emm.trim().isEmpty())
        {
            throw new IllegalArgumentException("Email field cannot be empty.");
        }

        emm = emm.trim();

        // no spaces
        if(emm.contains(" "))
        {
            throw new IllegalArgumentException("Email cannot contain spaces.");
        }

        // email must contain @
        if(!emm.contains("@"))
        {
            throw new IllegalArgumentException("Valid email must contain '@' character.");
        }

        int atIndex = emm.indexOf("@");

        // prevents user from entering "@gmail.com" as their email.
        if(atIndex == 0)
        {
            throw new IllegalArgumentException("Email must contain characters before '@'.");
        }

        // ensures that email has some domain like "gmail" "yahoo" "outlook" after the @
        if(atIndex == emm.length() - 1)
        {
            throw new IllegalArgumentException("Email must contain a domain after '@'.");
        }

        // examines chars after the "@" part
        String domain = emm.substring(atIndex + 1);

        // prevents user from typing something like "user@."
        // cant start a "." after the "@"
        if(domain.startsWith("."))
        {
            throw new IllegalArgumentException("Invalid email format.");
        }

        // prevents user from typing something like "user@gmail"
        // "." must be included after the domain (or generally just after the first char, like "g")
        if(!domain.contains("."))
        {
            throw new IllegalArgumentException("Email domain must contain a '.'");
        }

        // prevents user from typing something like "user@gmail."
        // must end in a ".com" or ".org" or just have one char atleast after the "."
        if(domain.endsWith("."))
        {
            throw new IllegalArgumentException("Invalid email format.");
        }

        // ex: the program will accept something like: "u@g.c"
        this.email = emm;
    }
    //=============================================


    //====== phone get/set==============
    public String getPhoneNumber()
    {
        return phoneNumber; // digits only
    }
    /**
     * Returns the phone number as the user originally typed it.
     *
     * This is useful for redisplaying it in settings without losing formatting.
     */
    public String getPhoneNumberRaw()
    {
        return phoneNumberRaw; // exactly as typed when displayed in settings
    }
    /**
     * Validates and stores the user's phone number.
     *
     * The method keeps two versions:
     * one normalized version for comparisons,
     * and one raw version for UI display.
     */
    public void setPhoneNumber(String phnum)
    {
        if (phnum == null || phnum.trim().isEmpty())
        {
            throw new IllegalArgumentException("Phone number field cannot be empty.");
        }

        phnum = phnum.trim();

        String digitsOnly = "";

        for (int i = 0; i < phnum.length(); i++)
        {
            char c = phnum.charAt(i);

            // ensure that the phone number is a valid integer(s)
            // within the 7-15 digit span
            // allows for "()", " ", "+", "-" format without rejecting
            if (Character.isDigit(c))
            {
                digitsOnly += c;
            }
            else if (c == '+' || c == '-' || c == '(' || c == ')' || c == ' ')
            {
                // allowed formatting characters, ignore but allow
            }
            else
            {
                throw new IllegalArgumentException("Phone number may only contain digits, +, -, (), or spaces.");
            }
        }

        // digit length validation
        if (digitsOnly.length() < 7 || digitsOnly.length() > 15)
        {
            throw new IllegalArgumentException("Enter a valid phone number (7–15 digits).");
        }

        // store normalized digits
        this.phoneNumber = digitsOnly;
        // store raw digits (to display for later in settings)
        this.phoneNumberRaw = phnum;
    }
    /**
     * Directly restores the raw phone format, usually after loading saved data.
     */
    public void setPhoneNumberRaw(String raw) {
        this.phoneNumberRaw = raw;
    }

    // ================================================

    //ADDRESS FIELDS

    //===== street get/set====
    public String getStreet()
    {
        return street;
    }
    public void setStreet(String stre)
    {
        if (stre == null || stre.trim().isEmpty())
        {
            throw new IllegalArgumentException("Street field cannot be empty.");
        }

        this.street = stre.trim();
    }
    // ============================


    // ===== city get/set============
    public String getCity()
    {
        return city;
    }
    public void setCity(String ci)
    {
        if (ci == null || ci.trim().isEmpty())
        {
            throw new IllegalArgumentException("City field cannot be empty.");
        }

        this.city = ci.trim();
    }
    // =================================


    // ====== state get/set============
    public String getState()
    {
        return state;
    }
    public void setState(String sta)
    {
        if (sta == null || sta.trim().isEmpty())
        {
            throw new IllegalArgumentException("State field cannot be empty.");
        }

        this.state = sta.trim();
    }
    // =================================


    // ====zip code get/set==========
    public String getZipCode()
    {
        return zipCode;
    }
    /**
     * Validates and stores the zip code.
     * keeps zip validation simple - digits only.
     */
    public void setZipCode(String zi)
    {
        if (zi == null || zi.trim().isEmpty())
        {
            throw new IllegalArgumentException("Zip code field cannot be empty.");
        }

        // zip code only allows for digits
        for (int i = 0; i < zi.length(); i++)
        {
            if (!Character.isDigit(zi.charAt(i)))
            {
                throw new IllegalArgumentException("Zip code must contain digits only.");
            }
        }

        this.zipCode = zi.trim();
    }
    //===============


    //======country get/set=======
    public String getCountry()
    {
        return country;
    }
    public void setCountry(String co)
    {
        if (co == null || co.trim().isEmpty())
        {
            throw new IllegalArgumentException("Country field cannot be empty.");
        }

        this.country = co.trim();
    }
    // ================================

    // USER CREDENTIAL FIELDS

    // ====== userID get/set========
    public String getUserID()
    {
        return userID;
    }
    /**
     * Validates and stores the user ID.
     *
     * Rules:
     * - 6 to 15 characters
     * - starts with a letter
     * - letters, digits, underscore only
     * - no spaces
     * - no leading or trailing underscore
     */
    public void setUserID(String id)
    {
        if (id == null || id.trim().isEmpty())
        {
            throw new IllegalArgumentException("User ID field cannot be empty.");
        }

        id = id.trim().toLowerCase();

        // valid length
        if (id.length() < 6 || id.length() > 15)
        {
            throw new IllegalArgumentException("User ID must be 6-15 characters.");
        }

        // no spaces
        if (id.contains(" "))
        {
            throw new IllegalArgumentException("User ID cannot contain spaces.");
        }

        // must start with letter
        if (!Character.isLetter(id.charAt(0)))
        {
            throw new IllegalArgumentException("User ID must start with a letter.");
        }

        // no leading or trailing underscore
        if (id.startsWith("_") || id.endsWith("_"))
        {
            throw new IllegalArgumentException("User ID cannot start or end with underscore.");
        }

        // only letters, numbers, underscore
        for (int i = 0; i < id.length(); i++)
        {
            char c = id.charAt(i);

            if (!Character.isLetterOrDigit(c) && c != '_')
            {
                throw new IllegalArgumentException("User ID may contain only letters, numbers, or underscore.");
            }
        }

        this.userID = id;
    }
    // ===================================


    // ====== password get/set ===============
    public String getPassword()
    {
        return password;
    }
    /**
     * Validates and stores the password.
     *
     * Rules:
     * - 8 to 30 characters
     * - no spaces
     * - cannot match the user ID
     * - must include a letter, number, and special character
     */
    public void setPassword(String pass)
    {
        if (pass == null || pass.trim().isEmpty())
        {
            throw new IllegalArgumentException("Password field cannot be empty.");
        }

        pass = pass.trim();

        // valid length
        if (pass.length() < 8 || pass.length() > 30)
        {
            throw new IllegalArgumentException("Password must be between 8 and 30 characters.");
        }

        // no spaces
        if (pass.contains(" "))
        {
            throw new IllegalArgumentException("Password cannot contain spaces.");
        }

        // password cannot be the same as userID
        if (userID != null && pass.equalsIgnoreCase(userID))
        {
            throw new IllegalArgumentException("Password cannot be the same as the UserID.");
        }

        boolean hasLetter = false;
        boolean hasNumber = false;
        boolean hasSpecialChar = false;

        // validate password by examination of each char
        for (int i = 0; i < pass.length(); i++)
        {
            char c = pass.charAt(i);

            if (Character.isLetter(c))
            {
                hasLetter = true;
            }
            else if (Character.isDigit(c))
            {
                hasNumber = true;
            }
            else
            {
                hasSpecialChar = true;
            }
        }

        if (!hasLetter || !hasNumber || !hasSpecialChar)
        {
            throw new IllegalArgumentException("Password must contain at least one letter, \none number, and one special character.");
        }

        this.password = pass;
    }
    // ============================================END OF GET/SETTERS=========================

    // ADDITIONAL METHODS //

    // General user helpers

    // checks to see if user logged in with a valid registered account
    public boolean authenticateLogin(String enteredID, String enteredPassword)
    {
        return userID.equals(enteredID.toLowerCase()) && password.equals(enteredPassword);
    }

    /**
     * Returns the user's difficulty access level based on age.
     *
     * Users under 12 are treated as Child-mode.
     * Everyone else gets Full-Access.
     */
    public String getDifficultyLevel()
    {
        int age = getAge();

        if(age < 12)
        {
            return "Child-mode";
        }
        else
        {
            return "Full-Access";
        }
    }

    //  Confirms the user's DOB during password reset or identity checks.
    public boolean verifyDOB(int month, int day, int year)
    {
        return bMonth == month && bDay == day && bYear == year;
    }

    // ===== Components to other sections of the application =====

    // === Wallet ===
    // Initialize Wallet with $245
    private WalletManager walletManager = new WalletManager(new Wallet(245.0));

    // Returns the user's wallet manager.
    public WalletManager getWalletManager() {
        return walletManager;
    }

    //  Replaces the wallet manager, usually during data loading.
    public void setWalletManager(WalletManager walletManager) {
        this.walletManager = walletManager;
    }

    // === First Savings Account Transfer PopUp===

    // Savings fee helper flag
    private boolean savingsTransferInfoShown = false;

    // Returns whether the savings transfer info popup has already been shown.
    public boolean isSavingsTransferInfoShown() {
        return savingsTransferInfoShown;
    }

    // Updates the savings transfer info popup flag.
    public void setSavingsTransferInfoShown(boolean shown) {
        this.savingsTransferInfoShown = shown;
    }

    // === Banking Manager ===
    private BankingManager bankingManager = new BankingManager();

    /**
     * Returns the banking manager for this user.
     * The notification manager is attached here so banking actions can generate alerts.
     */
    public BankingManager getBankingManager() {
        bankingManager.setNotificationManager(notificationManager);
        return bankingManager;
    }

    //  Replaces the banking manager, usually during data loading.
    public void setBankingManager(BankingManager bankingManager) {
        this.bankingManager = bankingManager;
    }

    // === Notification Manager ===

    private NotificationManager notificationManager = new NotificationManager();

    // Returns the user's notification manager.
    public NotificationManager getNotificationManager() {
        return notificationManager;
    }

    // Replaces the notification manager, usually during data loading.
    public void setNotificationManager(NotificationManager notificationManager) {
        this.notificationManager = notificationManager;
    }

    // === Scenario and Feedback ===

    private ScenarioManager scenarioManager = new ScenarioManager();
    private FeedbackManager feedbackManager = new FeedbackManager();

    //  Returns the user's scenario manager.
    public ScenarioManager getScenarioManager() {
        return scenarioManager;
    }

    // Replaces the scenario manager, usually during data loading.
    public void setScenarioManager(ScenarioManager scenarioManager) {
        this.scenarioManager = scenarioManager;
    }

    // Returns the user's feedback manager.
    public FeedbackManager getFeedbackManager() {
        return feedbackManager;
    }

    // Replaces the feedback manager, usually during data loading.
    public void setFeedbackManager(FeedbackManager feedbackManager) {
        this.feedbackManager = feedbackManager;
    }

    // === Asset Manager ===
    private AssetManager assetManager = new AssetManager();

    // Returns the user's asset manager.
    public AssetManager getAssetManager() {
        return assetManager;
    }

    //  Replaces the asset manager, usually during data loading.
    public void setAssetManager(AssetManager assetManager) {
        this.assetManager = assetManager;
    }

    // === Pending Debt ===

    // Pending debt tracking
    private double pendingDebt = 0;

    //  Returns the amount of unresolved pending debt tied to this user.
    public double getPendingDebt() {
        return pendingDebt;
    }

    /**
     * Adds to or reduces pending debt.
     * Negative amounts are allowed here so debt payments can subtract from the total.
     * The balance is clamped at zero so it never goes negative.
     */
    public void addPendingDebt(double amount) {
        this.pendingDebt += amount;
        if (this.pendingDebt < 0) {
            this.pendingDebt = 0;
        }
    }

    // Clears all pending debt.
    public void clearPendingDebt() {
        this.pendingDebt = 0;
    }

    // === Mandatory Scenarios ===

    // Mandatory scenario toggle
    private boolean mandatoryScenarioEnabled = false;

    //  Returns whether mandatory emergency scenarios are enabled for this user.
    public boolean isMandatoryScenarioEnabled() {
        return mandatoryScenarioEnabled;
    }

    // Updates the mandatory scenario toggle.
    public void setMandatoryScenarioEnabled(boolean enabled) {
        this.mandatoryScenarioEnabled = enabled;
    }

    // === Transfer Requests ===

    // Pending transfer requests
    private java.util.ArrayList<TransferRequest> pendingTransferRequests = new java.util.ArrayList<>();

    // Returns the user's list of outstanding transfer requests.
    public java.util.ArrayList<TransferRequest> getPendingTransferRequests() {
        return pendingTransferRequests;
    }

    // Adds a new transfer request generated by a scenario or transfer flow.
    public void addTransferRequest(TransferRequest request) {
        pendingTransferRequests.add(request);
    }

    // Removes any transfer requests that have already been completed.
    public void removeCompletedRequests() {
        pendingTransferRequests.removeIf(TransferRequest::isCompleted);
    }

    // === Help Section ===

    // Help manager
    private HelpManager helpManager = new HelpManager();

    // Returns the help manager used to supply static help content to the UI.
    public HelpManager getHelpManager() {
        return helpManager;
    }
}
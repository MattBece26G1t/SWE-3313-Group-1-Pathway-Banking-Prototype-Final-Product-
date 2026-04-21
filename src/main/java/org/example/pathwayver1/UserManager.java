/**
 * UserManager handles the collection of registered users in the program.
 *
 * This class is responsible for the core account management logic behind the
 * login and registration system. It keeps the in-memory list of users, loads
 * previously saved users when the program starts, checks for duplicate account
 * information during registration, and helps other controllers locate users
 * during login, recovery, and reset-password flows.
 *
 * It also works together with DataManager when user information needs to be
 * saved back to the text based storage files used by the project.
 */

package org.example.pathwayver1;

import java.util.ArrayList;

public class UserManager
{
    // UserAccount Attribute

    // Stores every user currently loaded into the program
    private ArrayList<UserAccount> users;
    // Handles file based save/load operations
    private DataManager dataManager;


    // UserAccount Constructor

    /**
     * Creates a UserManager and loads any previously saved users.
     *
     * When the program starts, this constructor rebuilds the in-memory user list
     * by asking DataManager to load all saved accounts from the storage files.
     */
    public UserManager()
    {
        users = new ArrayList<>();
        dataManager = new DataManager();

        // Load saved users on startup
        ArrayList<UserAccount> savedUsers = dataManager.loadAllUsers();
        for (UserAccount user : savedUsers)
        {
            users.add(user);
        }
    }

    /**
     * Registers a new user if their identifying information is unique.
     *
     * Before adding the user, this method checks whether another account already
     * uses the same:
     * - user ID
     * - email address
     * - phone number
     *
     * If any of those are already in use, registration is rejected with an
     * exception. Otherwise, the user is added to the current list.
     */
    public void registerUser(UserAccount newUser)
    {
        for (UserAccount user : users)
        {
            if (user.getUserID().equals(newUser.getUserID()))
            {
                throw new IllegalArgumentException("UserID already exists.");
            }

            if (user.getEmail().equalsIgnoreCase(newUser.getEmail()))
            {
                throw new IllegalArgumentException("Email already registered.");
            }

            if(user.getPhoneNumber().equals(newUser.getPhoneNumber()))
            {
                throw new IllegalArgumentException("Phone number already registered.");
            }
        }

        users.add(newUser);
    }

    /**
     * Authenticates a login attempt using the entered user ID and password.
     *
     * The entered ID is normalized to match the way user IDs are stored in the
     * system. If a matching account is found, that UserAccount object is
     * returned. If no match is found, an exception is thrown so the controller
     * can show an error message.
     */
    public UserAccount login(String id, String password)
    {
        id = id.trim().toLowerCase();
        password = password.trim();

        for (UserAccount user : users)
        {
            if (user.authenticateLogin(id, password))
            {
                return user;
            }
        }

        throw new IllegalArgumentException("Account does not exist or password incorrect.");
    }

    // ===== Lookup helpers for GUI controllers =====

    /**
     * Finds a user by either email address or phone number.
     * This is mainly used by the Recover ID flow. The method accepts one string
     * and tries both types of lookup:
     * - exact email match, ignoring case
     * - normalized phone number match using digits only
     * It returns the matching user if found, or null if no match exists.
     */
    public UserAccount findByEmailOrPhone(String value)
    {
        // Normalize phone input (strip formatting chars)
        String digitsOnly = "";
        for (int i = 0; i < value.length(); i++)
        {
            char c = value.charAt(i);
            if (Character.isDigit(c))
            {
                digitsOnly += c;
            }
        }

        for (UserAccount user : users)
        {
            if (user.getEmail().equalsIgnoreCase(value))
            {
                return user;
            }

            if (!digitsOnly.isEmpty() && user.getPhoneNumber().equals(digitsOnly))
            {
                return user;
            }
        }

        return null; // no match found
    }

    /**
     * Finds a user by email address only.
     * This method is mainly used during the password reset flow.
     * It returns the matching user if found, or null otherwise.
     */
    public UserAccount findByEmail(String email)
    {
        for (UserAccount user : users)
        {
            if (user.getEmail().equalsIgnoreCase(email))
            {
                return user;
            }
        }

        return null; // no match found
    }

    /**
     * Checks whether a user ID is already taken.
     * This is used during registration so the program can reject duplicate IDs
     * before creating the account.
     */
    public boolean isUserIDTaken(String id)
    {
        id = id.trim().toLowerCase();

        for (UserAccount user : users)
        {
            if (user.getUserID().equals(id))
            {
                return true;
            }
        }

        return false;
    }

    /**
     * Checks whether an email address is already registered.
     * This helps prevent multiple accounts from being created with the same email.
     */
    public boolean isEmailTaken(String email)
    {
        for (UserAccount user : users)
        {
            if (user.getEmail().equalsIgnoreCase(email))
            {
                return true;
            }
        }

        return false;
    }

    /**
     * Checks whether a phone number is already registered.
     * The method removes formatting first so values like:
     * (555) 123-4567 and 5551234567 are treated as the same number.
     */
    public boolean isPhoneTaken(String phone)
    {
        // normalize to digits only for comparison
        String digitsOnly = "";
        for (int i = 0; i < phone.length(); i++)
        {
            if (Character.isDigit(phone.charAt(i)))
            {
                digitsOnly += phone.charAt(i);
            }
        }

        for (UserAccount user : users)
        {
            if (user.getPhoneNumber().equals(digitsOnly))
            {
                return true;
            }
        }

        return false;
    }

    // ===== Save Components =====

    /**
     * Saves one user's current data to persistent storage.
     * This method creates a DataManager and asks it to write all of that user's
     * information back to the project files. It is used after important account
     * updates such as registration or settings changes.
     */
    public void saveUser(UserAccount user) {
        DataManager dm = new DataManager();
        dm.saveAll(user);
    }
}
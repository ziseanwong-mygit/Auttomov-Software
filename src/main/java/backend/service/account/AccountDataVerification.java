package main.java.backend.service.account;

import main.java.backend.model.UserAccount;
import main.java.backend.repository.impl.account.CustomerAccountRepository;
import main.java.backend.repository.impl.account.StaffAccountRepository;
import main.java.enums.data.Role;
import main.java.enums.system.DataFieldName;
import main.java.records.OperationResult;

/**
 * Algorithm logic:
 * 1. Check if the user ID or email address already exists in the system
 * 2. If it does, check if the user ID or email address is the same as the original one
 * 3. If it is the same, return a success flag
 * 4. If it is different, return an error message
 */
public class AccountDataVerification {

    // Create repository instances to retrieve user account record
    // Used to match the existing data with user's input to avoid duplicated data
    private static final CustomerAccountRepository customerAccRepository = CustomerAccountRepository.getInstance();
    private static final StaffAccountRepository staffAccRepository = StaffAccountRepository.getInstance();

    // Private constructor to prevent instantiation
    private AccountDataVerification() {
        throw new UnsupportedOperationException("This class is a utility class, which cannot be instantiated.");
    }

    public static OperationResult verifyUniqueUserId(Role userRole, String userId, String originalUserId) {

        UserAccount existingAcc = getAccountByUserId(userRole, userId);

        if (existingAcc != null && !existingAcc.getUserID().equals(originalUserId)) {
            return OperationResult.failed(DataFieldName.USER_ID,
                    "User ID already exists in the system.");
        }

        return OperationResult.success();
    }

    public static OperationResult verifyUniqueEmail(Role userRole, String emailAddress, String originalEmail) {

        UserAccount existingAcc = getAccountByEmail(userRole, emailAddress);

        if (existingAcc != null && !existingAcc.getEmailAddress().equals(originalEmail)) {
            return OperationResult.failed(DataFieldName.EMAIL_ADDRESS,
                    "Email address already exists in the system.");
        }

        return OperationResult.success();
    }

    private static UserAccount getAccountByUserId(Role userRole, String userId) {
        if (userRole == Role.CUSTOMER) {
            return customerAccRepository.getRecordByUserId(userId);
        }
        return staffAccRepository.getRecordByUserId(userId);
    }

    private static UserAccount getAccountByEmail(Role userRole, String email) {
        if (userRole == Role.CUSTOMER) {
            return customerAccRepository.getRecordByEmail(email);
        }
        return staffAccRepository.getRecordByEmail(email);
    }
}

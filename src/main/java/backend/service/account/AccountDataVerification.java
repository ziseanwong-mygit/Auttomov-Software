package main.java.backend.service.account;

import main.java.backend.repository.CustomerAccountRepository;
import main.java.backend.repository.StaffAccountRepository;
import main.java.enums.Role;
import main.java.records.OperationResult;

public class AccountDataVerification {

    // Create repository instances to retrieve user account record
    // Used to match the existing data with user's input to avoid duplicated data
    private static final CustomerAccountRepository customerAccRepository = CustomerAccountRepository.getInstance();
    private static final StaffAccountRepository staffAccRepository = StaffAccountRepository.getInstance();

    // Private constructor to prevent instantiation
    private AccountDataVerification() {
        throw new UnsupportedOperationException("This class is a utility class, which cannot be instantiated.");
    }

    public static OperationResult verifyUniqueUserId(Role userRole, String userId) {
        if (userRole == Role.CUSTOMER) {
            if (customerAccRepository.getAccountByUserId(userId) != null) {
                return OperationResult.failed("userID",
                        "User ID already exists in the system");
            }
        } else {
            if (staffAccRepository.getAccountByUserId(userId) != null) {
                return OperationResult.failed("userID",
                        "User ID already exists in the system");
            }
        }

        return OperationResult.success();
    }

    public static OperationResult verifyUniqueEmail(Role userRole, String emailAddress) {
        if (userRole == Role.CUSTOMER) {
            if (customerAccRepository.getAccountByEmail(emailAddress) != null) {
                return OperationResult.failed("email",
                        "Email address already exists in the system");
            }
        } else {
            if (staffAccRepository.getAccountByEmail(emailAddress) != null) {
                return OperationResult.failed("email",
                        "Email address already exists in the system");
            }
        }

        return OperationResult.success();
    }
}

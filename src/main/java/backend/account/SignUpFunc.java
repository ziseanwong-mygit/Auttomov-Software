package main.java.backend.account;

import main.java.backend.general.service.UserDataValidationFunc;
import main.java.enums.Role;
import main.java.records.ValidationResult;


public class SignUpFunc {

    public static ValidationResult beginSignUp(String userID, Role userRole, String firstName, String lastName,
                                   String emailAddress, String phoneNumber, String password, String confirmPassword) {

        ValidationResult validationResult = UserDataValidationFunc.validateUserDetails(
                userID, emailAddress, phoneNumber, password, confirmPassword);

        // TODO: Explain this to frontend - if got error, display them with window, this method would be over
        if (!validationResult.isValid()) {
            validationResult.fieldErrorsList().forEach(System.out::println);
            System.out.println();
            validationResult.fieldErrorsList().forEach(
                    fieldError -> System.out.println(fieldError.fieldName() + ": " + fieldError.errorMessage()));
            return validationResult;
        }

/*        UserAccount userAccount = new UserAccount(userID, userRole, firstName, lastName, emailAddress, phoneNumber,
                password);*/

        return validationResult;
    }

    public static void main(String[] args) {

        beginSignUp("012", Role.CUSTOMER, "John",
                "Doe", "johndoe123@email.com", "0123456789", "johndoe123",
                "johndoe123");









/*        UserAccount userAccount = new UserAccount("010101145678", Role.CUSTOMER, "John",
                "Doe", "johndoe123@email.com", "0123456789", "johndoe123",
                "johndoe123");

        // Validate user ID, email address, phone number, and password first
        // Then get result to determine whether to proceed with account registration
        Optional<String> userIDValidResult = UserDataValidationFunc.validateUserId(userAccount.getUserID());
        Optional<String> emailAddressValidResult = UserDataValidationFunc.validateEmailAddress(userAccount.getEmailAddress());
        Optional<String> phoneNumberValidResult = UserDataValidationFunc.validatePhoneNumber(userAccount.getPhoneNumber());
        Optional<String> passwordValidResult = UserDataValidationFunc.validatePassword(
                userAccount.getPassword(), userAccount.getConfirmPassword());

        // TODO: Need to adapt to Swing/AWT interface
        userIDValidResult.ifPresent(System.out::println);
        emailAddressValidResult.ifPresent(System.out::println);
        phoneNumberValidResult.ifPresent(System.out::println);
        passwordValidResult.ifPresent(System.out::println);

        FileIOFunc.writeTextFile();*/
    }
}

package main.java;

import main.java.backend.account.AccountSignUp;
import main.java.enums.EmploymentStatus;
import main.java.enums.JobPosition;
import main.java.enums.Role;
import main.java.records.OperationResult;

public class MainClass {

    public static void main(String[] args) {
/*        AccountSignUp accountSignUp = new AccountSignUp();
        OperationResult validationResult = accountSignUp.validateUserData(
                "010404145678", Role.CUSTOMER, "Wong", "Zi Sean",
                "wongzisean444@gmail.com", "012345678", "seanWong888&",
                "seanWong888&");
        if (validationResult.isValid()) {
            OperationResult registrationResult = accountSignUp.registerNewAccount();
            System.out.println(registrationResult.isValid() + "\n" + registrationResult.generalMessage());
        } else {
            System.out.println(validationResult.generalMessage() + "\n" + validationResult.fieldErrors());
        }*/

        AccountSignUp accountSignUp = new AccountSignUp();
        OperationResult validationResult = accountSignUp.validateUserData(
                "990216143714", Role.STAFF, "Sean", "Wong",
                "seanwong777@gmail.com", "012345678", "ZiSean4545#",
                "ZiSean4545#", JobPosition.MANAGER, EmploymentStatus.FULL_TIME, null);
        if (validationResult.isValid()) {
            OperationResult registrationResult = accountSignUp.registerNewAccount();
            System.out.println(registrationResult.isValid() + "\n" + registrationResult.generalMessage());
        } else {
            System.out.println(validationResult.generalMessage() + "\n" + validationResult.fieldErrors());
        }
    }
}

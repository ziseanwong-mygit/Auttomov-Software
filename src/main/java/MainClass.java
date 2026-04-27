package main.java;

import main.java.backend.general.AccountDelete;
import main.java.backend.general.AccountUpdate;
import main.java.enums.EmploymentStatus;
import main.java.enums.JobPosition;
import main.java.enums.Role;
import main.java.records.OperationResult;

import java.time.LocalDate;

public class MainClass {

    public static void main(String[] args) {
/*        AccountUpdate accountUpdate = new AccountUpdate(
                "92cc1f5e-d3df-4682-a5da-411c196ecff7", "990216143719", Role.STAFF,
                "Sean", "Wong", "seanwong778@gmail.com", "0123446725",
                JobPosition.MANAGER, LocalDate.now(), EmploymentStatus.FULL_TIME, null);
        OperationResult result = accountUpdate.executeProcess();
        System.out.println(result.isValid() + "\n" + result.generalMessage());*/

        AccountDelete accountDelete = new AccountDelete(
                "b3af5b58-337a-4756-bcb4-82c19c4b0453", Role.CUSTOMER);
        OperationResult result = accountDelete.executeProcess();
        System.out.println(result.isValid() + "\n" + result.generalMessage());
    }
}




/*        AccountCreate accountSignUp = new AccountCreate();
        OperationResult validationResult = accountSignUp.validateUserData(
                "980506148294", Role.CUSTOMER, "Jeffrey", "Kingston",
                "jeffrey77@gmail.com", "0123848593", "Jeffrey7!7!",
                "Jeffrey7!7!");
        if (validationResult.isValid()) {
            OperationResult registrationResult = accountSignUp.registerNewAccount();
            System.out.println(registrationResult.isValid() + "\n" + registrationResult.generalMessage());
        } else {
            System.out.println(validationResult.generalMessage() + "\n" + validationResult.fieldErrors());
        }*/

/*        // Initialize the repositories for reading and writing files
        UserAccountRepository.getInstance();
        StaffAccountRepository.getInstance();

        OperationResult validationResult = AccountCreate.accountCreationProcess(
                "950417149172", Role.STAFF, "Henry", "Chong Weng",
                "henryCW88@gmail.com", "0128287342", "hEnryChong58%",
                "hEnryChong58%", JobPosition.COUNTER_STAFF, EmploymentStatus.FULL_TIME, null);
        if (validationResult.isValid()) {
            System.out.println(validationResult.generalMessage());
        } else {
            System.out.println(validationResult.generalMessage() + "\n" + validationResult.fieldErrors());
        }*/

/*        AccountLogin accountLogin = new AccountLogin(
                Role.CUSTOMER, "wongzisean444@gmail.com", "seanWong888&");
        OperationResult loginResult = accountLogin.verifyLoginInputs();

        if (loginResult.isValid()) {
            System.out.println("Login successful!");
        } else {
            if (loginResult.fieldErrors().containsKey("email")) {
                System.out.println(loginResult.fieldErrors().get("email"));
            } else {
                System.out.println(loginResult.generalMessage());
            }
        }*/

/*        AccountUpdate accountUpdate = new AccountUpdate(
                "c26b455e-178a-46f4-932e-af9c8061e730", "010404145678", Role.CUSTOMER,
                "Wong", "Zi Sean", "wongzisean444@gmail.com", "0123446464");
        OperationResult result = accountUpdate.executeProcess();
        System.out.println(result.isValid() + "\n" + result.generalMessage());*/

/*        AccountDelete accountDelete = new AccountDelete(
                "b3af5b58-337a-4756-bcb4-82c19c4b0453", Role.CUSTOMER);
        OperationResult result = accountDelete.executeProcess();
        System.out.println(result.isValid() + "\n" + result.generalMessage());*/

/*
    Customer
    Email: wongzisean444@gmail.com
    Password: seanWong888&

    Email: jeffrey77@gmail.com
    Password: Jeffrey7!7!

    Staff
    Email: seanwong777@gmail.com
    Password: ZiSean4545#

    Email: henryCW88@gmail.com
    Password: hEnryChong58%
 */
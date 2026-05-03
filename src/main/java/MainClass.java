package main.java;

import main.java.backend.model.UserAccount;
import main.java.backend.repository.CustomerAccountRepository;
import main.java.backend.repository.StaffAccountRepository;
import main.java.backend.service.ServiceOperation;
import main.java.backend.service.account.*;
import main.java.enums.EmploymentStatus;
import main.java.enums.JobPosition;
import main.java.enums.Role;
import main.java.records.OperationResult;

import java.time.LocalDate;

public class MainClass {

    public static void main(String[] args) {

    }
}





/*        ServiceOperation operation = new AccountCreation(
                "950417549172", Role.STAFF, "Henry", "Chong Weng",
                "henryCW38@gmail.com", "0128287342", "hEnryChong58%",
                "hEnryChong58%", JobPosition.COUNTER_STAFF, EmploymentStatus.FULL_TIME, null);
        OperationResult result = operation.executeProcess();
        if (result.isValid()) {
            System.out.println(result.generalMessage());
        } else {
            System.out.println(result.generalMessage() + "\n" + result.fieldErrors());
        }*/

/*        ServiceOperation operation = new AccountLogin(Role.CUSTOMER,
                "wongzisean444@gmail.com", "seanWong888&");
        OperationResult loginResult = operation.executeProcess();

        if (loginResult.isValid()) {
            System.out.println("Login successful!");
        } else {
            if (loginResult.fieldErrors().containsKey("email")) {
                System.out.println(loginResult.fieldErrors().get("email"));
            } else {
                System.out.println(loginResult.generalMessage());
            }
        }*/

/*        AccountRetrieval accountRetrieval = new AccountRetrieval(Role.CUSTOMER);
        UserAccount test = accountRetrieval.getAccountByAccId();
        System.out.println(test);*/

/*        AccountRetrieval accountRetrieval = new AccountRetrieval(Role.STAFF, "9e016d45-28c3-429d-845a-20e23dbfca04");
        UserAccount test = accountRetrieval.getAccountByAccId();
        System.out.println(test);*/

/*        ServiceOperation operation = new AccountUpdate(
                "c26b455e-178a-46f4-932e-af9c8061e730", "010404145678", Role.CUSTOMER,
                "Wong", "Zi Sean", "wongzisean444@gmail.com", "0123446464");
        OperationResult result = operation.executeProcess();
        System.out.println(result.isValid() + "\n" + result.generalMessage());*/

/*        ServiceOperation operation = new AccountUpdate(
                "92cc1f5e-d3df-4682-a5da-411c196ecff7", "990216143719", Role.STAFF,
                "Sean", "Wong", "seanwong778@gmail.com", "0123446725",
                JobPosition.MANAGER, LocalDate.now(), EmploymentStatus.FULL_TIME, null);
        OperationResult result = operation.executeProcess();
        System.out.println(result.isValid() + "\n" + result.generalMessage());*/

/*        ServiceOperation operation = new AccountDeletion("b3af5b58-337a-4766-bcb5-82c19c4b0453", Role.CUSTOMER);
        OperationResult result = operation.executeProcess();
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
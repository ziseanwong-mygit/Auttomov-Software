package main.java;

public class MainClass {

    public static void main(String[] args) {

    }
}



/*        UserAccountDTO userAccountDTO = new UserAccountDTO("020603153820", Role.CUSTOMER, "Yung",
                "Hoi An", "yungHoiAn33@gmail.com", "0118382674",
                "yungHoiAn33#", "yungHoiAn33#");
        AccountController accountController = new AccountController();
        OperationResult result = accountController.createAccount(userAccountDTO);
        if (result.isValid()) {
            System.out.println(result.generalMessage());
        } else {
            System.out.println(result.generalMessage() + "\n" + result.fieldErrors());
        }*/

/*        StaffAccountDTO staffAccountDTO = new StaffAccountDTO("980705141272", Role.STAFF, "Peng",
                "Yi Ying", "pengy89@gmail.com", "0123764785",
                "pengYy78y#", "pengYy78y#", JobPosition.TECHNICIAN,
                EmploymentStatus.FULL_TIME, "Tyre maintenance and repair");
        AccountController accountController = new AccountController();
        OperationResult result = accountController.createAccount(staffAccountDTO);
        if (result.isValid()) {
            System.out.println(result.generalMessage());
        } else {
            System.out.println(result.generalMessage() + "\n" + result.fieldErrors());
        }*/

/*        StaffAccountDTO staffAccountDTO = new StaffAccountDTO("93000ad9-d745-4e13-8a84-4b59afcc7439",
                "980705148272", Role.STAFF, "Peng", "Yi Ying",
                "pengy88@gmail.com", "0123764785", JobPosition.TECHNICIAN,
                LocalDate.parse("2026-05-07"), EmploymentStatus.FULL_TIME,
                "Tyre maintenance and repair");
        AccountController accountController = new AccountController();
        OperationResult result = accountController.updateAccount(staffAccountDTO);
        if (result.isValid()) {
            System.out.println(result.generalMessage());
        } else {
            System.out.println(result.generalMessage() + "\n" + result.fieldErrors());
        }*/

/*        GeneralProcess operation = new AccountLogin(Role.CUSTOMER,
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
        UserAccountDTO test = accountRetrieval.getAccountByAccId();
        System.out.println(test);*/

/*        AccountRetrieval accountRetrieval = new AccountRetrieval(Role.STAFF, "9e016d45-28c3-429d-845a-20e23dbfca04");
        UserAccountDTO test = accountRetrieval.getAccountByAccId();
        System.out.println(test);*/

/*        GeneralProcess operation = new AccountUpdate(
                "c26b455e-178a-46f4-932e-af9c8061e730", "010404145678", Role.CUSTOMER,
                "Wong", "Zi Sean", "wongzisean444@gmail.com", "0123446464");
        OperationResult result = operation.executeProcess();
        System.out.println(result.isValid() + "\n" + result.generalMessage());*/

/*        GeneralProcess operation = new AccountUpdate(
                "92cc1f5e-d3df-4682-a5da-411c196ecff7", "990216143719", Role.STAFF,
                "Sean", "Wong", "seanwong778@gmail.com", "0123446725",
                JobPosition.MANAGER, LocalDate.now(), EmploymentStatus.FULL_TIME, null);
        OperationResult result = operation.executeProcess();
        System.out.println(result.isValid() + "\n" + result.generalMessage());*/

/*        UserAccountDTO userAccountDTO = new UserAccountDTO("9e016d45-28c3-429d-845a-20e23dbfca04",
                Role.STAFF, AccountStatus.ACTIVE);
        AccountController accountController = new AccountController();
        OperationResult result = accountController.updateAccountStatus(userAccountDTO);
        if (result.isValid()) {
            System.out.println(result.generalMessage());
        } else {
            System.out.println(result.generalMessage() + "\n" + result.fieldErrors());
        }*/

/*        UserAccountDTO userAccountDTO = new UserAccountDTO("c26b455e-178a-46f4-932e-af9c8061e730",
                Role.CUSTOMER, "SeaNwOng98&!", "SeaNwOng98&!");
        AccountController accountController = new AccountController();
        OperationResult result = accountController.updateAccountPassword(userAccountDTO);
        if (result.isValid()) {
            System.out.println(result.generalMessage());
        } else {
            System.out.println(result.generalMessage() + "\n" + result.fieldErrors());
        }*/

/*        GeneralProcess operation = new AccountDeletion("b3af5b58-337a-4766-bcb5-82c19c4b0453", Role.CUSTOMER);
        OperationResult result = operation.executeProcess();
        System.out.println(result.isValid() + "\n" + result.generalMessage());*/

/*        ServiceActivityDTO serviceActivityDTO = new ServiceActivityDTO("Service Activity 1",
                "hello", 100.00);
        ServiceActivityController serviceActivityController = new ServiceActivityController();
        OperationResult result = serviceActivityController.createServiceActivity(serviceActivityDTO);
        if (result.isValid()) {
            System.out.println(result.generalMessage());
        } else {
            System.out.println(result.generalMessage() + "\n" + result.fieldErrors());
        }*/

/*
    Customer
    Email: wongzisean444@gmail.com
    Password: SeaNwOng98&!

    Email: jeffrey77@gmail.com
    Password: Jeffrey7!7!

    Email: yungHoiAn33@gmail.com
    Password: yungHoiAn33#

    Staff
    Email: seanwong777@gmail.com
    Password: ZiSean4545#

    Email: henryCW88@gmail.com
    Password: hEnryChong58%

    Email: pengy88@gmail.com
    Password: pengYy78y#
 */
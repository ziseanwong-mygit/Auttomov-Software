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

/*        StaffAccountDTO staffAccountDTO = new StaffAccountDTO("9e016d45-28c3-429d-845a-20e23dbfca04",
                AccountStatus.ACTIVE);
        AccountController accountController = new AccountController();
        OperationResult result = accountController.updateStaffAccountStatus(staffAccountDTO);
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

/*        ServiceActivityDTO serviceActivityDTO = new ServiceActivityDTO("Tyre Balancing",
                "Tyre balancing ensures even weight distribution across each " +
                        "wheel-and-tire assembly.", 120.00);
        ServiceActivityController serviceActivityController = new ServiceActivityController();
        OperationResult result = serviceActivityController.createServiceActivity(serviceActivityDTO);
        if (result.isValid()) {
            System.out.println(result.generalMessage());
        } else {
            System.out.println(result.generalMessage() + "\n" + result.fieldErrors());
        }*/

/*        ServiceActivityDTO serviceActivityDTO = new ServiceActivityDTO(
                "a9f320f7-1a1e-4ef0-b793-b41d5a3cd862", "Tyre Balancing",
                "Tyre balancing ensures even weight distribution across each " +
                        "wheel-and-tire assembly.", 130.00);
        ServiceActivityController serviceActivityController = new ServiceActivityController();
        OperationResult result = serviceActivityController.updateServiceActivity(serviceActivityDTO);
        if (result.isValid()) {
            System.out.println(result.generalMessage());
        } else {
            System.out.println(result.generalMessage() + "\n" + result.fieldErrors());
        }*/

/*        ServiceActivityController serviceActivityController = new ServiceActivityController();
        OperationResult result = serviceActivityController.deleteServiceActivity(
                "e4ed0e85-e3a6-42df-937a-443d21207ff1");
        if (result.isValid()) {
            System.out.println(result.generalMessage());
        } else {
            System.out.println(result.generalMessage() + "\n" + result.fieldErrors());
        }*/

/*        ServiceActivityController serviceActivityController = new ServiceActivityController();
        OperationResult result = serviceActivityController.retrieveServiceActivityById("a9f320f7-1a1e-4ef0-b793-b41d5a3cd862");
        if (result.isValid()) {
            ServiceActivity data = (ServiceActivity) result.data();
            System.out.println(data);
        } else {
            System.out.println(result.generalMessage() + "\n" + result.fieldErrors());
        }*/

/*        List<String> serviceActivityList = List.of("A0001", "A0002", "A0003");
        List<String> vehiclePartList = List.of("B0001");

        ServicePackageDTO servicePackageDTO = new ServicePackageDTO(
                 "Test Service 1", "This is a normal service.",
                ServicePackageType.NORMAL, serviceActivityList, vehiclePartList, 30.00);

        ServicePackageController servicePackageController = new ServicePackageController();
        OperationResult result = servicePackageController.createServicePackage(servicePackageDTO);
        if (result.isValid()) {
            System.out.println(result.generalMessage());
        } else {
            System.out.println(result.generalMessage() + "\n" + result.fieldErrors());
        }*/

/*        List<String> serviceActivityList = List.of("A0001", "A0002", "A0003");
        List<String> vehiclePartList = List.of("B0001", "B0002");

        ServicePackageDTO servicePackageDTO = new ServicePackageDTO(
                "8f036a65-847e-41b5-9154-1ad639cf052f", "Test Service 1.1",
                "This is a major service.", ServicePackageType.MAJOR, serviceActivityList,
                vehiclePartList, 40.00);

        ServicePackageController servicePackageController = new ServicePackageController();
        OperationResult result = servicePackageController.updateServicePackage(servicePackageDTO);
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
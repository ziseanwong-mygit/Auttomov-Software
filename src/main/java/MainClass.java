package main.java;

public class MainClass {

    public static void main(String[] args) {

/*        SwingUtilities.invokeLater(() -> {
            new mainMenuUI().setVisible(true);
        });*/
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

/*        StaffAccountDTO staffAccountDTO = new StaffAccountDTO("891120143921", Role.STAFF, "Choong",
                "Man Lee", "choongML01@gmail.com", "0128238928",
                "cHoongMlee98$", "cHoongMlee98$", JobPosition.TECHNICIAN,
                EmploymentStatus.FULL_TIME, "Oil change and maintenance");
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

/*        List<String> serviceActivityList = List.of(
                "a9f320f7-1a1e-4ef0-b793-b41d5a3cd862", "cd9756dd-ba56-4f3a-b690-8fa6af5ba9b1");
        List<String> vehiclePartList = List.of(
                "18e5ee0a-3fd1-44f5-b673-6cde7bc131ca", "7bb17911-a1a2-4884-9d77-03c2754a7a66");

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
/*
        List<String> serviceActivityList = List.of(
                "a9f320f7-1a1e-4ef0-b793-b41d5a3cd862");
        List<String> vehiclePartList = List.of(
                "18e5ee0a-3fd1-44f5-b673-6cde7bc131ca");

        ServicePackageDTO servicePackageDTO = new ServicePackageDTO(
                "14a564e4-d21c-4b21-a456-732e27c6a839", "Test Service 1.2",
                "This is a normal service here.", ServicePackageType.NORMAL, serviceActivityList,
                vehiclePartList, 20.00);

        ServicePackageController servicePackageController = new ServicePackageController();
        OperationResult result = servicePackageController.updateServicePackage(servicePackageDTO);
        if (result.isValid()) {
            System.out.println(result.generalMessage());
        } else {
            System.out.println(result.generalMessage() + "\n" + result.fieldErrors());
        }*/


//      Read account records

/*        AccountController accountController = new AccountController();
        Optional<List<UserAccount>> result = accountController.retrieveAllAccounts(Role.STAFF);

        if (result.isPresent()) {
            for (UserAccount userAccount : result.get()) {
                System.out.println(userAccount);
            }
        } else {
            System.out.println("No records found.");
        }*/

/*        String uniqueID = "henryCW88@gmail.com";
        DataFieldName accUniqueIdType = DataFieldName.EMAIL_ADDRESS;
        Role userRole = Role.STAFF;

        AccountController accountController = new AccountController();
        OperationResult uniqueIdValidation = accountController.validateUniqueId(uniqueID, accUniqueIdType);

        if (uniqueIdValidation.isValid()) {
            Optional<UserAccount> userAccount = accountController.retrieveAccountById(uniqueID, userRole);
            if (userAccount.isPresent()) {
                System.out.println("Account found:");
                UserAccount user = userAccount.get();
                System.out.println(user.getUserID());
            } else {
                System.out.println("Account not found.");
            }
        } else {
            System.out.println(uniqueIdValidation.fieldErrors().get(DataFieldName.EMAIL_ADDRESS));
        }*/

/*        List<String> accountIDs = List.of("c26b455e-178a-46f4-932e-af9c8061e730",
                "b3af5b58-337a-4766-bcb4-82c19c4b0453");
        Role userRole = Role.CUSTOMER;
        DataFieldName accUniqueIdType = DataFieldName.RECORD_ID;

        AccountController accountController = new AccountController();
        OperationResult validationResult = accountController.validateUniqueIds(accountIDs, accUniqueIdType);

        if (validationResult.isValid()) {
            Optional<List<UserAccount>> userAccounts = accountController.retrieveAccountsByIds(accountIDs, userRole);
            if (userAccounts.isPresent()) {
                System.out.println("Account found:");
                for (UserAccount userAccount : userAccounts.get()) {
                    System.out.println(userAccount);
                }
            } else {
                System.out.println("Account not found.");
            }
        } else {
            System.out.println(validationResult.generalMessage());
        }*/

/*        ServicePackageController servicePackageController = new ServicePackageController();
        Optional<List<ServicePackage>> result = servicePackageController.retrieveAllServicePackages();

        if (result.isPresent()) {
            for (ServicePackage servicePackage : result.get()) {
                System.out.println(servicePackage);
            }
        } else {
            System.out.println("No records found.");
        }*/


//      Read service packages
/*        String svcpkgID = "bacf62f3-7c31-46ea-a7b5-86a91c5597b9";

        ServicePackageController servicePackageController = new ServicePackageController();
        OperationResult uniqueIdValidation = servicePackageController.validateServicePackageId(svcpkgID);

        if (uniqueIdValidation.isValid()) {
            Optional<ServicePackage> servicePackage = servicePackageController.retrieveServicePackageById(svcpkgID);
            if (servicePackage.isPresent()) {
                System.out.println("Pacakge found:");
                ServicePackage svcpkg = servicePackage.get();
                System.out.println(svcpkg.getServicePackageName());
            } else {
                System.out.println("Package not found.");
            }
        } else {
            System.out.println(uniqueIdValidation.generalMessage());
        }*/

        /*List<String> svcpkgIDs = List.of("bacf62f3-7c31-46ea-a7b5-86a91c5597b9",
                "8f036a65-847e-41b5-9154-1ad639cf052f");

        ServicePackageController servicePackageController = new ServicePackageController();
        OperationResult validationResult = servicePackageController.validateServicePackageIds(svcpkgIDs);

        if (validationResult.isValid()) {
            Optional<List<ServicePackage>> servicepackages =
                    servicePackageController.retrieveServicePackagesByIds(svcpkgIDs);
            if (servicepackages.isPresent()) {
                System.out.println("Package found:");
                for (ServicePackage servicePackage : servicepackages.get()) {
                    System.out.println(servicePackage);
                }
            } else {
                System.out.println("Package not found.");
            }
        } else {
            System.out.println(validationResult.generalMessage());
        }*/


/*        LocalDate date1 = LocalDate.parse("2026-05-23");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("hh:mm a", Locale.ENGLISH);
        LocalTime time1 = LocalTime.parse("10:15 PM", formatter);

        AppointmentDTO appointmentDTO = new AppointmentDTO("Appointment Test1",
                "This is for testing1.", "14a564e4-d21c-4b21-a456-732e27c6a839",
                date1, time1 );
        AppointmentController appointmentController = new AppointmentController();
        OperationResult result = appointmentController.createAppointment(appointmentDTO);
        if (result.isValid()) {
            System.out.println(result.generalMessage());
        } else {
            System.out.println(result.generalMessage() + "\n" + result.fieldErrors());
        }*/

        /*LocalDate date1 = LocalDate.parse("2026-05-24");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("hh:mm a", Locale.ENGLISH);
        LocalTime time1 = LocalTime.parse("12:28 PM", formatter);

        AppointmentDTO appointmentDTO = new AppointmentDTO("e8af637f-273b-494c-b860-7a0d017687c0",
                "AppointmentTest11", "This is testing 1.", "14a564e4-d21c-4b21-a456-732e27c6a839",
                date1, time1, AppointmentStatus.PENDING);
        AppointmentController appointmentController = new AppointmentController();
        OperationResult result = appointmentController.updateAppointment(appointmentDTO);
        if (result.isValid()) {
            System.out.println(result.generalMessage());
        } else {
            System.out.println(result.generalMessage() + "\n" + result.fieldErrors());
        }*/

/*        List<String> technicianIDs = List.of("93000ad9-d745-4e13-8a84-4b59afcc7439",
                "6e643dc8-a9bd-4d21-bc2b-b182b8bba6c8");

        AppointmentDTO appointmentDTO = new AppointmentDTO("e8af637f-273b-494c-b860-7a0d017687c0",
                technicianIDs);
        AppointmentController appointmentController = new AppointmentController();
        OperationResult result = appointmentController.assignTechnicianToAppointment(appointmentDTO);
        if (result.isValid()) {
            System.out.println(result.generalMessage());
        } else {
            System.out.println(result.generalMessage() + "\n" + result.fieldErrors());
        }*/


/*        AppointmentController appointmentController = new AppointmentController();
        Optional<List<Appointment>> appointments = appointmentController.retrieveAllAppointments();
        if (appointments.isPresent()) {
            List<Appointment> appointmentsList = appointments.get();
            for (Appointment appointment : appointmentsList) {
                System.out.println(appointment);
            }
        } else {
            System.out.println("No appointments found.");
        }*/

/*        ServicePackageController servicePackageController = new ServicePackageController();
        Optional<List<ServicePackage>> servicePackages = servicePackageController.retrieveAllServicePackages();
        if (servicePackages.isPresent()) {
            List<ServicePackage> servicePackagesList = servicePackages.get();
            for (ServicePackage servicePackage : servicePackagesList) {
                System.out.println(servicePackage);
            }
        }*/

/*        InvoiceDTO invoiceDTO = new InvoiceDTO(
                "9e016d45-28c3-429d-845a-20e23dbfca04", "e8af637f-273b-494c-b860-7a0d017687c0",
                20.00, "Hello World");
        InvoiceController invoiceController = new InvoiceController();
        OperationResult result = invoiceController.createInvoice(invoiceDTO);
        if (result.isValid()) {
            System.out.println(result.generalMessage());
        } else {
            System.out.println(result.generalMessage() + "\n" + result.fieldErrors());
        }*/

/*        PaymentRecordDTO paymentRecordDTO = new PaymentRecordDTO("897460f4-c066-4e59-8981-d7932f4d6831",
                "c26b455e-178a-46f4-932e-af9c8061e730", 42.00, PaymentMethod.E_WALLET,
                "Hello world");
        PaymentRecordController paymentRecordController = new PaymentRecordController();
        OperationResult paymentResult = paymentRecordController.createPaymentRecord(paymentRecordDTO);
        if (paymentResult.isValid()) {
            System.out.println(paymentResult.generalMessage());
        } else {
            System.out.println(paymentResult.generalMessage() + "\n" + paymentResult.fieldErrors());
        }*/

/*        ReceiptDTO receiptDTO = new ReceiptDTO("9e016d45-28c3-429d-845a-20e23dbfca04",
                "897460f4-c066-4e59-8981-d7932f4d6831", "d3fbce89-c172-4cca-b4bf-a55e52b21557",
                "Hello world");
        ReceiptController receiptController = new ReceiptController();
        OperationResult receiptResult = receiptController.createReceipt(receiptDTO);
        if (receiptResult.isValid()) {
            System.out.println(receiptResult.generalMessage());
        } else {
            System.out.println(receiptResult.generalMessage() + "\n" + receiptResult.fieldErrors());
        }*/

/*        PaymentRecordController paymentRecordController = new PaymentRecordController();
        Optional<List<PaymentRecord>> paymentRecords = paymentRecordController.retrieveAllPaymentRecords();
        if (paymentRecords.isPresent()) {
            for (PaymentRecord paymentRecord : paymentRecords.get()) {
                System.out.println(paymentRecord);
            }
        } else {
            System.out.println("No records found.");
        }*/

/*        ServiceActivityDTO serviceActivityDTO = new ServiceActivityDTO("a9f320f7-1a1e-4ef0-b793-b41d5a3cd862",
                "Tyre Balancing",
                "Tyre balancing ensures even weight distribution across each wheel-and-tire assembly.",
                60.00);
        ServiceActivityController serviceActivityController = new ServiceActivityController();
        OperationResult result = serviceActivityController.updateServiceActivity(serviceActivityDTO);
        if (result.isValid()) {
            System.out.println(result.generalMessage());
        } else {
            System.out.println(result.generalMessage() + "\n" + result.fieldErrors());
        }*/

/*        VehiclePartDTO vehiclePartDTO = new VehiclePartDTO("18e5ee0a-3fd1-44f5-b673-6cde7bc131ca",
                "Suzuki Engine 1500cc", "This is a good engine.",
                "Engine", "Suzuki", "1500cc",
                4000.00, 3);
        VehiclePartController vehiclePartController = new VehiclePartController();
        OperationResult result = vehiclePartController.updateVehiclePart(vehiclePartDTO);
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
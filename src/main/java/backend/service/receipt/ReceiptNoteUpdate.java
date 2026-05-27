package main.java.backend.service.receipt;

import main.java.backend.model.Receipt;
import main.java.backend.repository.impl.ReceiptRepository;
import main.java.backend.service.RecordWriter;
import main.java.dtos.ReceiptDTO;
import main.java.records.FieldComparison;
import main.java.records.OperationResult;

public class ReceiptNoteUpdate implements RecordWriter {

    private final ReceiptRepository receiptRepository;
    private final ReceiptDTO receiptDTO;

    public ReceiptNoteUpdate(ReceiptRepository receiptRepository, ReceiptDTO receiptDTO) {
        this.receiptRepository = receiptRepository;
        this.receiptDTO = receiptDTO;
    }

    @Override
    public OperationResult execute() {

        // Check if the receipt exists with the given ID
        Receipt existingReceipt = receiptRepository.getRecordById(receiptDTO.getReceiptID());
        if (existingReceipt == null) {
            return OperationResult.failed("The receipt with ID: " + receiptDTO.getReceiptID() +
                    " does not exist in the system.");
        }

        // Check if the input note is different from the current note
        FieldComparison<String> noteChange = new FieldComparison<>("receiptNote",
                existingReceipt.getReceiptNote(), receiptDTO.getReceiptNote(), null);

        if (!noteChange.isChanged()) {
            return OperationResult.failed("The input receipt note provided is the same as the " +
                    "current note. Please edit the note for update.");
        }

        // Set the new note into the existing receipt record
        existingReceipt.setReceiptNote(receiptDTO.getReceiptNote());

        // Begin updating receipt record in the repository
        return receiptRepository.updateRecord(existingReceipt);
    }
}

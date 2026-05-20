package main.java.backend.service;

import main.java.records.OperationResult;

import java.util.List;

public interface RecordReader {

    List<?> getAllRecords();

    OperationResult getRecordById(String id);
}

package main.java.backend.repository.general;

import main.java.records.OperationResult;

import java.util.List;

public interface CrudRepository<T> extends FileRepository {

    OperationResult createRecord(T record);
    OperationResult updateRecord(T record);
    OperationResult deleteRecord(T record);
    List<T> getAllRecords();
    T getRecordById(String recordId);
}

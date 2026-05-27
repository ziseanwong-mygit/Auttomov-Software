package main.java.backend.service;

import java.util.List;
import java.util.Optional;

public interface RecordReader<T> {

    Optional<List<T>> getAllRecords();

    Optional<T> getRecordById(String id);

    Optional<List<T>> getRecordsByIds(List<String> ids);
}

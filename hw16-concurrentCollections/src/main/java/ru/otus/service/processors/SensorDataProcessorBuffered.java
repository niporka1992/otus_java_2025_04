package ru.otus.service.processors;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;
import ru.otus.lib.SensorDataBufferedWriter;
import ru.otus.model.SensorData;

public class SensorDataProcessorBuffered {
    private final int bufferSize;
    private final SensorDataBufferedWriter writer;
    private final List<SensorData> buffer = new ArrayList<>();
    private final ReentrantLock lock = new ReentrantLock();

    public SensorDataProcessorBuffered(int bufferSize, SensorDataBufferedWriter writer) {
        this.bufferSize = bufferSize;
        this.writer = writer;
    }

    public void process(SensorData data) {
        lock.lock();
        try {
            buffer.add(data);
            if (buffer.size() >= bufferSize) {
                flush();
            }
        } finally {
            lock.unlock();
        }
    }

    public void flush() {
        List<SensorData> toWrite;
        lock.lock();
        try {
            if (buffer.isEmpty()) {
                return;
            }
            toWrite = new ArrayList<>(buffer);
            buffer.clear();
        } finally {
            lock.unlock();
        }
        List<SensorData> sortedList = toWrite.stream()
                .sorted(Comparator.comparing(SensorData::getMeasurementTime))
                .toList();

        writer.writeBufferedData(sortedList);
    }

    public void onProcessingEnd() {
        flush();
    }
}

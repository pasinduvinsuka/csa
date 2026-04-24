package com.smartcampus.data;

import com.smartcampus.models.Room;
import com.smartcampus.models.Sensor;
import com.smartcampus.models.SensorReading;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;
import java.util.List;

public class DataStore {
    private static final DataStore instance = new DataStore();


    public final Map<String, Room> rooms = new ConcurrentHashMap<>();
    public final Map<String, Sensor> sensors = new ConcurrentHashMap<>();
    public final Map<String, List<SensorReading>> readingsHistory = new ConcurrentHashMap<>();

    private DataStore() {

    }

    public static DataStore getInstance() {
        return instance;
    }
}
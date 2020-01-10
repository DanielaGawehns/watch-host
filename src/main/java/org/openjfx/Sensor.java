package org.openjfx;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;

import org.jetbrains.annotations.NotNull;

import nl.liacs.watch.protocol.server.WrappedConnection;
import nl.liacs.watch.protocol.types.MessageParameter;

/*
public enum Sensor {
    ACCELEROMETER,
    GRAVITY,
    LINEAR_ACCELERATION,
    MAGNETIC,
    ROTATION_VECTOR,
    ORIENTATION,
    GYROSCOPE,
    LIGHT,
    PROXIMITY,
    PRESSURE,
    ULTRAVIOLET,
    TEMPERATURE,
    HUMIDITY,
    HRM;

    @NotNull
    public String toFriendlyString() {
        switch (this) {
            case HRM:
                return "Heart Rate BPM)";
            case PRESSURE:
                return "Pressure (Pa)";
            case ACCELEROMETER:
                return "Accelerometer (XX)";
            case GRAVITY:
                return "Gravity (XX)";
            case LINEAR_ACCELERATION:
                return "Linear Acceleration (XX)";
            case MAGNETIC:
                return "Magnetic (XX)";
            case ROTATION_VECTOR:
                return "Rotation Vector (XX)";
            case ORIENTATION:
                return "Orientation (XX)";
            case GYROSCOPE:
                return "Gyroscope (XX)";
            case LIGHT:
                return "Light (XX)";
            case PROXIMITY:
                return "Proximity (XX)";
            case ULTRAVIOLET:
                return "Ultraviolet (XX)";
            case TEMPERATURE:
                return "Temperature (XX)";
            case HUMIDITY:
                return "Humidity (XX)";
        }

        throw new IllegalStateException("unknown sensor");
    }
*/

public class Sensor {
    public boolean active;
    public double interval;
    public String name;
    public double[] range;
    public double resolution;

    public Sensor(String name) {
        this.name = name;
    }

    public static CompletableFuture<Sensor> getByName(@NotNull String name, @NotNull WrappedConnection connection) throws IOException {
        var futures = new ArrayList<CompletableFuture<MessageParameter[]>>();
        futures.add(connection.getValues("sensor." + name + ".active"));
        futures.add(connection.getValues("sensor." + name + ".interval"));
        futures.add(connection.getValues("sensor." + name + ".range"));
        futures.add(connection.getValues("sensor." + name + ".resolution"));

        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[futures.size()])).thenApply(x -> {
            var sensor = new Sensor(name);
            try {
                sensor.active = futures.get(0).get()[0].asInteger().getValue() == 1;
                sensor.interval = futures.get(1).get()[0].asDouble().getValue();

                sensor.range = new double[2];
                sensor.range[0] = futures.get(2).get()[0].asDouble().getValue();
                sensor.range[1] = futures.get(2).get()[1].asDouble().getValue();

                sensor.resolution = futures.get(3).get()[0].asDouble().getValue();
            } catch (Exception e) {
                System.out.println(e);
            }
            return sensor;
        });
    }
}

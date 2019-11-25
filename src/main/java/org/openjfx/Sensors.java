package org.openjfx;

public class Sensors {
    public static String sensorNameToFriendlyString(String sensorName) {
        switch (sensorName) {
            case "HRM":                  return "Heart Rate BPM)";
            case "PRESSURE":             return "Pressure (Pa)";
            case "ACCELEROMETER":        return "Accelerometer (XX)";
            case "GRAVITY":              return "Gravity (XX)";
            case "LINEAR ACCELERATION":  return "Linear Acceleration (XX)";
            case "MAGNETIC":             return "Magnetic (XX)";
            case "ROTATION VECTOR":      return "Rotation Vector (XX)";
            case "ORIENTATION":          return "Orientation (XX)";
            case "GYROSCOPE":            return "Gyroscope (XX)";
            case "LIGHT":                return "Light (XX)";
            case "PROXIMITY":            return "Proximity (XX)";
            case "ULTRAVIOLET":          return "Ultraviolet (XX)";
            case "TEMPERATURE":          return "Temperature (XX)";
            case "HUMIDITY":             return "Humidity (XX)";
        }

        throw new IllegalStateException("unknown sensor");
    }
}

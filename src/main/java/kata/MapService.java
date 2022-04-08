package kata;

import java.time.Duration;

public class MapService {

    // TODO: Figure out what is that?
    private final double R = 6373.0;
    // in km/h
    private double averageSpeed = 50.0;

    public double getAverageSpeed() {
        return averageSpeed;
    }

    public Duration calculateETA(Coordinates from, Coordinates to) {
        var distance = this.calculateDistance(
                from.getLatitude(),
                from.getLongitude(),
                to.getLatitude(),
                to.getLongitude()
        );
        Double v = distance / this.averageSpeed * Duration.ofHours(1).toMinutes();
        return Duration.ofMinutes(v.longValue());
    }

    public void updateAverageSpeed(Duration elapsedTime, float latitude, float longitude, float otherLatitude, float otherLongitude) {
        var distance = this.calculateDistance(latitude, longitude, otherLatitude, otherLongitude);
        var updatedSpeed = distance / (elapsedTime.getSeconds() / (double) Duration.ofHours(1).toSeconds());
        this.averageSpeed = updatedSpeed;
    }

    private double calculateDistance(float latitude, float longitude, float otherLatitude, float otherLongitude) {
        var d1 = latitude * (Math.PI / 180.0);
        var num1 = longitude * (Math.PI / 180.0);
        var d2 = otherLatitude * (Math.PI / 180.0);
        var num2 = otherLongitude * (Math.PI / 180.0) - num1;
        var d3 = Math.pow(Math.sin((d2 - d1) / 2.0), 2.0) + Math.cos(d1) * Math.cos(d2) * Math.pow(Math.sin(num2 / 2.0), 2.0);

        return R * (2.0 * Math.atan2(Math.sqrt(d3), Math.sqrt(1.0 - d3)));
    }
}

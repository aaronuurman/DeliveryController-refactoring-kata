package kata;

import java.time.Duration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static kata.factories.CoordinatesFactory.TALLINN_CENTER;
import static kata.factories.CoordinatesFactory.TARTU_CENTER;
import static kata.factories.CoordinatesFactory.TARTU_TURU_29B;
import static kata.factories.CoordinatesFactory.VILJANDI_CENTER;
import static kata.factories.DeliveryEventFactory.DELIVERY_TIME;
import static kata.factories.DeliveryFactory.createLateScheduleWithThreeDeliveries;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MapServiceTest {

    private MapService mapService;

    @BeforeEach()
    void setUp() {
        mapService = new MapService();
    }

    @Test
    void calculateETA_averageSpeed50_sameCityDeliveryTakesLessTimeThanDifferentCityDelivery() {
        // Arrange & Act
        var sameCity = mapService.calculateETA(
                TARTU_CENTER.getLatitude(),
                TARTU_CENTER.getLongitude(),
                TARTU_TURU_29B.getLatitude(),
                TARTU_TURU_29B.getLongitude()
        );
        var differentCity = mapService.calculateETA(
                TARTU_CENTER.getLatitude(),
                TARTU_CENTER.getLongitude(),
                TALLINN_CENTER.getLatitude(),
                TALLINN_CENTER.getLongitude()
        );

        // Assert
        assertTrue(sameCity.getSeconds() < differentCity.getSeconds());
    }

    @Test
    void calculateETA_averageSpeed50_differentCityShortDistanceTakesLessTimeThanDifferentCityLongDistance() {
        // Arrange & Act
        var differentCityShortDistance = mapService.calculateETA(
                TARTU_CENTER.getLatitude(),
                TARTU_CENTER.getLongitude(),
                VILJANDI_CENTER.getLatitude(),
                VILJANDI_CENTER.getLongitude()
        );
        var differentCityLongDistance = mapService.calculateETA(
                TARTU_CENTER.getLatitude(),
                TARTU_CENTER.getLongitude(),
                TALLINN_CENTER.getLatitude(),
                TALLINN_CENTER.getLongitude()
        );

        // Assert
        assertTrue(differentCityShortDistance.getSeconds() < differentCityLongDistance.getSeconds());
    }

    @Test
    void updateAverageSpeed_lateDelivery_newAverageSpeed() {
        // Arrange
        var schedule = createLateScheduleWithThreeDeliveries(DELIVERY_TIME.minusMinutes(15));
        var previousDelivery = schedule.get(0);
        var delivery = schedule.get(1);
        var elapsedTime = Duration.between(previousDelivery.getTimeOfDelivery(), delivery.getTimeOfDelivery());
        assertEquals(50, mapService.getAverageSpeed());

        // Act
        mapService.updateAverageSpeed(
                elapsedTime,
                previousDelivery.getLatitude(),
                previousDelivery.getLongitude(),
                delivery.getLatitude(),
                delivery.getLongitude()
        );

        // Assert
        assertEquals(54.16357790736967, mapService.getAverageSpeed());
    }

}

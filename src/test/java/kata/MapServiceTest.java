package kata;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static kata.factories.CoordinatesFactory.TALLINN_CENTER;
import static kata.factories.CoordinatesFactory.TARTU_CENTER;
import static kata.factories.CoordinatesFactory.TARTU_TURU_29B;
import static kata.factories.CoordinatesFactory.VILJANDI_CENTER;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MapServiceTest {

    private MapService mapService;

    @BeforeEach()
    void setUp() {
        mapService = new MapService();
    }

    @Test
    @Disabled("Potential bug in calculateETA when delivery is in same city, but different address")
    void calculateETA_averageSpeed50_sameCityDeliveryTakesLessTimeThanDifferentCityDelivery() {
        // Arrange & Act
        var sameCity = mapService.calculateETA(
                TARTU_CENTER.getLatitude(),
                TARTU_CENTER.getLatitude(),
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

}

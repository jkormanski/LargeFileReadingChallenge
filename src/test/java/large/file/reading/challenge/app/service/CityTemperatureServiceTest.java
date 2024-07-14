package large.file.reading.challenge.app.service;

import large.file.reading.challenge.app.dto.AnnualAverageTemperatureDTO;
import large.file.reading.challenge.app.dto.CityTemperatureResponseDTO;
import large.file.reading.challenge.app.exception.CityNotFoundException;
import large.file.reading.challenge.app.exception.InvalidCityException;
import large.file.reading.challenge.app.store.DataStore;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * @Author Jarosław Kormański
 */
@ExtendWith(MockitoExtension.class)
public class CityTemperatureServiceTest {

    @Mock
    private DataStore dataStore;

    @InjectMocks
    private CityTemperatureService cityTemperatureService;

    @Test
    public void testGetAnnualAverageTemperaturesForCityNotFound() {
        when(dataStore.getData("Londyn")).thenReturn(null);

        Exception exception = assertThrows(CityNotFoundException.class, () -> {
            cityTemperatureService.getAnnualAverageTemperatures("Londyn");
        });

        String expectedMessage = "Data for city Londyn was not found";
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    public void testGetAnnualAverageTemperaturesForExistingCity() {
        List<AnnualAverageTemperatureDTO> annualAverageTemperatureDTOList = Arrays.asList(
                new AnnualAverageTemperatureDTO("2018", 19.5)
        );
        when(dataStore.getData("Szczecin")).thenReturn(annualAverageTemperatureDTOList);

        CityTemperatureResponseDTO response = cityTemperatureService.getAnnualAverageTemperatures("Szczecin");

        assertEquals("Szczecin", response.getCity());
        assertEquals(annualAverageTemperatureDTOList, response.getData());
        assertEquals(HttpStatus.OK, response.getStatus());
    }

    @Test
    public void testGetAnnualAverageTemperaturesForEmptyCity() {
        Exception exception = assertThrows(InvalidCityException.class, () -> {
            cityTemperatureService.getAnnualAverageTemperatures("");
        });

        String expectedMessage = "City cannot be null or empty";
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
    }

}

package large.file.reading.challenge.app.service;

import large.file.reading.challenge.app.dto.AnnualAverageTemperatureDTO;
import large.file.reading.challenge.app.store.DataStore;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * @author Jaroslaw Kormański
 */
@ExtendWith(MockitoExtension.class)
public class CsvFileDataLoaderServiceTest {

    @Mock
    private DataStore dataStore;

    @InjectMocks
    private CsvFileDataLoaderService csvFileDataLoaderService;

    @Captor
    private ArgumentCaptor<String> cityCaptor;

    @Captor
    private ArgumentCaptor<List<AnnualAverageTemperatureDTO>> temperatureListCaptor;

    @Test
    public void testLoadCsvData() {
        ReflectionTestUtils.setField(csvFileDataLoaderService, "csvFilePath", "src/test/resources/test.csv");
        csvFileDataLoaderService.loadCsvData();

        verify(dataStore, times(1)).clearCache();
        verify(dataStore, times(6)).addData(cityCaptor.capture(), temperatureListCaptor.capture());

        List<String> capturedCities = cityCaptor.getAllValues();
        List<List<AnnualAverageTemperatureDTO>> capturedTemperatureLists = temperatureListCaptor.getAllValues();

        assertEquals(6, capturedCities.size());
        assertEquals(6, capturedTemperatureLists.size());

        assertEquals("Gdańsk", capturedCities.get(0));
        assertEquals(6, capturedTemperatureLists.get(0).size());
        assertEquals("2019", capturedTemperatureLists.get(0).get(0).getYear());
        assertEquals(13.86 , capturedTemperatureLists.get(0).get(0).getAverageTemperature());

        assertEquals("Warszawa", capturedCities.get(1));
        assertEquals(6, capturedTemperatureLists.get(1).size());
        assertEquals("2019", capturedTemperatureLists.get(1).get(0).getYear());
        assertEquals(13.81, capturedTemperatureLists.get(1).get(0).getAverageTemperature());
    }
}

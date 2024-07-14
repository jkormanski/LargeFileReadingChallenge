package large.file.reading.challenge.app.controller;

import large.file.reading.challenge.app.dto.CityTemperatureResponseDTO;
import large.file.reading.challenge.app.exception.InvalidCityException;
import large.file.reading.challenge.app.service.CityTemperatureService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.FileNotFoundException;

/**
 * @author Jarosław Kormański
 * CityTemperatureController is a REST controller that provides an endpoint to retrieve
 * the annual average temperatures for a specified city.
 */
@RestController
@RequestMapping("/city/temperature")
@Validated
@AllArgsConstructor
public class CityTemperatureController {

    private final CityTemperatureService cityTemperatureService;

    /**
     * Retrieves the annual average temperatures for a specified city.
     *
     * @param city the name of the city
     * @return a ResponseEntity containing the CityTemperatureResponseDTO with the city's temperature data and the response status
     */
    @GetMapping("/annual/average")
    public ResponseEntity<CityTemperatureResponseDTO> getAnnualAverageTemperatures(@RequestParam("city")  String city) {
        CityTemperatureResponseDTO response = cityTemperatureService.getAnnualAverageTemperatures(city);
        return new ResponseEntity<>(response, response.getStatus());
    }
}

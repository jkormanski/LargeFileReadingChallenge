package large.file.reading.challenge.app.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpStatus;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CityTemperatureResponseDTO {

    private String city;

    private List<AnnualAverageTemperatureDTO> data;

    @JsonIgnore
    private HttpStatus status;

}

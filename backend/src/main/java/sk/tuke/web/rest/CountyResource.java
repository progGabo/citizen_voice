package sk.tuke.web.rest;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sk.tuke.service.impl.CountyService;
import sk.tuke.service.dto.CountyDTO;

import java.util.List;

@RestController
@RequestMapping("/api/county")
public class CountyResource {



    private final CountyService countyService;

    public CountyResource(CountyService countyService) {
        this.countyService = countyService;
    }

    @GetMapping("/all")
    public ResponseEntity<List<CountyDTO>> getAllCounties() {

        List<CountyDTO> counties = countyService.getAllCounties();
        return ResponseEntity.ok(counties);
    }
}

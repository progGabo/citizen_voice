package sk.tuke.web.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sk.tuke.service.CityService;
import sk.tuke.service.dto.CityDTO;
import sk.tuke.service.dto.specific.ArticleSpecificDTO;
import sk.tuke.service.dto.specific.CitySpecificDTO;

@RestController
@RequestMapping("/api/city")
public class CityResource {

    private final Logger log = LoggerFactory.getLogger(EventResource.class);
    private final CityService cityService;

    public CityResource(CityService cityService) {
        this.cityService = cityService;
    }

    @GetMapping("/all")
    public ResponseEntity<Page<CitySpecificDTO>> getAllCities(Pageable pageable) {
        log.debug("REST request to get all cities");
        Page<CitySpecificDTO> page = cityService.findAll(pageable);
        return ResponseEntity.ok(page);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CitySpecificDTO> getCityName(@PathVariable Long id) {
        log.info("Request to retrieve City with ID: {}", id);

        return cityService.findOneById(id)
            .map(ResponseEntity::ok)
            .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

}

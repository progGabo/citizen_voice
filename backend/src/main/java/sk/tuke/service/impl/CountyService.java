package sk.tuke.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import sk.tuke.repository.CountyRepository;
import sk.tuke.service.dto.CountyDTO;
import sk.tuke.service.mapper.CountyMapper;
import tech.jhipster.config.JHipsterProperties;

import java.util.List;

@Service
public class CountyService {

    private static final Logger LOG = LoggerFactory.getLogger(CountyService.class);

    private final CountyRepository countyRepository;

    private final CountyMapper countyMapper;

    private final JHipsterProperties jHipsterProperties;


    public CountyService(CountyRepository countyRepository,
                         JHipsterProperties jHipsterProperties,
                         CountyMapper countyMapper) {
        this.countyRepository = countyRepository;
        this.jHipsterProperties = jHipsterProperties;
        this.countyMapper = countyMapper;
    }


    public List<CountyDTO> getAllCounties(){

        return countyMapper.toDto(countyRepository.findAll());
    }
}

package sk.tuke.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sk.tuke.repository.CityRepository;
import sk.tuke.service.CityService;
import sk.tuke.service.dto.ArticleDTO;
import sk.tuke.service.dto.CityDTO;
import sk.tuke.service.dto.specific.ArticleSpecificDTO;
import sk.tuke.service.dto.specific.CitySpecificDTO;
import sk.tuke.service.mapper.CityMapper;
import sk.tuke.service.mapper.specific.CitySpecificMapper;

import java.util.Optional;

@Service
@Transactional
public class CityServiceImpl implements CityService {

    private static final Logger LOG = LoggerFactory.getLogger(CityServiceImpl.class);

    private final CityRepository cityRepository;
    private final CityMapper cityMapper;
    private final CitySpecificMapper citySpecificMapper;

    public CityServiceImpl(CityRepository cityRepository, CityMapper cityMapper, CitySpecificMapper citySpecificMapper) {
        this.cityRepository = cityRepository;
        this.cityMapper = cityMapper;
        this.citySpecificMapper = citySpecificMapper;

    }

    @Transactional(readOnly = true)
    @Override
    public Page<CitySpecificDTO> findAll(Pageable pageable) {
        return cityRepository.findAll(pageable).map(citySpecificMapper::toDto);
    }

    @Override
    public Optional<CitySpecificDTO> findOneById(Long id) {
        return cityRepository.findById(id)
            .map(citySpecificMapper::toDto);
    }


}

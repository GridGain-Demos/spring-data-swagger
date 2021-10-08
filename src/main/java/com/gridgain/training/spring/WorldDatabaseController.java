package com.gridgain.training.spring;

import com.gridgain.training.spring.model.Country;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class WorldDatabaseController {
    @Autowired
    CityRepository cityRepository;
    @Autowired
    CountryRepository countryRepository;

    @GetMapping("/mostPopulated")
    public List<List<?>> getMostPopulatedCities(@RequestParam(value = "limit", required = false) Integer limit) {
        return cityRepository.findTopXMostPopulatedCities(limit);
    }

    @GetMapping("/countryPopulationAboveLimit")
    public List<Country> getCountryPopulationAboveLimit(@RequestParam(value="population", required = false) Integer population){
        return countryRepository.findByPopulationGreaterThanOrderByPopulationDesc(population);
    }

}

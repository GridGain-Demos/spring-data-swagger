package com.gridgain.training.spring;

import com.gridgain.training.spring.model.Country;
import org.apache.ignite.springdata.repository.IgniteRepository;
import org.apache.ignite.springdata.repository.config.RepositoryConfig;
import org.springframework.stereotype.Repository;

import java.util.List;


@RepositoryConfig(cacheName = "Country")
@Repository
public interface CountryRepository extends IgniteRepository<Country, String> {

    public List<Country> findByPopulationGreaterThanOrderByPopulationDesc(int population);


}


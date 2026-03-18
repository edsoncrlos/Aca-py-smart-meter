package br.ufma.lsdi.SmartMeterVerifierSSI.services;

import java.util.List;

public interface ResourceCatalog {
    List<String> getResourcesByLocation(Double lat, Double lon, Double radius);
}

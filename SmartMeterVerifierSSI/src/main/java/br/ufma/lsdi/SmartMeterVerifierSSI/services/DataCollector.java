package br.ufma.lsdi.SmartMeterVerifierSSI.services;

import br.ufma.lsdi.SmartMeterVerifierSSI.utils.InterscityCollectorQuery;

public interface DataCollector {
    String getResourcesHistoryData(Integer index, InterscityCollectorQuery query);
    String getResourceHistoryData(String uuid, Integer index, InterscityCollectorQuery query);
}

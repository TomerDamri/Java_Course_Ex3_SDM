package model.response;

import java.util.List;

import model.ZoneDTO;

public class GetZonesResponse {

    List<ZoneDTO> systemZones;

    public GetZonesResponse (List<ZoneDTO> systemZones) {
        this.systemZones = systemZones;
    }

    public List<ZoneDTO> getSystemZones () {
        return systemZones;
    }
}

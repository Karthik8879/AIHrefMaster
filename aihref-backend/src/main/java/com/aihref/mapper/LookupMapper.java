package com.aihref.mapper;

import com.aihref.dto.LookupResponse;
import com.aihref.model.LookupDocument;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface LookupMapper {
    
    @Mapping(target = "id", source = "id")
    LookupResponse toResponse(LookupDocument document);
    
    LookupResponse.TrafficData toTrafficResponse(LookupDocument.TrafficData traffic);
    LookupResponse.WebVitalsData toWebVitalsResponse(LookupDocument.WebVitalsData webVitals);
    LookupResponse.SeoData toSeoResponse(LookupDocument.SeoData seo);
    LookupResponse.TechData toTechResponse(LookupDocument.TechData tech);
    LookupResponse.LiveData toLiveResponse(LookupDocument.LiveData live);
    LookupResponse.ThreatData toThreatResponse(LookupDocument.ThreatData threat);
    
    LookupResponse.CountryData toCountryResponse(LookupDocument.CountryData country);
    LookupResponse.DeviceData toDeviceResponse(LookupDocument.DeviceData device);
    LookupResponse.ReferrerData toReferrerResponse(LookupDocument.ReferrerData referrer);
}

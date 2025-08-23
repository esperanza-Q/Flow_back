package org.example.flow.geo.dto;

import java.util.List;


public class KakaoLocalDtos {
    public record Meta(int total_count, int pageable_count, boolean is_end) {}


    public record Address(String address_name, String region_1depth_name, String region_2depth_name,
                          String region_3depth_name, String mountain_yn, String main_address_no,
                          String sub_address_no, String zip_code) {}


    public record RoadAddress(String address_name, String region_1depth_name, String region_2depth_name,
                              String region_3depth_name, String road_name, String underground_yn,
                              String main_building_no, String sub_building_no, String building_name,
                              String zone_no) {}


    // 주소검색/좌표→주소 공통 문서
    public record Document(String address_name, String x, String y,
                           Address address, RoadAddress road_address) {}


    // 키워드 검색 결과 문서
    public record PlaceDocument(String id, String place_name, String category_name, String category_group_code,
                                String phone, String address_name, String road_address_name,
                                String x, String y, String place_url, String distance) {}


    public record AddressSearchResponse(Meta meta, List<Document> documents) {}
    public record ReverseGeocodeResponse(Meta meta, List<Document> documents) {}
    public record KeywordSearchResponse(Meta meta, List<PlaceDocument> documents) {}
}
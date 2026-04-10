package com.devnear.web.service.map;

import com.devnear.global.config.KakaoMapsProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

/**
 * Kakao Maps API 연동 서비스
 * 주소 검색 및 좌표 변환 기능 제공
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class KakaoMapsService {

    private final KakaoMapsProperties kakaoMapsProperties;
    private static final String KAKAO_API_BASE_URL = "https://dapi.kakao.com";

    /**
     * 주소 검색 API
     * @param address 검색할 주소
     * @return 주소 검색 결과 (JSON)
     */
    public String searchAddress(String address) {
        log.info("Kakao Maps API - 주소 검색: {}", address);

        // TODO: 실제 API 연동 구현
        // RestClient를 사용하여 Kakao API 호출
        /*
        RestClient restClient = RestClient.create();

        String response = restClient.get()
            .uri(KAKAO_API_BASE_URL + "/v2/local/search/address.json?query={query}", address)
            .header(HttpHeaders.AUTHORIZATION, "KakaoAK " + kakaoMapsProperties.getRestApiKey())
            .retrieve()
            .body(String.class);

        return response;
        */

        log.warn("Kakao Maps API Key가 설정되지 않았습니다. API 연동을 위해 환경 변수를 설정하세요.");
        return "{}"; // 임시 응답
    }

    /**
     * 좌표로 주소 검색 API (Reverse Geocoding)
     * @param latitude 위도
     * @param longitude 경도
     * @return 좌표에 해당하는 주소 정보 (JSON)
     */
    public String getAddressByCoordinates(Double latitude, Double longitude) {
        log.info("Kakao Maps API - 좌표로 주소 검색: lat={}, lng={}", latitude, longitude);

        // TODO: 실제 API 연동 구현
        /*
        RestClient restClient = RestClient.create();

        String response = restClient.get()
            .uri(KAKAO_API_BASE_URL + "/v2/local/geo/coord2address.json?x={x}&y={y}",
                longitude, latitude)
            .header(HttpHeaders.AUTHORIZATION, "KakaoAK " + kakaoMapsProperties.getRestApiKey())
            .retrieve()
            .body(String.class);

        return response;
        */

        log.warn("Kakao Maps API Key가 설정되지 않았습니다. API 연동을 위해 환경 변수를 설정하세요.");
        return "{}"; // 임시 응답
    }

    /**
     * 키워드로 장소 검색 API
     * @param keyword 검색 키워드
     * @return 장소 검색 결과 (JSON)
     */
    public String searchPlace(String keyword) {
        log.info("Kakao Maps API - 장소 검색: {}", keyword);

        // TODO: 실제 API 연동 구현
        /*
        RestClient restClient = RestClient.create();

        String response = restClient.get()
            .uri(KAKAO_API_BASE_URL + "/v2/local/search/keyword.json?query={query}", keyword)
            .header(HttpHeaders.AUTHORIZATION, "KakaoAK " + kakaoMapsProperties.getRestApiKey())
            .retrieve()
            .body(String.class);

        return response;
        */

        log.warn("Kakao Maps API Key가 설정되지 않았습니다. API 연동을 위해 환경 변수를 설정하세요.");
        return "{}"; // 임시 응답
    }

    /**
     * API Key 설정 여부 확인
     * @return API Key가 설정되어 있으면 true
     */
    public boolean isApiKeyConfigured() {
        return kakaoMapsProperties.getRestApiKey() != null
            && !kakaoMapsProperties.getRestApiKey().equals("your-kakao-rest-api-key");
    }
}

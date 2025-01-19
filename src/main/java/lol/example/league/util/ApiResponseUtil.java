package lol.example.league.util;

import org.springframework.http.HttpStatus;

public class ApiResponseUtil {

    private ApiResponseUtil() {
    }

    public static ApiResponse success() {
        ApiResult successResult = new ApiResult(HttpStatus.OK.value(), "Success");
        return new ApiResponse(successResult);
    }

    public static ApiResponse success(Object data) {
        ApiResult successResult = new ApiResult(HttpStatus.OK.value(), "Success");
        return new ApiDataResponse<>(successResult, data);
    }

//    public static SlicePageResponse slicePage(Object data, Object nextPage) {
//        ApiResult successResult = new ApiResult(HttpStatus.OK.value(), "Success");
//        return new SlicePageResponse(successResult, data, nextPage);
//    }

    public static ApiResponse fail(int errorCode, String message) {
        ApiResult errorResult = new ApiResult(errorCode, message);
        return new ApiResponse(errorResult);
    }
}

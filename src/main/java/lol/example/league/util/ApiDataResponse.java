package lol.example.league.util;

import lombok.NonNull;

public class ApiDataResponse<T> extends ApiResponse {
    protected T data;

    public ApiDataResponse() {
        this(new ApiResult(0, (String)null), (T) null);
    }

    public ApiDataResponse(T data) {
        this(new ApiResult(0, (String)null), data);
    }

    public ApiDataResponse(int code, T data) {
        this(new ApiResult(code, (String)null), data);
    }

    public ApiDataResponse(int code, String msg, T data) {
        this(new ApiResult(code, msg), data);
    }

    public ApiDataResponse(@NonNull ApiResult result, T data) {
        super(result);
        if (result == null) {
            throw new NullPointerException("result is marked non-null but is null");
        } else {
            this.data = data;
        }
    }

    public T getData() {
        return this.data;
    }

    public void setData(final T data) {
        this.data = data;
    }

    public String toString() {
        return "ApiDataResponse(data=" + this.getData() + ")";
    }
}

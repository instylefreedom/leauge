package lol.example.league.util;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.NonNull;

import java.io.Serializable;

public class ApiResponse implements Serializable {
    protected @NonNull ApiResult result;

    public ApiResponse(ApiResult result) {
        this.result = result;
    }

    @JsonIgnore
    public boolean isSuccessful() {
        return this.getResultCode() == 0;
    }

    @JsonIgnore
    public int getResultCode() {
        return this.result.getCode();
    }

    @JsonIgnore
    public String getResultMessage() {
        return this.result.getMsg();
    }

    public ApiResponse() {
    }

    public @NonNull ApiResult getResult() {
        return this.result;
    }

    public void setResult(final @NonNull ApiResult result) {
        if (result == null) {
            throw new NullPointerException("result is marked non-null but is null");
        } else {
            this.result = result;
        }
    }

    public String toString() {
        return "ApiResponse(result=" + this.getResult() + ")";
    }
}

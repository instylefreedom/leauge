package lol.example.league.util;

import java.io.Serializable;

public class ApiResult implements Serializable {
    protected int code;
    protected String msg;

    public ApiResult() {
        this(0, (String)null);
    }

    public ApiResult(final int code, final String msg) {
        this.code = code;
        this.msg = msg;
    }

    public int getCode() {
        return this.code;
    }

    public String getMsg() {
        return this.msg;
    }

    public void setCode(final int code) {
        this.code = code;
    }

    public void setMsg(final String msg) {
        this.msg = msg;
    }

    public String toString() {
        return "ApiResult(code=" + this.getCode() + ", msg=" + this.getMsg() + ")";
    }
}

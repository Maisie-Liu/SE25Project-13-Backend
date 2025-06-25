package com.campus.trading.dto;

/**
 * 通用API响应对象
 *
 * @param <T> 响应数据类型
 */
public class ApiResponse<T> {

    /**
     * 响应码
     */
    private Integer code;

    /**
     * 响应消息
     */
    private String message;

    /**
     * 响应数据
     */
    private T data;
    
    public ApiResponse() {
    }
    
    public ApiResponse(Integer code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }
    
    public Integer getCode() {
        return code;
    }
    
    public void setCode(Integer code) {
        this.code = code;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    public T getData() {
        return data;
    }
    
    public void setData(T data) {
        this.data = data;
    }

    /**
     * 成功响应
     *
     * @param data 响应数据
     * @param <T>  数据类型
     * @return API响应对象
     */
    public static <T> ApiResponse<T> success(T data) {
        ApiResponse<T> response = new ApiResponse<>();
        response.setCode(200);
        response.setMessage("操作成功");
        response.setData(data);
        return response;
    }

    /**
     * 成功响应
     *
     * @param message 响应消息
     * @param data    响应数据
     * @param <T>     数据类型
     * @return API响应对象
     */
    public static <T> ApiResponse<T> success(String message, T data) {
        ApiResponse<T> response = new ApiResponse<>();
        response.setCode(200);
        response.setMessage(message);
        response.setData(data);
        return response;
    }

    /**
     * 失败响应
     *
     * @param code    响应码
     * @param message 响应消息
     * @param <T>     数据类型
     * @return API响应对象
     */
    public static <T> ApiResponse<T> error(Integer code, String message) {
        ApiResponse<T> response = new ApiResponse<>();
        response.setCode(code);
        response.setMessage(message);
        return response;
    }

    /**
     * 参数错误响应
     *
     * @param message 响应消息
     * @param <T>     数据类型
     * @return API响应对象
     */
    public static <T> ApiResponse<T> badRequest(String message) {
        return error(400, message);
    }

    /**
     * 未授权响应
     *
     * @param message 响应消息
     * @param <T>     数据类型
     * @return API响应对象
     */
    public static <T> ApiResponse<T> unauthorized(String message) {
        return error(401, message);
    }

    /**
     * 禁止访问响应
     *
     * @param message 响应消息
     * @param <T>     数据类型
     * @return API响应对象
     */
    public static <T> ApiResponse<T> forbidden(String message) {
        return error(403, message);
    }

    /**
     * 资源不存在响应
     *
     * @param message 响应消息
     * @param <T>     数据类型
     * @return API响应对象
     */
    public static <T> ApiResponse<T> notFound(String message) {
        return error(404, message);
    }

    /**
     * 服务器错误响应
     *
     * @param message 响应消息
     * @param <T>     数据类型
     * @return API响应对象
     */
    public static <T> ApiResponse<T> serverError(String message) {
        return error(500, message);
    }
} 
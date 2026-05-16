package com.stu.dormitory.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.net.http.HttpRequest;

// Dùng để tạo lỗi riêng cho hệ thống dùng kế thừa từ bên RuntimeException ( Không có throw và try- catch )
@Getter
public class AppException extends RuntimeException{
    private final HttpStatus status;

    public AppException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }
}
/**
 * Kiến thức ta đã sử dụng tính kế thùa từ lớp RuntimeException và phát triển 1 cái là status
 * Tác dụng khi mà có lỗi ta cần tạo thêm biến lỗi và status nhen
 */
/**
 * Nên ghi lỗi bằng tiếng anh sao này đễ debug cho team
 * Cách sử dụng ví dụ
 * Nếu mà ta có lỗi là Phòng đã đầy sinh viên rồi thì ta có thể dùng là
 *  if( room.isFull())
 *  {
 *      throw new AppException("Room is full", HttpStatus.BAD_REQUEST)
 *  }
 */


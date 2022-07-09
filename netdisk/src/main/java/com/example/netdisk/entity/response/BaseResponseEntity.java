package com.example.netdisk.entity.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.io.Serializable;


@Data
public class BaseResponseEntity implements Serializable{

    private int code;

    private String msg;

    private Object resData;

}

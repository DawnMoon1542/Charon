package com.dawnmoon.charon.model.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

public class MQRequests {

    @Data
    @Schema
    public static class SendMsg {

        @Schema(description = "队列名称", example = "/")
        @NotBlank(message = "队列名称不能为空")
        private String queueName;

        @Schema(description = "要发送的消息内容", example = "test")
        @NotBlank(message = "发送内容不可为空")
        private String msg;
    }

}

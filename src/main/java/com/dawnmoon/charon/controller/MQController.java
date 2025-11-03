package com.dawnmoon.charon.controller;

import com.dawnmoon.charon.common.api.ApiResponse;
import com.dawnmoon.charon.model.request.MQRequests;
import com.dawnmoon.charon.service.MQService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "消息队列", description = "消息队列相关接口")
@RestController
@RequestMapping("/api/mq")
@RequiredArgsConstructor
@Validated
public class MQController {

    private final MQService mqService;

    @Operation(summary = "发送消息", description = "向指定的queue发送一条消息")
    @PostMapping
    public ApiResponse<Void> message(@RequestBody @Valid MQRequests.SendMsg request) {

        mqService.sendMsg(request.getQueueName(), request.getMsg());
        return ApiResponse.success();
    }
}

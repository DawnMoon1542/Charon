package com.dawnmoon.charon.util;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.dawnmoon.charon.model.response.PageResponse;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 分页工具类
 */
public class PageUtil {

    /**
     * 将 IPage 转换为 PageResponse
     */
    public static <T> PageResponse<T> toPageResponse(IPage<T> page) {
        PageResponse<T> response = new PageResponse<>();
        response.setTotal(page.getTotal());
        response.setPageNum((int) page.getCurrent());
        response.setPageSize((int) page.getSize());
        response.setPages((int) page.getPages());
        response.setList(page.getRecords());
        return response;
    }

    /**
     * 将 IPage 转换为 PageResponse，并对列表进行转换
     */
    public static <S, T> PageResponse<T> toPageResponse(IPage<S> page, Function<S, T> converter) {
        PageResponse<T> response = new PageResponse<>();
        response.setTotal(page.getTotal());
        response.setPageNum((int) page.getCurrent());
        response.setPageSize((int) page.getSize());
        response.setPages((int) page.getPages());

        List<T> convertedList = page.getRecords().stream()
                .map(converter)
                .collect(Collectors.toList());
        response.setList(convertedList);

        return response;
    }
}



package com.sky.service;

import com.sky.dto.DataOverViewQueryDTO;
import com.sky.vo.TurnoverReportVO;

import java.time.LocalDate;

/**
 * @author yao
 * @version 1.0
 * @date 2024/10/17 - 14:33
 * @className ReportService
 * @since 1.0
 */
public interface ReportService {
    /**
     * 统计指定时间区间营业额数据
     * @param begin
     * @param end
     * @return
     */
    TurnoverReportVO getTurnoverStatistics(LocalDate begin, LocalDate end);
}

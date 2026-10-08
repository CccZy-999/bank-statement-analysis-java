package com.example.bankanalysis.chart;

import com.example.bankanalysis.model.CounterpartyStat;
import com.example.bankanalysis.model.FlowCategoryStat;
import com.example.bankanalysis.model.MonthlyCashFlow;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;

@Component
public class ChartGenerator {

    private static final int W = 800;
    private static final int H = 480;

    public BufferedImage monthlyTrendChart(List<MonthlyCashFlow> data) {
        DefaultCategoryDataset ds = new DefaultCategoryDataset();
        for (MonthlyCashFlow m : data) {
            ds.addValue(m.getInflow(), "进账", m.getMonth());
            ds.addValue(m.getOutflow(), "出账", m.getMonth());
        }
        JFreeChart chart = ChartFactory.createLineChart(
                "月度现金流趋势", "月份", "金额",
                ds, PlotOrientation.VERTICAL, true, false, false);
        style(chart);
        return chart.createBufferedImage(W, H);
    }

    public BufferedImage topCounterpartyChart(List<CounterpartyStat> data, String title) {
        DefaultCategoryDataset ds = new DefaultCategoryDataset();
        int limit = Math.min(10, data.size());
        for (int i = limit - 1; i >= 0; i--) {  // 反序让最大值在顶部
            CounterpartyStat s = data.get(i);
            ds.addValue(s.getInflow().signum() > 0 ? s.getInflow() : s.getOutflow(),
                    "金额", shorten(s.getName()));
        }
        JFreeChart chart = ChartFactory.createBarChart(
                title, "交易对手方", "金额",
                ds, PlotOrientation.HORIZONTAL, false, false, false);
        style(chart);
        return chart.createBufferedImage(W, H);
    }

    public BufferedImage compositionChart(List<FlowCategoryStat> data, String title) {
        DefaultPieDataset<String> ds = new DefaultPieDataset<>();
        for (FlowCategoryStat s : data) {
            if (s.getAmount().signum() > 0) ds.setValue(s.getCategory(), s.getAmount());
        }
        JFreeChart chart = ChartFactory.createPieChart(title, ds, true, false, false);
        return chart.createBufferedImage(W, H);
    }

    private void style(JFreeChart chart) {
        chart.setBackgroundPaint(Color.WHITE);
        if (chart.getPlot() instanceof CategoryPlot plot) {
            plot.setBackgroundPaint(Color.WHITE);
            plot.setRangeGridlinePaint(Color.LIGHT_GRAY);
            plot.setOutlineVisible(false);
            plot.getDomainAxis().setCategoryLabelPositions(CategoryLabelPositions.UP_45);
        }
        chart.getTitle().setFont(new Font("SansSerif", Font.BOLD, 16));
    }

    private String shorten(String s) {
        if (s == null) return "";
        return s.length() > 14 ? s.substring(0, 12) + "…" : s;
    }
}

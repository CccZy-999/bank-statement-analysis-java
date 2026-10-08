# Bank Statement Analysis (Java)

[weiwill88/bank-statement-analysis](https://github.com/weiwill88/bank-statement-analysis) 的 Java 重写版本。

## 功能

- 解析银行流水（Excel / CSV）
- 数据标准化与清洗
- 数据质量校验：余额连续性、Benford 定律检验
- 账户余额分析、借贷分析、现金流趋势分析
- 交易对手方分析（进账来源、出账对端）
- 流入 / 流出构成分析
- 生成 Word 分析报告（含图表、表格、文字）
- 生成 Markdown 分析报告

## 技术栈

- Java 17 + Spring Boot 3
- Apache POI / EasyExcel（Excel 解析）
- poi-tl（Word 报告生成）
- JFreeChart（图表）
- commons-math3（统计检验）

## 快速开始

### 1. 准备 Word 模板

由于版权与二进制文件不便直接放入代码仓库，请自行创建一个 Word 模板文件
`src/main/resources/templates/report-template.docx`，并按下方"模板占位符说明"填入 `{{placeholder}}`。

### 2. 编译与运行

```bash
mvn clean package
java -jar target/bank-statement-analysis-java-1.0.0.jar \
    --input=/path/to/abc-bank-flow.xlsx \
    --bank=ABC \
    --type=CORPORATE \
    --company="示例科技有限公司" \
    --output=./output/report.docx
```

### 3. 参数说明

| 参数 | 说明 | 可选值 |
|---|---|---|
| `--input` | 流水文件路径 | 必填 |
| `--bank` | 银行代码 | `ABC` |
| `--type` | 账户类型 | `CORPORATE` / `PERSONAL` |
| `--company` | 企业/个人名称 | 必填 |
| `--output` | 输出文件路径 | 默认 `./output/report.docx` |
| `--format` | 输出格式 | `WORD` / `MARKDOWN` |

## 模板占位符说明

Word 模板 `report-template.docx` 中可使用以下占位符：

### 文本
- `{{companyName}}` — 企业名称
- `{{accountNo}}` — 账号
- `{{reportDate}}` — 报告日期
- `{{totalInflow}}` — 总进账金额
- `{{totalOutflow}}` — 总出账金额
- `{{netFlow}}` — 净现金流
- `{{startDate}}` / `{{endDate}}` — 数据区间
- `{{balanceCheckResult}}` — 余额连续性检查结果

### 图片
- `{{monthlyTrendChart}}` — 月度现金流趋势折线图
- `{{inflowCompositionChart}}` — 流入构成饼图
- `{{outflowCompositionChart}}` — 流出构成饼图
- `{{topCounterpartyChart}}` — 主要交易对手方柱状图

### 表格
- `{{topInflowCounterparties}}` — 进账 TOP 对手方明细表
- `{{topOutflowCounterparties}}` — 出账 TOP 对手方明细表

## 银行格式扩展

新增银行格式步骤：

1. 在 `src/main/resources/bank-formats/` 下新增 `<bank>-<type>.yml`，定义列映射。
2. 实现 `BankStatementParser` 接口。
3. 在 `ParserFactory` 中注册。

## License

Apache-2.0

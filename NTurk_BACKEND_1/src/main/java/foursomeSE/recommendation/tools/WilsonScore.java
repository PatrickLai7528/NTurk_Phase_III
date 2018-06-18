package foursomeSE.recommendation.tools;

public class WilsonScore {
    /**
     * 威尔逊得分计算函数
     * 参考：https://en.wikipedia.org/wiki/Binomial_proportion_confidence_interval
     *
     * @param pos:   正例数
     * @param total: 总数
     * @return 威尔逊得分
     * <p>
     * 公式:
     * n = u + v
     * p = u / n
     * S = [p + Za^2/(2n) - (Za/(2n)) * sqrt(4n(1-p)p + Za^2)] / (1 + Za^2/n)
     */
    public static double getScore(int pos, int total) {
        if (total == 0) {
            return 0.0;
        }

        // 正态分布分位数，取95%置信度，z_a = 1.96
        double Za = 1.96;
        double n = (double) total;
        double p = pos / n;

        // 分子and分母
        double numerator = p + square(Za) / (2 * n) - (Za / (2 * n)) * Math.sqrt(4 * n * (1 - p) * p + square(Za));
        double denominator = 1 + square(Za) / n;
        double score = numerator / denominator;
        return score;
    }

    private static double square(double n) {
        return n * n;
    }
}

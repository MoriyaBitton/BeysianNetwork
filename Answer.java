import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;

/**
 * this class print the three answer each algorithm need to return:
 * 1. the answer
 * 2. number of "+"
 * 3. number of "*"
 */
public class Answer {

    private double ans;
    private int numOfSums;
    private int numOfMult;

    public Answer(double ans, int numOfSums, int numOfMult) {
        this.ans = ans;
        this.numOfSums = numOfSums;
        this.numOfMult = numOfMult;
        this.ans = roundToFive(this.ans);
    }

    /**
     * round final probability
     *
     * @param ans
     * @return
     */
    private double roundToFive(double ans) {
        BigDecimal bigDecimal = new BigDecimal(Double.toString(ans));
        bigDecimal = bigDecimal.setScale(5, RoundingMode.HALF_UP);
        ans = bigDecimal.doubleValue();
        return ans;
    }

    @Override
    /**
     * String final answer
     */
    public String toString() {
        String ansText = Double.toString(Math.abs(ans));
        int indexOf = ansText.indexOf('.');
        int decimalPlaces = ansText.length() - indexOf - 1;
        if (decimalPlaces < 5) {
            DecimalFormat formatter = new DecimalFormat("0.00000");
            String string_ans = formatter.format(ans);
            return string_ans + "," + numOfSums + "," + numOfMult;
        }
        return ans + "," + numOfSums + "," + numOfMult;
    }
}

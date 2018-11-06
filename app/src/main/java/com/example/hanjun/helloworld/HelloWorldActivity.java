package com.example.hanjun.helloworld;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.math.BigDecimal;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HelloWorldActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hello_world);


        //找到“计算”按钮
        Button button = (Button) findViewById(R.id.caculate);


        button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                EditText orgin = (EditText) findViewById(R.id.orginPrice);
                EditText degrees =  (EditText) findViewById(R.id.degrees);
                //打印位置
                TextView textView = (TextView) findViewById(R.id.textView5);
                Result result = new Result();
                result.setResult(true);
                validateParams(orgin,degrees,result);
                if(!result.isResult()){
                    textView.setText(result.getData().toString());
                    return;
                }

                String orginPrice = orgin.getText().toString().trim();
                String degreesNum = degrees.getText().toString().trim();
                String[] split = degreesNum.split(",");

                textView.setText("");
                printIncreaseByDegrees(orginPrice,4,3,split,textView);
            }
        });
    }

    /**
     * 验证参数
     * @param orginPrice
     * @param split
     * @param result
     */
    private void validateParams(EditText orgin, EditText degrees , Result result) {
        if(orgin.getText()==null || "".equals(orgin.getText().toString().trim()) ){
            result.setResult(false);
            result.setData("参数错误：第一年的价格必须填写");
            return;
        }
        if(!isPriceNumber(orgin.getText().toString().trim())){
            result.setResult(false);
            result.setData("参数错误：第一年的价格格式有问题，小数点后2位的数字");
            return;
        }


        /*if(orgin.getText().toString().trim().length()>9){
            result.setResult(false);
            result.setData("参数错误：第一年的价格最多9位数");
            return;
        }*/
       /* if(degrees.getText()==null || "".equals(degrees.getText().toString().trim()) ){
            result.setResult(false);
            result.setData("每年递增必须填写，如果没有就写0");
            return;
        }*/
       if(degrees.getText()==null || "".equals(degrees.getText().toString().trim())) return;
        String degreesNum = degrees.getText().toString().trim();
        String regEx = "^(\\d+[,])*(\\d+)$";
        Pattern pattern = Pattern.compile(regEx);
        Matcher matcher = pattern.matcher(degreesNum);
        boolean rs = matcher.matches();
        if(!rs){
            result.setResult(false);
            result.setData("参数错误：每年递增的格式必须为例如：5,10,15");
            return;
        }


    }


    //金额验证
    public static boolean isPriceNumber(String str){
        Pattern pattern=Pattern.compile("^(([1-9]{1}\\d*)|([0]{1}))(\\.(\\d){0,2})?$"); // 判断小数点后2位的数字的正则表达式
        Matcher match=pattern.matcher(str);
        if(match.matches()==false){
            return false;
        }else{
            return true;
        }
    }

    /**
     * 根据第一年的价格，和每年递增的价格计算每年的收入以及总收入
     *
     * @param origin   第一年价格
     * @param modeNum  舍入方式
     * @param newScale 精度位数 4：四舍五入
     * @param degrees  递增百分比
     */
    public void printIncreaseByDegrees(String origin, int modeNum, int newScale, String[] degrees,TextView textView) {
        BigDecimal sum = BigDecimal.ZERO;
        int mode = getMode(modeNum);
        BigDecimal tempOrigin = new BigDecimal(origin).setScale(newScale, mode);
        sum = sum.add(tempOrigin);
        //System.out.println("第1年：" + tempOrigin + "--->总共累计：" + sum);
        textView.append("第1年：" + tempOrigin + "--->总共累计：" + sum);
        textView.append("\n");
        if (degrees==null || degrees.length < 1) return;
        if(degrees.length==1 && "".equals(degrees[0])) return;
        for (int i = 0; i < degrees.length; i++) {
            BigDecimal tempDegree = new BigDecimal(degrees[i]);
            tempOrigin = tempOrigin.add(tempOrigin.multiply(tempDegree).divide(new BigDecimal(100)).setScale(newScale, mode)).setScale(newScale, mode);
            sum = sum.add(tempOrigin);
          //  System.out.println("第" + (i + 2) + "年：" + tempOrigin + "--->总共累计：" + sum);
            textView.append("第" + (i + 2) + "年：" + tempOrigin + "--->总共累计：" + sum);
            textView.append("\n");
        }
    }

    public int getMode(int modeNum) {
        switch (modeNum) {
            case 0:
                //（常量字段值0）远离零的舍入模式（向上舍入）。舍弃某部分时，若舍弃部分非零则对其前面的数字加1（此舍入模式始终不会减少计算值的大小）
                return BigDecimal.ROUND_UP;
            case 1:
                //（常量字段值1）接近零的舍入模式（向下舍入）。直接丢弃需舍弃部分（此舍入模式始终不会增加计算值的大小）
                return BigDecimal.ROUND_DOWN;
            case 2:
                //（常量字段值2）接近正无穷大的舍入模式。若BigDecimal为正，则舍入行为同ROUND_UP；若为负，则舍入行为同ROUND_DOWN（此舍入模式始终不会减少计算值大小）
                return BigDecimal.ROUND_CEILING;
            case 3:
                //（常量字段值3）接近负无穷大（不是无穷小哦）的舍入模式。其行为与ROUND_CEILING相反，若BigDecimal为负，则舍入行为同ROUND_UP；若为正，则舍入行为同ROUND_DOWN（此舍入模式始终不会增加计算值大小）
                return BigDecimal.ROUND_FLOOR;
            case 4:
                //（常量字段值4）向“最接近的”数字舍入，如果与两个相邻数字的距离相等，则为向上舍入的舍入模式（四舍五入，即舍弃部分>=0.5则向上舍入，否则向下舍入）
                return BigDecimal.ROUND_HALF_UP;
            case 5:
                //（常量字段值5）向“最接近的”数字舍入，如果与两个相邻数字的距离相等，则为向下舍入的舍入模式（舍弃部分<=0.5则向下舍入，否则向上舍入）
                return BigDecimal.ROUND_HALF_DOWN;
            case 6:
                //（常量字段值6）向“最接近的”数字舍入，如果与两个相邻数字的距离相等，则向相邻的偶数舍入（在重复进行一系列计算时，此舍入模式可以将累加错误减到最小）
                return BigDecimal.ROUND_HALF_EVEN;
            case 7:
                //（常量字段值7）断言请求的操作具有精确的结枚，因此不需要舍入，若该操作无精确结果（如1/3）则抛出 ArithmeticException
                return BigDecimal.ROUND_UNNECESSARY;
            default:
                return BigDecimal.ROUND_HALF_UP;

        }
    }
}

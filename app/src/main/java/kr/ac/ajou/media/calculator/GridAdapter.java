package kr.ac.ajou.media.calculator;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.math.BigDecimal;

/**
 * Created by kcassia on 2016-05-10.
 */
public class GridAdapter extends BaseAdapter
{
    // 계산기 버튼
    private static final String[] items =
            {"MC", "MR", "M+", "M-",
            "AC", "C", "√", "/",
            "7", "8", "9", "X",
            "4", "5", "6", "-",
            "1", "2", "3", "+",
            "+/-", "0", ".", "="};
    private Context context;
    private TextView process, result; // 계산 과정, 출력 된 값
    private BigDecimal memory, temp; // M, 현재까지의 계산 값
    private boolean clearFlag, rootFlag, operatorFlag; // 화면이 초기화되어야 할 조건, root가 선택된 조건, 연산자가 선택된 조건
    private int rootSize; // root 안에 들어갈 수의 자릿수
    private String lastOperator; // 전에 기록 된 계산되어야 할 연산자

    // 객체 초기 생성시 실행되는 생성자
    public GridAdapter(Context context)
    {
        this.context = context;
        process = (TextView)((MainActivity)context).findViewById(R.id.process);
        result = (TextView)((MainActivity)context).findViewById(R.id.result);
        result.setText("0");
        memory = temp = BigDecimal.ZERO;
        clearFlag = rootFlag = operatorFlag = false;
        rootSize = 1;
        lastOperator = "=";
    }

    // 데이터 복구시 실행되는 생성자
    public GridAdapter(Context context, String process, String result, double memory, double temp,
                       boolean clearFlag, boolean rootFlag, boolean operatorFlag, int rootSize, String lastOperator)
    {
        this.context = context;
        this.process = (TextView)((MainActivity)context).findViewById(R.id.process);
        this.result = (TextView)((MainActivity)context).findViewById(R.id.result);

        this.process.setText(process);
        this.result.setText(result);
        this.memory = BigDecimal.valueOf(memory);
        this.temp = BigDecimal.valueOf(temp);
        this.clearFlag = clearFlag;
        this.rootFlag = rootFlag;
        this.operatorFlag = operatorFlag;
        this.rootSize = rootSize;
        this.lastOperator = lastOperator;
    }
    @Override
    public int getCount(){return items.length;}
    @Override
    public Object getItem(int position){return items[position];}
    @Override
    public long getItemId(int position){return position;}

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        if(convertView == null)
        {
            LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = inflater.inflate(R.layout.item_grid, null);
            final Button button = (Button)view.findViewById(R.id.item);
            button.setText(items[position]);
            button.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v)
                {
                    String buttonText = button.getText().toString();
                    String processStr = process.getText().toString();
                    String resultStr = result.getText().toString();
                    switch(buttonText)
                    {
                        case "MC": // Memory Clear
                            memory = BigDecimal.ZERO;
                            break;
                        case "MR": // Memory Read
                            printResult(memory);
                            break;
                        case "M+": // Memory Add
                            memory = memory.add(BigDecimal.valueOf(Double.parseDouble(resultStr)));
                            break;
                        case "M-": // Memory Substract
                            memory = memory.subtract(BigDecimal.valueOf(Double.parseDouble(resultStr)));
                            break;
                        case "AC": // All Clear
                            process.setText("");
                            result.setText("0");
                            temp = BigDecimal.ZERO;
                            break;
                        case "C": // Clear
                            result.setText("0");
                            if(rootFlag)
                            {
                                process.setText(processStr.substring(0, processStr.length() - rootSize));
                                while(process.getText().toString().charAt(process.getText().length() - 1) == '√')
                                    process.setText(process.getText().toString().substring(0, process.getText().toString().length() - 1));
                                rootFlag = false;
                            }
                            break;
                        case "√": // Root
                            try
                            {
                                if(rootFlag)
                                    process.setText(processStr.substring(0, processStr.length() - rootSize)
                                            + "√" + processStr.substring(processStr.length() - rootSize));
                                else
                                {
                                    rootSize = resultStr.length();
                                    process.setText(processStr + " √" + resultStr);
                                }
                                BigDecimal root = BigDecimal.valueOf(Math.sqrt(Double.parseDouble(resultStr)))
                                        .setScale(7, BigDecimal.ROUND_HALF_UP);
                                printResult(root);
                            }
                            catch(NumberFormatException e)
                            {
                                process.setText("");
                                result.setText("Error");
                                temp = BigDecimal.ZERO;
                                lastOperator = "=";
                            }
                            break;
                        case "/": case "X": case "-": case "+":
                            if(rootFlag)
                                process.setText(processStr + " " + buttonText + " ");
                            else
                                process.setText(processStr + " " + resultStr + " " + buttonText + " ");
                            if(operatorFlag)
                                process.setText(processStr.substring(0, processStr.length()-2) + buttonText + " ");
                            else
                                calculate(lastOperator, BigDecimal.valueOf(Double.parseDouble(resultStr)));
                            printResult(temp);
                            lastOperator = buttonText;
                            break;
                        case "+/-": // Change Sign
                            if(Double.parseDouble(resultStr) > 0)
                                result.setText("-" + resultStr);
                            else if(Double.parseDouble(resultStr) < 0)
                                result.setText(resultStr.substring(1));
                            break;
                        case ".": // Period
                            if(!resultStr.contains("."))
                                result.setText(resultStr + ".");
                            break;
                        case "=":
                            calculate(lastOperator, BigDecimal.valueOf(Double.parseDouble(resultStr)));
                            printResult(temp);
                            temp = BigDecimal.ZERO;
                            process.setText("");
                            lastOperator = buttonText;
                            break;
                        default: // Digit
                            if(resultStr.equals("0") || clearFlag)
                            {
                                result.setText(buttonText);
                                if(rootFlag)
                                {
                                    process.setText(processStr.substring(0, processStr.length() - rootSize));
                                    while(process.getText().toString().charAt(process.getText().length() - 1) == '√')
                                        process.setText(process.getText().toString().substring(0, process.getText().toString().length() - 1));
                                    rootFlag = false;
                                }
                            }
                            else
                                result.setText(resultStr + buttonText);
                    }
                    if(!buttonText.equals("C") && !buttonText.contains("M"))
                    {
                        rootFlag = buttonText.equals("√");
                        operatorFlag = buttonText.equals("+") || buttonText.equals("-") || buttonText.equals("X") || buttonText.equals("/");
                    }
                    clearFlag = buttonText.contains("M") || buttonText.equals("=") || rootFlag || operatorFlag;
                }
            });

            return view;
        }
        else
        {
            final Button button = (Button)convertView.findViewById(R.id.item);
            button.setText(items[position]);
            return convertView;
        }
    }

    // 현재까지의 누적 계산
    private void calculate(String lastOperator, BigDecimal operand)
    {
        switch(lastOperator)
        {
            case "/":
                try{temp = temp.divide(operand, 7, BigDecimal.ROUND_HALF_UP);}
                catch(ArithmeticException e)
                {
                    process.setText("");
                    result.setText("Error");
                    temp = BigDecimal.ZERO;
                    this.lastOperator = "=";
                }
                break;
            case "X":
                temp = temp.multiply(operand);
                break;
            case "+":
                temp = temp.add(operand);
                break;
            case "-":
                temp = temp.subtract(operand);
                break;
            default :
                temp = operand.setScale(7, BigDecimal.ROUND_HALF_UP);
        }
    }

    // 소수점의 값이 있는 경우 소수점까지 정수인 경우 소수점 자르고 정수만 출력
    private void printResult(BigDecimal value)
    {
        if(value.doubleValue() == value.intValue()) // 소수점의 값이 없는 경우
            result.setText(String.valueOf(value.intValue()));
        else if(new BigDecimal(String.valueOf(value.doubleValue())).scale() <= 6) // 소수점 6자리 이하인 경우
            result.setText(String.valueOf(value.doubleValue()));
        else // 소수점 7자리 이상인 경우
            result.setText(String.format("%E", value.doubleValue()));
    }

    public TextView getProcess(){return process;}
    public TextView getResult(){return result;}
    public BigDecimal getMemory(){return memory;}
    public BigDecimal getTemp(){return temp;}
    public boolean isClearFlag(){return clearFlag;}
    public boolean isRootFlag(){return rootFlag;}
    public boolean isOperatorFlag(){return operatorFlag;}
    public int getRootSize(){return rootSize;}
    public String getLastOperator(){return lastOperator;}
}

package a3.debianpulse.injen.applivr.ProtoOERP;

import java.util.ArrayList;
import java.util.StringTokenizer;

/**
 * Created by Guillaume on 03/12/2014.
 */
public class AnswerOERP {
    public final static int LOGIN = 1;
    public final static int GETDELIVERY = 2;
    public final static int CHOOSEDELIV = 3;
    public final static int CLOSE = 4;
    public final static int BREAKDOWN = 5;
    public final static int REPAIRS = 6;

    private int Code;
    private ArrayList<String> Charge;

    public AnswerOERP(String data) {
        StringTokenizer st = new StringTokenizer(data,"$");
        Code = Integer.parseInt(st.nextToken());
        Charge = new ArrayList<>();
        while(st.hasMoreElements())
            Charge.add(st.nextToken());
        st = new StringTokenizer(Charge.toString(), ",");
        Charge.clear();
        while (st.hasMoreElements())
            Charge.add(st.nextToken());
    }

    public AnswerOERP(int Code, boolean answer) {
        this.Code = Code;
        Charge = new ArrayList<>();
        Charge.add(Boolean.toString(answer));
    }

    public AnswerOERP(int Code, boolean answer, String data) {
        this.Code = Code;
        Charge = new ArrayList<>();
        Charge.add(Boolean.toString(answer));
        Charge.add(data);
    }

    public int getCode() {
        return Code;
    }

    public void setCode(int Code) {
        this.Code = Code;
    }

    public ArrayList<String> getCharge() {
        return Charge;
    }

    public void setCharge(ArrayList<String> Charge) {
        this.Charge = Charge;
    }

    public void setAnswer(boolean b) {
        Charge.set(0,Boolean.toString(b));
    }

    public boolean getAnswer() {
        return Charge.get(0).contains("[true");
    }
}

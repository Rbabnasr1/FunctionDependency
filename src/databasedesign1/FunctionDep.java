package databasedesign1;

import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class FunctionDep {

    public Connection con() throws ClassNotFoundException, SQLException {
        Connection connection = null;
        if (connection != null) {
            return connection;
        } else {

            try { 

                String url = "jdbc:mysql://localhost:3306/ass1";
                Class.forName("com.mysql.jdbc.Driver");
                connection = DriverManager.getConnection(url, "root", "root");

                return connection;

            } catch (Exception e) {
                System.out.println("erorr in connection");

            }

        }
        return connection;

    }

    public void ShowData(ArrayList a) {

        for (int i = 0; i < a.size(); i++) {
            System.out.println(a.get(i));

        }

    }
      public void ShowAll(ArrayList a, ArrayList b) {

        for (int i = 0; i < a.size(); i++) {
            System.out.print(a.get(i));
            System.out.print("  pk :   "+b.get(i));
            System.out.println("");
        }
          System.out.println("");

    }

    public void ShowRelation(ArrayList a) {
        System.out.print("R0(");
        for (int i = 0; i < a.size(); i++) {
            System.out.print(a.get(i));
            
        }
        System.out.println(")");

    }

    public String dependency(char Att1, char Att2) throws ClassNotFoundException, SQLException {
        String ResultDepAtt1 = " ";
        ArrayList<String> att1Data = new ArrayList<>();
        ArrayList<String> att2Data = new ArrayList<>();
        Statement st = con().createStatement();
        String sql = "select " + Att1 + " , " + Att2 + " from r";
        ResultSet rs = st.executeQuery(sql);
        while (rs.next()) {
            if (Att1 == 'A' || Att1 == 'D') {
                att1Data.add(String.valueOf(rs.getInt(String.valueOf(Att1))));
            } else {
                att1Data.add(rs.getString(String.valueOf(Att1)));
            }
            if (Att2 == 'A' || Att2 == 'D') {
                att2Data.add(String.valueOf(rs.getInt(String.valueOf(Att2))));
            } else {
                att2Data.add(rs.getString(String.valueOf(Att2)));
            }

        }
//////////////////////////////////////////////// <> dependancy  Distict Value
        for (int i = 0; i < att1Data.size(); i++) {
            for (int j = 0; j < att2Data.size(); j++) {
                if (att1Data.get(i).equals(att1Data.get(j))) {
                    if (!att2Data.get(i).equals(att2Data.get(j))) {
                        ResultDepAtt1 += "!" + Att1 + "-->" + Att2;
                        break;
                    }
                }
            }

            if (!ResultDepAtt1.equals(" ")) {/////////////////3shan msh append 
                break;
            }
        }
/////////////////////////////////////////////////////////////////
        ////////////////////////////////////////////  dependancy 
        if (ResultDepAtt1.equals(" ")) {
            ResultDepAtt1 += Att1 + "-->" + Att2;
        }
        return ResultDepAtt1;
    }

    public String Result(char Att1, char Att2) throws ClassNotFoundException, SQLException {

        return dependency(Att1, Att2) + dependency(Att2, Att1);

    }

    public ArrayList Remove(ArrayList<Character> a, char b) throws ClassNotFoundException, SQLException {

        for (int i = 0; i < a.size(); i++) {
            if (b == a.get(i)) {
                a.remove(i);
            }

        }
        return a;

    }

    public ArrayList key() throws FileNotFoundException, IOException, ClassNotFoundException, SQLException {

        RandomAccessFile file = new RandomAccessFile("t5.txt", "rw");
        String candidate = "", closure = "";
        ArrayList<String> CandiditeKeys = new ArrayList<>();

        ArrayList<Character> remainders = new ArrayList<>();
        ArrayList<Character> DistinctValue = new ArrayList<>();
        String Record = file.readLine();
        String RecordsFile = Record;
        String FunctionDep[] = null;

        while (Record != null) {///////// More than Record
            Record = file.readLine();
            if (Record != null) {
                RecordsFile += Record;
            }
        }
        FunctionDep = RecordsFile.split(",");////////////////////////////DistinctValue
        for (int i = 0; i < FunctionDep.length; i++) {
            for (int j = 0; j < FunctionDep[i].length(); j++) {
                if (!DistinctValue.contains(FunctionDep[i].charAt(j))) {
                    if (FunctionDep[i].charAt(j) != '>') {
                        DistinctValue.add(FunctionDep[i].charAt(j));
//                    
                    }

                }
            }
        }

        /////////////////////////////////////////////////////// Store RHS 
        ArrayList<Character> RHS = new ArrayList<>();

        for (int i = 0; i < FunctionDep.length; i++) {
            String split[] = FunctionDep[i].split(">");
            for (int j = 0; j < split[1].length(); j++) {
                RHS.add(split[1].charAt(j));
            }
        }

        ///////////////////////////////////////////////////////////////////////////////////////
        boolean change = true;
        int index = 0;
        int UseRemainder = 0;
        for (int i = 0; i < FunctionDep.length; i++) {

            remainders = new ArrayList<>();
            for (int j = 0; j < DistinctValue.size(); j++) {
                remainders.add(DistinctValue.get(j));

            }

            //////////////////////////////////first function
            if (candidate == "") {
                for (int j = 0; j < FunctionDep[i].length(); j++) {
                    char characterOfFun = FunctionDep[i].charAt(j);

                    if (characterOfFun == '>') {
                        index = 1;
                    } else {
                        if (index == 0) {
                            candidate += characterOfFun;
                            closure += characterOfFun;
                            remainders = Remove(remainders, characterOfFun);

                        } else {
                            closure += characterOfFun;
                            remainders = Remove(remainders, characterOfFun);

                        }
                    }
                }
            }

/////////////////////////////////////////////////////// in not RHS and not in candidta
            for (int k = 0; k < FunctionDep.length; k++) {

                String split[] = FunctionDep[k].split(">");

                for (int j = 0; j < split[0].length(); j++) {

                    char charOfLeft = split[0].charAt(j);
                    if (!closure.contains(String.valueOf(charOfLeft)) && !RHS.contains(charOfLeft)) {
                        closure += charOfLeft;
                        candidate += charOfLeft;

                        remainders = Remove(remainders, charOfLeft);

                    }
                }
            }

            change = true;
            while (change) {
                change = false;
                for (int k = 0; k < FunctionDep.length; k++) {
                    String split[] = FunctionDep[k].split(">");
                    String charOfLeft = split[0];
                    String charOfRight = split[1];
                    int Exist = 1;
                    for (int j = 0; j < split[0].length(); j++) {
                        if (!closure.contains(String.valueOf(charOfLeft.charAt(j)))) {
                            Exist = 0;
                            break;

                        }
                    }

                    if (Exist == 1 && !closure.contains(String.valueOf(charOfRight))) {
                        for (int j = 0; j < charOfRight.length(); j++) {

                        }
                        closure += charOfRight;
                        for (int j = 0; j < split[0].length(); j++) {
                            remainders = Remove(remainders, charOfLeft.charAt(j));
                        }
                        change = true;
                    }
                }
                if (closure.length() >= DistinctValue.size() && change == false) {
                    break;
                } else if (closure.length() < DistinctValue.size() && change == false) {
                    candidate += remainders.get(closure(remainders, candidate, closure, FunctionDep, DistinctValue));
                    closure += remainders.get(closure(remainders, candidate, closure, FunctionDep, DistinctValue));
                    remainders.remove(closure(remainders, candidate, closure, FunctionDep, DistinctValue));
                }

            }
///////////////////////////////////////////////////////////////////////////sort
            char[] chars = candidate.toCharArray();
            Arrays.sort(chars);
            String newText = new String(chars);
            if (!CandiditeKeys.contains(newText)) {
                CandiditeKeys.add(newText);
            }

            candidate = "";
            closure = "";
            index = 0;

        }///////////////////////////////////////primary key
        int primaryKey = 200;
        int indexOfPrimary = -1;
        for (int i = 0; i < CandiditeKeys.size(); i++) {
            if (primaryKey > CandiditeKeys.get(i).length()) {
                primaryKey = CandiditeKeys.get(i).length();
                indexOfPrimary = i;

            }

        }
        System.out.println("******************");
        System.out.println(" dependacy Function");
        System.out.println("********************");
        for (int i = 0; i < FunctionDep.length; i++) {
            System.out.println(FunctionDep[i]);

        }
        System.out.println("******************");
//        System.out.println("Distinct value : ");
//        System.out.println("******************");
//        ShowData(DistinctValue);
//        System.out.println("******************");
//        System.out.println("Candidate Key : ");
//        System.out.println("******************");
//        ShowData(CandiditeKeys);
//        System.out.println("******************");
//        System.out.println(" the primary key is : " + CandiditeKeys.get(indexOfPrimary));
//        System.out.println("******************");
        return CandiditeKeys;

    }

    public int closure(ArrayList<Character> remaind, String candidate, String closure, String[] Record, ArrayList distinctValue) throws ClassNotFoundException, SQLException {
        boolean change = true;
        int[] candIndex = new int[remaind.size()];
        int index = 0;

        for (int i = 0; i < remaind.size(); i++) {
            String newCand = "";
            newCand = candidate;
            String newClosure = "";
            newClosure = closure;
            newCand += remaind.get(i);
            newClosure += remaind.get(i);
            change = true;
            while (change) {
                change = false;

                for (int k = 0; k < Record.length; k++) {
//                    System.out.println("FN :  " + Record[k]);
                    String split[] = Record[k].split(">");
                    String charOfLeft = split[0];
                    String charOfRight = split[1];
                    int Exist = 1;
                    for (int j = 0; j < split[0].length(); j++) {
                        if (!newClosure.contains(String.valueOf(charOfLeft.charAt(j)))) {
                            Exist = 0;
//                            System.out.println("msh mwgodaaa ");
                            break;

                        }
                    }

                    if (Exist == 1 && !newClosure.contains(String.valueOf(charOfRight))) {
//                        System.out.println("mwgodaa ");
                        for (int j = 0; j < charOfRight.length(); j++) {
                            if (!newClosure.contains(String.valueOf(charOfRight.charAt(j)))) {
                                newClosure += charOfRight.charAt(j);
                            }
                        }
//                        System.out.println("distinct len " + distinctValue.size() + "  y len " + newClosure.length());
                        if (newClosure.length() >= distinctValue.size()) {
//                            System.out.println("a5bark eh ya len ");
                            change = false;
                            break;
                        }

                        change = true;
//                        System.out.println("hello ");
//                        System.out.println("c" + c);
//                        System.out.println("new clo " + newClosure);
                    }
                }

            }
            candIndex[i] = newClosure.length();

        }
        int max = -1;

        for (int i = 0; i < candIndex.length; i++) {
            if (max > candIndex[i]) {
                max = candIndex[i];
                index = i;

            }

        }
        return index;

    }

    public void MinCover() throws FileNotFoundException, IOException {

        RandomAccessFile file = new RandomAccessFile("t5.txt", "rw");
        ArrayList<String> LHS = new ArrayList<>();
        ArrayList<String> RHS = new ArrayList<>();
        ArrayList<String> RightR = new ArrayList<>();
        String Records = file.readLine();
        String dependancyFunction[] = Records.split(",");
        String ch[] = null;
        //////////////////////////////////////////////////////////////////////////////////split RHS,LFS
        for (int i = 0; i < dependancyFunction.length; i++) {
            ch = dependancyFunction[i].split(">");
            LHS.add(ch[0]);
            RHS.add(ch[1]);
        }
        /////////////////////////////////////////////////////////////////////////////////////////// Right Red 1
        for (int i = 0; i < RHS.size(); i++) {
            if (RHS.get(i).length() > 1) {
                for (int j = 0; j < RHS.get(i).length(); j++) {
                    RightR.add(LHS.get(i) + ">" + RHS.get(i).charAt(j));
                }
            } else {
                RightR.add(LHS.get(i) + ">" + RHS.get(i));
            }
        }
//////////////////////////////////////////////////////////////////////////////  Cond 2
        LHS = new ArrayList<>();
        RHS = new ArrayList<>();
        for (int i = 0; i < RightR.size(); i++) {
            ch = RightR.get(i).split(">");
            LHS.add(ch[0]);
            RHS.add(ch[1]);
        }
        char leftRemoved;
        String left = "";
        String leftAppend = "";
        boolean change = true;
        boolean change2 = true;
        while (change2) {
            change2 = false;
            for (int i = 0; i < LHS.size(); i++) {
                if (LHS.get(i).length() > 1) {
                    for (int j = 0; j < LHS.get(i).length(); j++) {
                        leftRemoved = LHS.get(i).charAt(j);
                        leftAppend = LHS.get(i).substring(0, j);
                        leftAppend += LHS.get(i).substring(j + 1, LHS.get(i).length());
                        left = leftAppend;
                        change = true;
                        while (change) {
                            change = false;
                            for (int k = 0; k < LHS.size(); k++) {
                                int leftCorrectCondition = 1;
                                for (int l = 0; l < LHS.get(k).length(); l++) {
                                    if (!leftAppend.contains(String.valueOf(LHS.get(k).charAt(l)))) {//check more than char
                                        leftCorrectCondition = 0;
                                    }
                                }

                                if ((leftCorrectCondition == 1) && !leftAppend.contains(RHS.get(k))) {
                                    leftAppend += RHS.get(k);
                                    if (leftAppend.contains(RHS.get(i))) {
                                        change = false;
                                        change2 = true;
                                        LHS.set(i, left);
                                        break;
                                    } else {
                                        change = true;
                                    }
                                }
                            }
                        }
                    }

                }
            }
        }

        ////////////////////////////////////////////////////////////////////////////////////////non Red
        left = "";

        String right = "";
        leftAppend = "";
        change = true;
        change2 = true;

        int iteration = 0;
        while (iteration < LHS.size()) {

            right = RHS.get(iteration);
            leftAppend = LHS.get(iteration);
            left = leftAppend;

            change = true;
            while (change) {
                change = false;
                for (int k = 0; k < LHS.size(); k++) {
                    if (k != iteration) {
                        int iS = 1;
                        for (int l = 0; l < LHS.get(k).length(); l++) {
                            if (!leftAppend.contains(String.valueOf(LHS.get(k).charAt(l)))) {
                                iS = 0;
                            }
                        }

                        if ((iS == 1) && !leftAppend.contains(RHS.get(k))) {
                            leftAppend += RHS.get(k);
                            if (leftAppend.contains(right)) {
                                change = false;
                                LHS.remove(iteration);
                                RHS.remove(iteration);
                                iteration--;

                                break;
                            } else {
                                change = true;

                            }
                        }

                    }

                }

            }
            iteration++;

        }
        System.out.println("===================================================");
        System.out.println("minCover");
        System.out.println("===================================================");
        for (int i = 0; i < LHS.size(); i++) {

            System.out.println(" ** " + LHS.get(i) + " > " + RHS.get(i));

        }
    }

    public boolean Contain(ArrayList can, String lft) {

        char[] chars = lft.toCharArray();
        Arrays.sort(chars);
        String LEFT = new String(chars);

        for (int i = 0; i < can.size(); i++) {
            if (can.get(i).equals(LEFT)) {
                System.out.println("*******hi*******");
                ShowData(can);
                return true;

            }
        }
        return false;

    }
    
    
    public ArrayList NewCandidate(ArrayList <String> CandiditeKeys) {
        int LengthOfPrimary = 200;
        int indexOfPrimary = -1; 
        ArrayList<String> NewCandiditeKey = new ArrayList<>();
        for (int i = 0; i < CandiditeKeys.size(); i++) {
            if (LengthOfPrimary > CandiditeKeys.get(i).length()) {
                LengthOfPrimary = CandiditeKeys.get(i).length();
                indexOfPrimary = i;

            }

        }
        String PK = CandiditeKeys.get(indexOfPrimary);
        NewCandiditeKey.add(PK);
        //////////////////////////////////////////////////////////////////////////Review candidite after Removing
        for (int i = 0; i < CandiditeKeys.size(); i++) {
            int lenOfPK = 0;
            for (int j = 0; j < CandiditeKeys.get(i).length(); j++) {
                if (PK.contains(String.valueOf(CandiditeKeys.get(i).charAt(j)))) {
                    lenOfPK++;

                }
            }
            if (lenOfPK != PK.length()) {
                NewCandiditeKey.add(CandiditeKeys.get(i));

            }

        }
          return NewCandiditeKey;
        
    }



}

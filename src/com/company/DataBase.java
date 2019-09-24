package com.company;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

public class DataBase {

    public String host,user,password;
    public Connection connection;

    public DataBase(String host, String user, String password){
        this.host=host;
        this.user=user;
        this.password=password;
    }
    public void addRecord(String name, int score){
        try {
            String sql = String.format("INSERT INTO player(Name, Score) VALUE('%s',%d)", name, score);
            Statement st = connection.createStatement();
            st.executeUpdate(sql);
            st.close();
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
    public ArrayList<String> getRecords(){
        ArrayList<String> result=new ArrayList<String>();
        try{
            Statement st= connection.createStatement();
            ResultSet res = st.executeQuery("SELECT*FROM player");
            st.executeQuery("SELECT * FROM player");
            while(res.next()){
                int score = res.getInt(3);
                String name = res.getString(2);
                String date = res.getString(4);

                result.add(name+"at"+date+":"+ score);
            }
            res.close();
            st.close();
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return result;
    }

    public void init(){
        try{
            Class.forName("com.mysql.cj.jdbc.Driver").getDeclaredConstructor().newInstance();
            connection = DriverManager.getConnection(host, user, password);
            System.out.println("Connection to GameDrop DB succesfull!");

        }
        catch(Exception ex){
            System.out.println("Conn. failed");
            System.out.println(ex);
        }
    }
}

package app;

import dao.Schema;
import model.IdGenerator;

public class IdCheck {
    public static void main(String[] args) throws Exception {
        Schema.create();
        System.out.println(IdGenerator.next("USR"));
    }
}
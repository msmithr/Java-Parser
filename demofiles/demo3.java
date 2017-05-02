package demo2.java;

public class demo2 {

    public static void main(String[] args) {
        InnerClass object = new InnerClass(5);

        System.out.println(object.getC());

    } // end main

    private class InnerClass {
        private int c;

        public InnerClass(int c) {
            this.c = c;
        } // end constructor

        public int getC() {
            return c // ERROR HERE, NO SEMICOLON
        }

    } // end class

} // end class

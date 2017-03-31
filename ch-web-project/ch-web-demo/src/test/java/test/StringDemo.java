package test;

/**
 * Created by ludynice on 2017/1/27.
 */
public class StringDemo {
    public static void main(String[] args) {
        //String类常用的方法
        /*String con = new String(new StringBuffer("buffer"));
        String con2 = new String(new StringBuilder("builder"));
        String con3 = new String("temp");//创建副本*/
        System.out.println("abc".compareTo("bbc"));//-1
        System.out.println("bbc".compareTo("abc"));//1
        System.out.println("ab".concat("c"));//abc
        System.out.println("abc".contains("c"));//true
        System.out.println("abc".contains("bc"));//true
        System.out.println("abc.txt".endsWith(".txt"));//true
        System.out.println("abcdec".indexOf("c"));//2
        System.out.println("abcdec".indexOf("c",3));//5
        System.out.println("abcdec".indexOf("cd"));//2
        System.out.println("".isEmpty());//true
        //lastIndexOf()
        //"abc".length
        //"xxx".matches(String regex)



    }
}

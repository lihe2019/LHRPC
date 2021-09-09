public class Test {
    public static void main(String[] args) {
        double df = 522;
        double n = 48;
//        double theta = 3.1415926/3;
        double theta = 60;
        double wn = (df + (4*n - 3))/8*theta;
        double lambda = 39.8;
        double D = lambda * df / wn;
        System.out.println(D);
    }


}

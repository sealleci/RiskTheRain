public class Test {
    private static boolean is(F f){
        return f instanceof C;
    }
    public static void main(String[] args) {
            C c=new C();
            F f=new F();
            System.out.println(is(c));
    }
}

class F{
    public F(){

    }
}

class C extends F{
    public C(){

    }
}

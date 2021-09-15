public class Test {
    private static boolean is(F f){
        return f instanceof C;
    }
    public static void main(String[] args) {
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

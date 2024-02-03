package jdk.internal.misc;

public class OpenJDK11 {
//    public static jdk.internal.misc.Unsafe getUnsafeInstance() {
//        Unsafe unsafe = null;
//        try {
//            Class<?> uc     = jdk.internal.misc.Unsafe.class;
//            Field[]  fields = uc.getDeclaredFields();
//            for(int i = 0; i < fields.length; i++) {
//                if(fields[i].getName().equals("theUnsafe")) {
//                    fields[i].setAccessible(true);
//                    unsafe = (Unsafe) fields[i].get( uc);
//                    break;
//                }
//            }
//        } catch( Exception e ) {
//            throw new RuntimeException( "Unable to get access to Unsafe instance : " + e.getMessage(), e );
//        }
//
//        return unsafe;
//    }
//
//    public static Method getMemMapCleanerAccessorMethod() {
//        return bypassSecurityAndGetMethod( "java.nio.DirectByteBuffer", "cleaner" );
//    }
//
//    public static Method getMemMapFreeMethod() {
//        return bypassSecurityAndGetMethod( "jdk.internal.ref.Cleaner", "clean" );
//    }
//
//    public static Method getUnsafeCompareAndSwap( ) {
//
//        String className  = "jdk.internal.misc.Unsafe";
//        String methodName = "compareAndSetObject";
//
//        Method m;
//
//        try {
//            Class<?> c = Class.forName( className );
//            Class<?>[] param = { Object.class, long.class, Object.class, Object.class };
//            m = c.getMethod( methodName, param );
//            m.setAccessible( true );
//        } catch( Exception e ) {
//            throw new RuntimeException( "Unable to get method " + methodName + " in " + className + " via reflection : " + e.getMessage(), e );
//        }
//
//        return m;
//    }
//
//    private static Method bypassSecurityAndGetMethod( String className, String methodName ) {
//
//        Method m;
//
//        try {
//            Class<?> c = Class.forName( className );
//            Class<?>[] param = {};
//            m = c.getMethod( methodName, param );
//            m.setAccessible( true );
//        } catch( Exception e ) {
//            throw new RuntimeException( "Unable to get method " + methodName + " in " + className + " via reflection : " + e.getMessage(), e );
//        }
//
//        return m;
//    }
}

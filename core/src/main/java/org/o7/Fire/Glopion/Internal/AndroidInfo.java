package org.o7.Fire.Glopion.Internal;

import java.lang.reflect.Field;

public final class AndroidInfo {
    private AndroidInfo(){}
    
    private static Object fieldValue(String className, String fieldName){
        try{
            Class<?> type = Class.forName(className);
            Field field = type.getField(fieldName);
            return field.get(null);
        }catch(Throwable ignored){
            return null;
        }
    }
    
    public static int sdkInt(){
        Object value = fieldValue("android.os.Build$VERSION", "SDK_INT");
        return value instanceof Integer ? (Integer) value : 0;
    }
    
    public static String model(){
        Object value = fieldValue("android.os.Build", "MODEL");
        return value == null ? "unknown" : value.toString();
    }
}

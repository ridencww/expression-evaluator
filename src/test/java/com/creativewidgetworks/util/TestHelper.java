package com.creativewidgetworks.util;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class TestHelper {

    /**
     * Retrieves the value of a field (private/protected/public) in the referenced class
     * 
     * @param instanceOrStaticClass of object being called
     * @param field name being retrieved
     * @return The value of the field
     * @throws SecurityException, NoSuchMethodException, IllegalArgumentException, IllegalAccessException, InvocationTargetException
     */
    public static Object getMember(Object instanceOrStaticClass, String memberName) 
        throws SecurityException, IllegalArgumentException, IllegalAccessException {
        boolean isClass = (instanceOrStaticClass instanceof Class);

        Class clazz = isClass ? (Class) instanceOrStaticClass : instanceOrStaticClass.getClass();

        Object retVal = null;

        while (!Object.class.equals(clazz)) {
            Field field = null;
            try {
                field = clazz.getDeclaredField(memberName);
                boolean isAccessible = field.isAccessible();
                try {
                    field.setAccessible(true);
                    retVal = field.get(isClass ? null : instanceOrStaticClass);
                } finally {
                    field.setAccessible(isAccessible);
                }
            } catch (NoSuchFieldException ex) {
                /* Continue through the loop using this class's superclass */
            } finally {
                clazz = clazz.getSuperclass();
            }
        }

        return retVal;
    }    
    
    /*----------------------------------------------------------------------------*/
    
    /**
     * Calls a method (private/protected/public) in the referenced class
     * @param instance of object being called
     * @param method name being called
     * @param Class<?> an array of Classes that form the parameters
     * @param Object[] the array of parameter objects
     * @return The object returned by the method being called
     * @throws SecurityException, NoSuchMethodException, IllegalArgumentException, IllegalAccessException, InvocationTargetException
     */
   public static  <T> T callMethod(Object staticOrInstanceClass, String methodName, Class<?>[] classTypes, Object... parameters) 
       throws SecurityException, NoSuchMethodException, IllegalArgumentException, IllegalAccessException, InvocationTargetException {
       
       Object returnValue;
       
       Method method = null;
       
       if (staticOrInstanceClass instanceof Class) { 
           // Calling a static method
           method = ((Class)staticOrInstanceClass).getDeclaredMethod(methodName, classTypes);
           staticOrInstanceClass = null;
       } else {
           // Calling an instance method
           Class parentClass = staticOrInstanceClass.getClass();
           boolean found = false;
           while (!found) {
               try {
                   method = parentClass.getDeclaredMethod(methodName, classTypes);
                   found = true;
               } catch (NoSuchMethodException ex) {
                   parentClass = parentClass.getSuperclass();
                   if (parentClass == null) {
                       throw ex;
                   }
                   staticOrInstanceClass = parentClass.cast(staticOrInstanceClass);
               }
           }            
       }
       
       boolean isAccessible = method.isAccessible();
       try {
           // Allow call regardless of access scope (public/protected/private)
           method.setAccessible(true);
           if (classTypes.length == 1 && classTypes[0].isArray()) {
               returnValue = method.invoke(staticOrInstanceClass, new Object[]{parameters});
           } else {
               returnValue = method.invoke(staticOrInstanceClass, parameters);                
           }
       } finally {
           method.setAccessible(isAccessible);
       }
       
       return (T)returnValue;        
   }        

   /*----------------------------------------------------------------------------*/
   
   /**
    * Remove a directory and all of its contents
    * @param directory to be removed
    * @return true if the directory and its contents were removed
    */
   public static boolean removeDirectory(File directory) {
       if (directory == null || !directory.isDirectory()) {
         return false;
       }
       
       if (!directory.exists()) {
         return true;
       }
       
       String[] list = directory.list();
       
       if (list != null) {
           for (int i = 0; i < list.length; i++) {
               File entry = new File(directory, list[i]);
               if (entry.isDirectory()) {
                   if (!removeDirectory(entry)) {
                       return false;
                   }
               } else {
                   if (!entry.delete()) {
                       return false;
                   }
               }
           }
       }

       return directory.delete();
     }
   
}

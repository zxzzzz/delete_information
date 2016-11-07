// IPackageDataObserver.aidl
package android.content.pm;
import android.content.pm.PackageStats;
//import com.example.zx.clearcache.PackageStats;
// Declare any non-default types here with import statements

interface IPackageStatsObserver {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
   // void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat,
     //       double aDouble, String aString);
   oneway void onGetStatsCompleted(in PackageStats stats,boolean success);
}

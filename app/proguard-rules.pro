# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile
#------------The obfuscation configuration that must be introduced starts------------
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile
-allowaccessmodification

#------------Change to your own package name------------
-repackageclasses 'com.magnifyingglass.magnifiercamera'

-obfuscationdictionary dict.txt
-classobfuscationdictionary dict.txt
-packageobfuscationdictionary dict.txt

# Do not confuse resource classes. Search for **.R$* and remove other keep statements about R files. ------------
-keep class !com.common.clean.**,**.R$* { *; }

-optimizationpasses 5  # Specify the compression level of the code
-dontusemixedcaseclassnames   # Whether to use mixed case
-dontskipnonpubliclibraryclasses  # Specifies not to ignore non-public library classes
#-dontoptimize   #Optimize    Do not optimize input class files
-dontpreverify   # Whether to perform pre-verification during obfuscation
-verbose  # Whether to record logs during obfuscation
#-keepattributes SourceFile,LineNumberTable  #Keep the number of lines of code when an exception is thrown
-ignorewarnings  #Ignore warnings
-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*  # The algorithm used for obfuscation

#Protection Annotation
-keepattributes *Annotation*

#Avoid confusing generics. If confusion occurs, it is recommended to turn it off.
-keepattributes Signature

#Protecting exception classes and inner classes
-keepattributes Exceptions,InnerClasses


#Close Logging
-assumenosideeffects class android.util.Log {
     public static boolean isLoggable(java.lang.String,int);
     public static int v(...);
     public static int i(...);
     public static int w(...);
     public static int d(...);
     public static int e(...);
 }
#------------The obfuscation configuration that must be introduced ends------------

#------------The confusion introduced by the APP begins------------



-keepclasseswithmembers class * extends androidx.viewbinding.ViewBinding { *; }
-keep class com.magnifyingglass.magnifiercamera.bean.**{*;}



#------------The obfuscation introduced by APP ends------------

#------------Basic obfuscation configuration------------
#Do not confuse files under the v4 package -dontwarn Do not terminate if there is a warning
-dontwarn android.support.v4.**
-keep class android.support.v4.app.**{*;}
-keep class android.support.v4.** { *; }
-keep interface android.support.v4.app.** { *; }
-keep public class * extends android.support.v4.**
-keep public class * extends android.app.Fragment  #Don't confuse all subclasses of fragment
-keep public class * extends android.app.Activity  #Don't confuse all activity subclasses
-keep public class * extends android.app.Application #Same usage as above
-keep public class * extends android.app.Service #Same usage as above
-keep public class * extends android.content.BroadcastReceiver #Same usage as above
-keep public class * extends android.content.ContentProvider #Same usage as above
-keep public class * extends android.app.backup.BackupAgentHelper #Same usage as above
-keep public class * extends android.preference.Preference #Same usage as above
-keep public class * extends android.view.View
-keep public class com.android.vending.licensing.ILicensingService

#Keep native methods from being obfuscated
-keepclasseswithmembernames class * {
    native <methods>;
}

#Keep custom control classes from being obfuscated
-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet);
}

#Keep custom control classes from being obfuscated
-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet, int);
}

#Keep the method of specifying rules from being confused (the onClick method configured for the control in the Android layout file cannot be confused)
-keepclassmembers class * extends android.app.Activity {
   public void *(android.view.View);
}

#Keep the method of specifying rules for custom controls from being confused
-keep public class * extends android.view.View {
    public <init>(android.content.Context);
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet, int);
    public void set*(...);
    *** get*();
}

-keepclassmembers enum * { #Keep enum classes from being obfuscated
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

#Keep Parcelable from being obfuscated
-keep class * implements android.os.Parcelable {
  public static final android.os.Parcelable$Creator *;
}

#Classes that need to be serialized and deserialized cannot be confused (Note: Classes used for Java reflection cannot be confused either)
-keepnames class * implements java.io.Serializable

#Protect the class members of the specified rules in the class that implements the Serializable interface from being confused
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    !static !transient <fields>;
    !private <fields>;
    !private <methods>;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}

-keepclassmembers class * { # For callback function onXXEvent not to be confused
    void *(**On*Event);
}

#webview + js
#Keep declaring @JavascriptInterface
-keepattributes *JavascriptInterface*


#If your project uses complex operations of webview, it is best to add
-keepclassmembers class * extends android.webkit.WebViewClient {
  public void *(android.webkit.WebView,java.lang.String,android.graphics.Bitmap);
  public boolean *(android.webkit.WebView,java.lang.String);
}
-keepclassmembers class * extends android.webkit.WebChromeClient {
  public void *(android.webkit.WebView,java.lang.String);
}

#okhttp
-dontwarn okhttp3.**
-keep class okhttp3.**{*;}

#okio
-dontwarn okio.**
-keep class okio.**{*;}


# eventbus
-keepattributes *Annotation*
-keepclassmembers class * {
    @org.greenrobot.eventbus.Subscribe <methods>;
}
-keep enum org.greenrobot.eventbus.ThreadMode { *; }
# Only required if you use AsyncExecutor
-keepclassmembers class * extends org.greenrobot.eventbus.util.ThrowableFailureEvent {
    <init>(java.lang.Throwable);
}

##Glide
-dontwarn com.bumptech.glide.**
-keep class com.bumptech.glide.**{*;}
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep public class * extends com.bumptech.glide.AppGlideModule
-keep public enum com.bumptech.glide.load.resource.bitmap.ImageHeaderParser$** {
  **[] $VALUES;
  public *;
}

#butterknife
-keep class butterknife.** { *; }
-dontwarn butterknife.internal.**
-keep class **$$ViewBinder { *; }

-keepclasseswithmembernames class * {
    @butterknife.* <fields>;
}

-keepclasseswithmembernames class * {
    @butterknife.* <methods>;
}

#GSON
-keep class com.google.gson.** { *; }
-keep class com.google.gson.JsonObject {*;}
-keep class org.json.** {*;}
-keep class com.google.** {*;}

#------------Basic obfuscation configuration End------------
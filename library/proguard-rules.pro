# Add projectEntity specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your projectEntity uses WebView with JS, uncomment the following
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

-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.SerializationKt
-keep,includedescriptorclasses class com.merseyside.dictionaryapp.**$$serializer { *; }
-keepclassmembers class com.merseyside.mvvmcleanarch.** {
    *** Companion;
}
-keepclasseswithmembers class com.merseyside.mvvmcleanarch.** {
    kotlinx.serialization.KSerializer serializer(...);
}

-keepnames class kotlinx.** { *; }

-keep class org.apache.http.** {
    *;
}

-keepattributes SourceFile,LineNumberTable
-keep public class * extends java.lang.Exception

-optimizationpasses 5
-dontusemixedcaseclassnames
-dontskipnonpubliclibraryclasses
-dontpreverify
-verbose
-optimizations !code/simplification/arithmetic,!field
-ignorewarnings
